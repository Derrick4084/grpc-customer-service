package com.derocode.customer_grpc_server.repository;

import com.derocode.customer_grpc_server.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface CustomerMongoRepository extends MongoRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);
}
