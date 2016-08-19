package com.monitise.performhance.services;

import com.monitise.performhance.api.model.ResponseCode;
import com.monitise.performhance.api.model.Role;
import com.monitise.performhance.entity.Organization;
import com.monitise.performhance.entity.Team;
import com.monitise.performhance.entity.User;
import com.monitise.performhance.exceptions.BaseException;
import com.monitise.performhance.helpers.SecurityHelper;
import com.monitise.performhance.repositories.TeamRepository;
import com.monitise.performhance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    public static final String UNDEFINED = "c8e7279cd035b23bb9c0f1f954dff5b3";

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private SecurityHelper securityHelper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationService organizationService;

    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    public Team get(int id) throws BaseException {
        Team team = teamRepository.findOne(id);
        if (team == null) {
            throw new BaseException(ResponseCode.TEAM_ID_DOES_NOT_EXIST, "A team with given ID does not exist.");
        }
        return team;
    }

    public List<Team> searchTeams(int organizationId, String teamName) {
        Specification<Team> filter = Team.organizationIdIs(organizationId);
        if (!UNDEFINED.equals(teamName)) {
            filter = Specifications.where(filter).and(Team.teamNameContains(teamName));
        }
        return teamRepository.findAll(filter);
    }

    public void deleteTeam(int teamId) throws BaseException {
        ensureExistence(teamId);
        removeLeadershipFromTeam(teamId);
        removeAllEmployeesFromTeam(teamId);
        removeTeamFromOrganization(teamId);
        teamRepository.delete(teamId);
    }

    public List<Team> getListFilterByOrganizationId(int organizationId) throws BaseException {
        return teamRepository.findByOrganizationId(organizationId);
    }

    public Team add(Team team) throws BaseException {
        int organizationId = team.getOrganization().getId();
        securityHelper.checkAuthentication(organizationId);
        Team teamFromRepo = teamRepository.save(team);

        if (teamFromRepo == null) {
            throw new BaseException(ResponseCode.UNEXPECTED, "Could not add given team.");
        }
        organizationService.addTeam(organizationId, teamFromRepo);
        return teamFromRepo;
    }

    public Team assignEmployeeToTeam(int userId, int teamId) throws BaseException {
        Team team = teamRepository.findOne(teamId);
        User user = userRepository.findOne(userId);

        team.getMembers().add(user);
        Team updatedTeam = teamRepository.save(team);
        user.setTeam(team);
        User userFromRepo = userRepository.save(user);

        if (updatedTeam == null || userFromRepo == null) {
            throw new BaseException(ResponseCode.UNEXPECTED, "Could not assign given employee to the given team.");
        }
        return updatedTeam;
    }

    public Team removeEmployeeFromTeam(int employeeId, int teamId) throws BaseException {
        Team team = teamRepository.findOne(teamId);
        User employee = userRepository.findOne(employeeId);
        team.getMembers().remove(employee);
        employee.setTeam(null);

        Team updatedTeam = teamRepository.save(team);
        User userFromRepo = userRepository.save(employee);
        if (updatedTeam == null || userFromRepo == null) {
            throw new BaseException(ResponseCode.UNEXPECTED, "Could not remove given employee from given team.");
        }
        return updatedTeam;
    }

    public Team assignLeaderToTeam(int leaderId, int teamId) throws BaseException {
        if (!isLeaderAMemberOfTheTeam(teamId, leaderId)) {
            assignEmployeeToTeam(leaderId, teamId);
        }
        Team team = teamRepository.findOne(teamId);
        User leader = userRepository.findOne(leaderId);
        team.setLeader(leader);
        leader.setRole(Role.TEAM_LEADER);
        User userFromRepo = userRepository.save(leader);
        Team updatedTeam = teamRepository.save(team);
        if (updatedTeam == null || userFromRepo == null) {
            throw new BaseException(ResponseCode.UNEXPECTED, "Could not assign given user as given team's leader.");
        }
        return updatedTeam;
    }

    // Leader stays in the team, only his/her leadership is removed.
    public Team removeLeadershipFromTeam(int teamId) throws BaseException {
        Team team = teamRepository.findOne(teamId);
        User leader = team.getLeader();
        team.setLeader(null);
        leader.setRole(Role.EMPLOYEE);
        Team updatedTeam = teamRepository.save(team);
        User userFromRepo = userRepository.save(leader);
        if (userFromRepo == null || updatedTeam == null) {
            throw new BaseException(ResponseCode.UNEXPECTED, "Could not remove given team's leader.");
        }
        return updatedTeam;
    }

    public void ensureTeamHasLeader(int teamId) throws BaseException {
        Team team = teamRepository.findOne(teamId);
        if (team.getLeader() == null) {
            throw new BaseException(ResponseCode.TEAM_HAS_NO_LEADER,
                    "Given team has no leader");
        }
    }

    // region Helper Methods

    private boolean isLeaderAMemberOfTheTeam(int teamId, int leaderId) {
        Team team = teamRepository.findOne(teamId);
        User leader = userRepository.findOne(leaderId);
        List<User> members = team.getMembers();
        for (User member : members) {
            if (member.getId() == leader.getId()) {
                return true;
            }
        }
        return false;
    }

    private void ensureExistence(int teamId) throws BaseException {
        Team team = teamRepository.findOne(teamId);
        if (team == null) {
            throw new BaseException(ResponseCode.TEAM_ID_DOES_NOT_EXIST, "A team with given ID does not exist.");
        }
    }

    private void removeAllEmployeesFromTeam(int teamId) throws BaseException {
        List<User> employees = new ArrayList<>(get(teamId).getMembers());
        for (User employee : employees) {
            removeEmployeeFromTeam(employee.getId(), teamId);
        }
    }

    private void removeTeamFromOrganization(int teamId) throws BaseException {
        Team team = get(teamId);
        Organization organization = team.getOrganization();
        organization.getTeams().remove(team);
        organizationService.update(organization);
    }

    // endregion

}
