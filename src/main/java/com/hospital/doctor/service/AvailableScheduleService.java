package com.hospital.doctor.service;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AvailableScheduleService {
    Long findScheduleIdByDoctorDateTime(Long doctorId, LocalDate date, LocalTime  startTime);
}
