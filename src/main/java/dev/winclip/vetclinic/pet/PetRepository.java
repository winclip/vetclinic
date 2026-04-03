package dev.winclip.vetclinic.pet;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

	Page<Pet> findAllByOwnerIdAndActiveTrue(Long ownerId, Pageable pageable);

	Optional<Pet> findByIdAndOwnerIdAndActiveTrue(Long id, Long ownerId);

	Page<Pet> findAllByOrderByCreatedAtDesc(Pageable pageable);

	Page<Pet> findAllByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

	Page<Pet> findAllByActiveFalseOrderByCreatedAtDesc(Pageable pageable);
}
