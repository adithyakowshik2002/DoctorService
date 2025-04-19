package com.hospital.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedSlotResponse {
    private Long bookedSlotId;
    private String slotDate;
    private LocalTime slotStartTime;
    private LocalTime slotEndTime;
    private boolean isBooked;


}
