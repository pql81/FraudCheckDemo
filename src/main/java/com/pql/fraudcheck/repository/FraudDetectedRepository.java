package com.pql.fraudcheck.repository;

import com.pql.fraudcheck.domain.FraudDetected;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by pasqualericupero on 06/05/2021.
 */
@Repository
public interface FraudDetectedRepository extends MongoRepository<FraudDetected, Long> {

    Optional<FraudDetected> findByRequestId(String requestId);
}
