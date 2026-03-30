package dev.winclip.vetclinic.pet;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.pet.dto.PetCreateRequest;
import dev.winclip.vetclinic.pet.dto.PetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pets/me")
@RequiredArgsConstructor
public class PetController {

	private final PetService petService;

	@GetMapping
	public List<PetResponse> list(@AuthenticationPrincipal String username) {
		return petService.listMyPets(username);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PetResponse create(@AuthenticationPrincipal String username, @Valid @RequestBody PetCreateRequest request) {
		return petService.createMyPet(username, request);
	}
}
