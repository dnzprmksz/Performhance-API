package com.monitise.performhance.repositories;

import com.monitise.performhance.entity.JobTitle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobTitleRepository extends CrudRepository<JobTitle, Integer> {

    @Override
    List<JobTitle> findAll();

    JobTitle findByTitleAndOrganizationId(String title, int organizationId);

}
