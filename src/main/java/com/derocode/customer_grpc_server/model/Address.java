package com.derocode.customer_grpc_server.model;


import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Address {

    private String street;
    private String houseNumber;
    private Long zipCode;

}
