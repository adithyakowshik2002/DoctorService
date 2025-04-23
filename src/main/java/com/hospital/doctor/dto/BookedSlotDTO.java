package com.hospital.doctor.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookedSlotDTO
{
    private String slotDate;
    private String slotStartTime;

    private String slotEndTime;
    private String status;
}



