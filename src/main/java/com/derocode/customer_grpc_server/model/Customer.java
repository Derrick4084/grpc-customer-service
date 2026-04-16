package com.derocode.customer_grpc_server.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "customers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Customer {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Address> addresses;
}
