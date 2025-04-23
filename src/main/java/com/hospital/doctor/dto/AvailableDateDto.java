package com.hospital.doctor.dto;

import com.hospital.doctor.entity.AvailableScheduleEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableDateDto {

   // private Long id;
    private String availableDate;

    private List<AvailableScheduleDto> schedule;
   // private Long doctorId;
}
