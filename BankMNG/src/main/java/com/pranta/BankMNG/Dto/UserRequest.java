package com.pranta.BankMNG.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private Long id;
    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String stateOrigin;
    private String accoutNumber;
    private String email;
    private String  phonenumber;
    private String alternativePhoneNumber;

}
