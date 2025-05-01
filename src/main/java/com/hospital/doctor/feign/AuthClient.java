package com.hospital.doctor.feign;


import com.hospital.doctor.dto.AuthDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTHENTICATION-SERVICE")
public interface AuthClient {

    @GetMapping("/auth/get_user")
    public AuthDto getUserDetails(@RequestHeader("Authorization") String jwt);

}
