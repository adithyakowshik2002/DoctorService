package com.hospital.doctor.dto;

import com.hospital.doctor.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {
    private Long id;
    private String email;
    private String password;
    private Role role;

}
