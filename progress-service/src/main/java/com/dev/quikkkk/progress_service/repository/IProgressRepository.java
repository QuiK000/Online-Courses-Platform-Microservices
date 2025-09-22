package com.dev.quikkkk.progress_service.repository;

import com.dev.quikkkk.progress_service.document.Progress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProgressRepository extends MongoRepository<Progress, String> {
}
