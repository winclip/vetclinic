package dev.winclip.vetclinic.pet.dto;

import java.time.LocalDate;

import dev.winclip.vetclinic.pet.PetSex;
import dev.winclip.vetclinic.pet.PetSpecies;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record PetCreateRequest(
		@NotBlank(message = "Name is required")
		@Size(max = 100, message = "Name must be at most 100 characters long")
		@Schema(example = "Rex") String name,

		@NotNull(message = "Species is required")
		@Schema(example = "DOG") PetSpecies species,

		@Size(max = 100, message = "Breed must be at most 100 characters long")
		@Schema(example = "Beagle") String breed,

		@Schema(example = "FEMALE") PetSex sex,

		@Past(message = "Date of birth must be in the past")
		@Schema(example = "2014-01-15") LocalDate dateOfBirth,

		@Size(max = 2000, message = "Notes must be at most 2000 characters long")
		@Schema(example = "No special notes") String notes) {
}
