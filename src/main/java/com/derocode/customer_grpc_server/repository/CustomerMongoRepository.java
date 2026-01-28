package com.derocode.customer_grpc_server.repository;

import com.derocode.customer_grpc_server.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CustomerMongoRepository extends MongoRepository<Customer, String> {
}
