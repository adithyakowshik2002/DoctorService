package com.hospital.doctor.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * POST /booked‑slots
 * { "scheduleId": 5, "slotDate": "2025-04-21", "slotStartTime": "09:00", "slotEndTime": "09:15" }
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookedSlotRequest {
    private Long scheduleId;
    private String slotDate;
    private LocalTime slotStartTime;
    private LocalTime slotEndTime;
    private Boolean isBooked;
}



