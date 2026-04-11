package dev.winclip.vetclinic.appointment;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
				and (:excludeId is null or a.id <> :excludeId)
			""")
	boolean existsOverlapForDoctor(
			@Param("doctorId") Long doctorId,
			@Param("status") AppointmentStatus status,
			@Param("rangeStart") Instant rangeStart,
			@Param("rangeEnd") Instant rangeEnd,
			@Param("excludeId") Long excludeAppointmentId);

	@Query("""
			select a from Appointment a
				join fetch a.pet p
				join fetch p.owner
				join fetch a.doctor
			where a.id = :id
			""")
	Optional<Appointment> findWithPetOwnerAndDoctorById(@Param("id") Long id);

	List<Appointment> findByPet_IdOrderByStartsAtDesc(Long petId);

	@Query("""
			select a from Appointment a
				join fetch a.doctor
				join fetch a.pet
			where a.pet.owner.id = :ownerId
			order by a.startsAt desc
			""")
	List<Appointment> findAllForOwnerOrderByStartsAtDesc(@Param("ownerId") Long ownerId);
}
