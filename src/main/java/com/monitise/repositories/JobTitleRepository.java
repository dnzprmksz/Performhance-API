package com.monitise.repositories;

import com.monitise.entity.JobTitle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTitleRepository extends CrudRepository<JobTitle, Integer> {

}
