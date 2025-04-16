package com.example.salooniveryvells.Dto;
import com.example.salooniveryvells.Enum.UserRole;
import lombok.Data;


@Data
public class UserDTO {
    private int userId;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private String phoneNumber;
    private String address;
    private String serviceArea;
    private String adminLevel;

    private String verificationStatus;
    private String status;

    private String idProofPath;
    private String addressProofPath;

}
