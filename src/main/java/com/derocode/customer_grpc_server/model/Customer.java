package com.derocode.customer_grpc_server.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "customers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Customer {

    @Transient
    public static final String SEQUENCE_NAME = "customer_sequence";

    @Id
    private Long id;

    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    private String email;

    private List<Address> addresses;
}
