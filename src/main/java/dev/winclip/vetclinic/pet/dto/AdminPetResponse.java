package dev.winclip.vetclinic.pet.dto;

import java.time.Instant;
import java.time.LocalDate;

import dev.winclip.vetclinic.pet.Pet;
import dev.winclip.vetclinic.pet.PetSex;
import dev.winclip.vetclinic.pet.PetSpecies;

public record AdminPetResponse(
		Long id,
		String ownerUsername,
		String name,
		PetSpecies species,
		String breed,
		PetSex sex,
		LocalDate dateOfBirth,
		boolean active,
		Instant createdAt) {

	public static AdminPetResponse from(Pet p) {
		return new AdminPetResponse(
				p.getId(),
				p.getOwner().getUsername(),
				p.getName(),
				p.getSpecies(),
				p.getBreed(),
				p.getSex(),
				p.getDateOfBirth(),
				p.isActive(),
				p.getCreatedAt());
	}
}

