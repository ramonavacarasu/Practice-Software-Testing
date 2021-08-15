package com.amigoscode.testing.customer;

<<<<<<<<< Temporary merge branch 1
import javax.validation.constraints.NotBlank;
import java.lang.reflect.Constructor;
import java.util.UUID;

public class Customer {
=========
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
public class Customer {

    @Id
>>>>>>>>> Temporary merge branch 2
    private UUID id;

    @NotBlank
    private String name;
<<<<<<<<< Temporary merge branch 1
=========

    @NotBlank
>>>>>>>>> Temporary merge branch 2
    private String phoneNumber;

    public Customer(UUID id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

<<<<<<<<< Temporary merge branch 1
=========
    public Customer() {
    }

>>>>>>>>> Temporary merge branch 2
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
