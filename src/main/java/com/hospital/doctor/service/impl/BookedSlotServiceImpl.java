package com.hospital.doctor.service.impl;


import com.hospital.doctor.DoctorMapper.BookedSlotMapper;
import com.hospital.doctor.dto.BookedSlotDTO;
import com.hospital.doctor.dto.SlotBookedDTO;
import com.hospital.doctor.dto.SlotTimeDto;
import com.hospital.doctor.entity.AvailableScheduleEntity;
import com.hospital.doctor.entity.BookedSlotEntity;
import com.hospital.doctor.repository.AvailableScheduleRepository;
import com.hospital.doctor.repository.BookedSlotRepository;
import com.hospital.doctor.service.BookedSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
class BookedSlotServiceImpl implements BookedSlotService {

    private final BookedSlotMapper bookedSlotMapper;

private final AvailableScheduleRepository availableScheduleRepository;
    private final BookedSlotRepository bookedSlotRepository;

    @Override
    public List<BookedSlotDTO> fetchBookedSlots(Long doctorId, LocalDate date) {
        List<BookedSlotEntity> bookedSlots = bookedSlotRepository.findByDoctorIdAndSlotDate(doctorId, date);

        return bookedSlotMapper.toResponse(bookedSlots).stream()
                .map(slot -> new BookedSlotDTO(
                        slot.getSlotDate(),
                        slot.getSlotStartTime(),
                        slot.getSlotEndTime(),
                        slot.getStatus()
                ))
                .collect(Collectors.toList());

    }


    @Override
    public List<SlotTimeDto> fetchAvailableSlotTimings(Long doctorId, LocalDate date) {

        // 1. Fetch all booked slots
        List<BookedSlotEntity> bookedSlots = bookedSlotRepository.findByDoctorIdAndSlotDate(doctorId, date);
        List<SlotTimeDto> bookedStartTimes = bookedSlots.stream()
                .map(s->new SlotTimeDto(s.getSlotStartTime().toString()))
                .toList();

        // 2. Fetch all schedules for the given date
        List<AvailableScheduleEntity> schedules = availableScheduleRepository
                .findByDoctorIdAndAvailableDate(doctorId, date);

        List<SlotTimeDto> availableSlots = new ArrayList<>();

        for (AvailableScheduleEntity schedule : schedules) {
            LocalTime current = schedule.getAvailableFrom();
            LocalTime end = schedule.getAvailableTo();

            // Create slots in 15-minute intervals
            while (current.isBefore(end)) {
                if (!bookedStartTimes.contains(current)) {
                    availableSlots.add(new SlotTimeDto(current.toString()));
                }
                current = current.plusMinutes(15);
            }
        }

        return availableSlots;
    }

    @Override
    public void bookSlot(SlotBookedDTO request) {

        AvailableScheduleEntity schedule = availableScheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found for ID: " + request.getScheduleId()));

        // 1. Duplicate check
        boolean alreadyBooked = bookedSlotRepository.existsByAvailableScheduleEntityAndSlotDateAndSlotStartTime(
                schedule,
                LocalDate.parse(request.getSlotDate()),
                LocalTime.parse(request.getSlotStartTime())
        );

        if (alreadyBooked) {
            throw new IllegalStateException("This slot is already booked. Please choose another time.");
        }

        // 2. Save booking
        BookedSlotEntity bookedSlot = BookedSlotEntity.builder()
                .availableScheduleEntity(schedule)
                .slotDate(LocalDate.parse(request.getSlotDate()))
                .slotStartTime(LocalTime.parse(request.getSlotStartTime()))
                .slotEndTime(LocalTime.parse(request.getSlotStartTime()).plusMinutes(15))  // 15 minutes slot
                .patientId(request.getPatientId())
                .build();

        bookedSlotRepository.save(bookedSlot);
    }



}