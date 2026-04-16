package com.derocode.customer_grpc_server.model;


import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Address {
    private String houseNumber;
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
