package dev.winclip.vetclinic.pet.dto;

import java.time.Instant;
import java.time.LocalDate;

import dev.winclip.vetclinic.pet.Pet;
import dev.winclip.vetclinic.pet.PetSex;
import dev.winclip.vetclinic.pet.PetSpecies;

public record PetResponse(
		Long id,
		String name,
		PetSpecies species,
		String breed,
		PetSex sex,
		LocalDate dateOfBirth,
		String notes,
		Instant createdAt) {

	public static PetResponse from(Pet p) {
		return new PetResponse(
				p.getId(),
				p.getName(),
				p.getSpecies(),
				p.getBreed(),
				p.getSex(),
				p.getDateOfBirth(),
				p.getNotes(),
				p.getCreatedAt());
	}
}
