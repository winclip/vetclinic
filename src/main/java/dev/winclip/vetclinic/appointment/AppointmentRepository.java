package dev.winclip.vetclinic.appointment;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	@Query("""
			select case when count(a) > 0 then true else false end
			from Appointment a
			where a.doctor.id = :doctorId
				and a.status = :status
				and a.startsAt < :rangeEnd
				and a.endsAt > :rangeStart
			""")
	boolean existsOverlapForDoctor(
			@Param("doctorId") Long doctorId,
			@Param("status") AppointmentStatus status,
			@Param("rangeStart") Instant rangeStart,
			@Param("rangeEnd") Instant rangeEnd);

	List<Appointment> findByPet_IdOrderByStartsAtDesc(Long petId);
}
