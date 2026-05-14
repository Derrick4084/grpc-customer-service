package com.derocode.customer_grpc_server.service;


import com.derocode.customer.*;
import com.derocode.customer_grpc_server.configs.ServerInterceptorConfig;
import com.derocode.customer_grpc_server.mapper.LombokMapperImpl;
import com.derocode.customer_grpc_server.model.Customer;
import com.derocode.customer_grpc_server.model.DatabaseSequence;
import com.derocode.customer_grpc_server.repository.CustomerMongoRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.grpc.server.service.GrpcService;
import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@GrpcService(interceptors = ServerInterceptorConfig.class)
@RequiredArgsConstructor
public class CustomerServiceImpl extends CustomerServiceGrpc.CustomerServiceImplBase {


    private final LombokMapperImpl lombokMapper;
    private final MongoOperations mongoOperations;
    private final CustomerMongoRepository customerMongoRepository;

    public long generateSequence(String seqName) {
        DatabaseSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }

    @Override
    public void getCustomerById(@NonNull CustomerRequestById request, StreamObserver<CustomerResponse> responseObserver) {
        Optional<Customer> customerEntity = customerMongoRepository.findById(request.getId());
        if (customerEntity.isPresent()) {
            Customer customer = customerEntity.get();
            CustomerResponse customerResponse = lombokMapper.toResponse(customer);
            responseObserver.onNext(customerResponse);
            responseObserver.onCompleted();
        }
        else {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Customer with id " + request.getId() + " not found")
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getCustomerByEmail(CustomerRequestByEmail request, StreamObserver<CustomerResponse> responseObserver) {
        Optional<Customer> customerEntity = customerMongoRepository.findByEmail(request.getEmail());
        if (customerEntity.isPresent()) {
            Customer customer = customerEntity.get();
            CustomerResponse customerResponse = lombokMapper.toResponse(customer);
            responseObserver.onNext(customerResponse);
            responseObserver.onCompleted();
        }
        else {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Customer with " + request.getEmail() + " already exists")
                            .asRuntimeException()
            );

        }
    }

    @Override
    public void addCustomer(AddCustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        Customer customer = lombokMapper.toCustomer(request);
        customer.setId(generateSequence(Customer.SEQUENCE_NAME));
        Customer savedCustomer = null;
        try {
            savedCustomer = customerMongoRepository.save(customer);
            CustomerResponse response = lombokMapper.toResponse(savedCustomer);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch(IllegalArgumentException | OptimisticLockingFailureException e){
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription("Customer with " + request.getEmail() + " already exists")
                            .asRuntimeException()
            );

        }
    }

    @Override
    public void getAllCustomers(Empty request, StreamObserver<CustomerResponseList> responseObserver) {
        List<CustomerResponse> customers = customerMongoRepository.findAll()
                .stream()
                .map(lombokMapper::toResponse)
                .toList();
        CustomerResponseList.Builder responseList = CustomerResponseList.newBuilder();
        for (CustomerResponse response : customers) {
            responseList.addCustomerResponse(response);
        }
        responseObserver.onNext(responseList.build());
        responseObserver.onCompleted();
    }


}
