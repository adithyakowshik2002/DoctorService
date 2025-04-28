package com.hospital.doctor.dto;

import lombok.Data;

@Data
public class SlotBookedDTO {

    private String slotDate;
    private String slotStartTime;
  private Long patientId;
  private Long scheduleId;
    private String slotEndTime;
    private String status;
}
