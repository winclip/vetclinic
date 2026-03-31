package dev.winclip.vetclinic.pet;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

	List<Pet> findAllByOwnerIdAndActiveTrueOrderByCreatedAtDesc(Long ownerId);

	Optional<Pet> findByIdAndOwnerIdAndActiveTrue(Long id, Long ownerId);

	List<Pet> findAllByOrderByCreatedAtDesc();

	List<Pet> findAllByActiveTrueOrderByCreatedAtDesc();

	List<Pet> findAllByActiveFalseOrderByCreatedAtDesc();
}
