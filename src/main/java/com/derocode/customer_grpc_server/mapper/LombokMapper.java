package com.derocode.customer_grpc_server.mapper;

import com.derocode.customer.AddCustomerRequest;
import com.derocode.customer_grpc_server.model.Address;
import com.derocode.customer.CustomerResponse;
import com.derocode.customer.CustomerResponseAddress;
import com.derocode.customer_grpc_server.model.Customer;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LombokMapper {

    default Customer toCustomer(@NonNull AddCustomerRequest request) {
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        List<Address> addresses = request.getAddressesList().stream().map(
                addCustomerAddress -> new Address(
                        addCustomerAddress.getHouseNumber(),
                        addCustomerAddress.getStreet(),
                        addCustomerAddress.getCity(),
                        addCustomerAddress.getState(),
                        addCustomerAddress.getZipCode()
                )
        ).toList();
        customer.setAddresses(addresses);
        return customer;
    }

    default CustomerResponse toResponse(@NonNull Customer customer) {
        CustomerResponse.Builder builder = CustomerResponse.newBuilder()
                .setId(customer.getId())
                .setFirstName(customer.getFirstName())
                .setLastName(customer.getLastName())
                .setEmail(customer.getEmail());
        List<CustomerResponseAddress> addresses = customer.getAddresses()
                .stream().map(address -> CustomerResponseAddress
                        .newBuilder()
                        .setHouseNumber(address.getHouseNumber())
                        .setStreet(address.getStreet())
                        .setCity(address.getCity())
                        .setState(address.getState())
                        .setZipCode(address.getZipCode())
                        .build()
                )
                .toList();
        builder.addAllAddresses(addresses);
        return builder.build();
    }
}
