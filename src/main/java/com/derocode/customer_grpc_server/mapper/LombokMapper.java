package com.derocode.customer_grpc_server.mapper;


import com.derocode.customer.AddCustomerRequest;
import com.derocode.customer.CustomerResponse;
import com.derocode.customer_grpc_server.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LombokMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address.street", source = "street")
    @Mapping(target = "address.houseNumber", source = "houseNumber")
    @Mapping(target = "address.zipCode", source = "zipCode")
    Customer toEntity(AddCustomerRequest request);


    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "houseNumber", source = "address.houseNumber")
    @Mapping(target = "zipCode", source = "address.zipCode")
    CustomerResponse toResponse(Customer customer);
}
