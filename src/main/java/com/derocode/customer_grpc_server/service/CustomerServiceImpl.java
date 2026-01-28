package com.derocode.customer_grpc_server.service;


import com.derocode.customer.*;
import com.derocode.customer_grpc_server.configs.ServerInterceptorConfig;
import com.derocode.customer_grpc_server.model.Address;
import com.derocode.customer_grpc_server.model.Customer;
import com.derocode.customer_grpc_server.repository.CustomerMongoRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.math.BigInteger;
import java.util.Optional;

@GrpcService(interceptors = ServerInterceptorConfig.class)
@RequiredArgsConstructor
public class CustomerServiceImpl extends CustomerServiceGrpc.CustomerServiceImplBase {

    private final CustomerMongoRepository customerMongoRepository;

    @Override
    public void getCustomerById(CustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {

        Optional<Customer> customerEntity = customerMongoRepository.findById(request.getId());

        if (customerEntity.isPresent()){
            Customer customer = customerEntity.get();

            CustomerResponse customerResponse = CustomerResponse.newBuilder()
                    .setFirstName(customer.getFirstName())
                    .setLastName(customer.getLastName())
                    .setEmail(customer.getEmail())
                    .setAddress(CustomerAddress.newBuilder()
                            .setHouseNumber(customer.getAddress().getHouseNumber())
                            .setStreet(customer.getAddress().getStreet())
                            .setZipCode(customer.getAddress().getZipCode())
                            .build())
                    .build();
            responseObserver.onNext(customerResponse);
            responseObserver.onCompleted();
        }

    }

    @Override
    public void addCustomer(AddCustomerRequest request, StreamObserver<AddCustomerResponse> responseObserver) {

        Address custAddress = new Address();
        custAddress.setHouseNumber(request.getAddress().getHouseNumber());
        custAddress.setStreet(request.getAddress().getStreet());
        custAddress.setZipCode(request.getAddress().getZipCode());

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(custAddress)
                .build();

        Customer savedCustomer = customerMongoRepository.save(customer);

        AddCustomerResponse addCustomerResponse = AddCustomerResponse.newBuilder()
                .setId(savedCustomer.getId())
                .build();

        responseObserver.onNext(addCustomerResponse);
        responseObserver.onCompleted();
    }
}
