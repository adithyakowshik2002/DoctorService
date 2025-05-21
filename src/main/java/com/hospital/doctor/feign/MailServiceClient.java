package com.hospital.doctor.feign;

import com.hospital.doctor.dto.other.MailDtoClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "EMAIL-SERVICE")
public interface MailServiceClient {

    @PostMapping("/mail/reschedule")
    void sendRescheduleEmail(@RequestBody MailDtoClass mailDto);
}
