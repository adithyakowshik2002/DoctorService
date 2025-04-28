package com.hospital.doctor.repository;

import com.hospital.doctor.entity.AvailableScheduleEntity;
import com.hospital.doctor.entity.BookedSlotEntity;
import com.hospital.doctor.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookedSlotRepository extends JpaRepository<BookedSlotEntity, Long> {

    @Query("SELECT b FROM BookedSlotEntity b " +
            "JOIN b.availableScheduleEntity s " +
            "JOIN s.availableDate d " +
            "JOIN d.doctor doc " +
            "WHERE doc.id = :doctorId AND b.slotDate = :date")
    List<BookedSlotEntity> findByDoctorIdAndSlotDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);




    @Query("""
        SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
        FROM BookedSlotEntity b
        WHERE b.availableScheduleEntity = :schedule
          AND b.slotDate = :slotDate
          AND b.slotStartTime = :slotStartTime
    """)
    boolean existsByAvailableScheduleEntityAndSlotDateAndSlotStartTime(
            @Param("schedule") AvailableScheduleEntity schedule,
            @Param("slotDate") LocalDate slotDate,
            @Param("slotStartTime") LocalTime slotStartTime
    );
}
