package com.derocode.customer_grpc_server.service;


import com.derocode.customer.*;
import com.derocode.customer_grpc_server.configs.ServerInterceptorConfig;
import com.derocode.customer_grpc_server.mapper.LombokMapperImpl;
import com.derocode.customer_grpc_server.model.Customer;
import com.derocode.customer_grpc_server.repository.CustomerMongoRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;

@GrpcService(interceptors = ServerInterceptorConfig.class)
@RequiredArgsConstructor
public class CustomerServiceImpl extends CustomerServiceGrpc.CustomerServiceImplBase {

    private final CustomerMongoRepository customerMongoRepository;
    private final LombokMapperImpl lombokMapper;

    @Override
    public void getCustomerById(@NonNull CustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {

        Optional<Customer> customerEntity = customerMongoRepository.findById(request.getId());
        if (customerEntity.isPresent()){
            Customer customer = customerEntity.get();
            CustomerResponse customerResponse = lombokMapper.toResponse(customer);
            responseObserver.onNext(customerResponse);
            responseObserver.onCompleted();
        }

    }

    @Override
    public void addCustomer(AddCustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        Customer customer = lombokMapper.toEntity(request);
        Customer savedCustomer = customerMongoRepository.save(customer);
        CustomerResponse response = lombokMapper.toResponse(savedCustomer);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllCustomers(Empty request, StreamObserver<CustomerResponseList> responseObserver) {
        List<CustomerResponse> customers = customerMongoRepository.findAll().stream().map(lombokMapper::toResponse).toList();
        CustomerResponseList.Builder responseList = CustomerResponseList.newBuilder();
        while (customers.iterator().hasNext()) {
            CustomerResponse response = customers.getFirst();
            responseList.addCustomerResponse(response);
        }
        responseObserver.onNext(responseList.build());
        responseObserver.onCompleted();
    }
}
