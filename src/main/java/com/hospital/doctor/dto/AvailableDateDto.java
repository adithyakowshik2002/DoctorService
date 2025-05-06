package com.hospital.doctor.dto;

import com.hospital.doctor.entity.AvailableScheduleEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableDateDto {

   // private Long id;
    private LocalDate availableDate;

    private List<AvailableScheduleDto> schedule;
   // private Long doctorId;
}
