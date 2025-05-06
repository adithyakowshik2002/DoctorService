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
    private LocalDate slotDate;
    private LocalDate slotStartTime;

    private LocalDate slotEndTime;
    private String status;
}



