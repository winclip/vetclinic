package dev.winclip.vetclinic.pet;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.api.PagedResponse;
import dev.winclip.vetclinic.pet.dto.PetCreateRequest;
import dev.winclip.vetclinic.pet.dto.PetResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/pets/me")
@RequiredArgsConstructor
public class PetController {

	private static final int DEFAULT_SIZE = 20;
	private static final int MAX_SIZE = 100;

	private final PetService petService;

	@GetMapping
	@SecurityRequirement(name = "bearerAuth")
	public PagedResponse<PetResponse> list(
			@AuthenticationPrincipal String username,
			@RequestParam(defaultValue = "1") int pageNumber,
			@RequestParam(defaultValue = "20") int size) {
		int safePage = Math.max(1, pageNumber);
		int safeSize = (size <= 0) ? DEFAULT_SIZE : Math.min(MAX_SIZE, size);

		int internalPage = safePage - 1;
		Pageable pageable = PageRequest.of(internalPage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<PetResponse> result = petService.listMyPets(username, pageable);
		PagedResponse.Info info = new PagedResponse.Info(result.getTotalElements(), result.getTotalPages(),
				result.getNumber() + 1, result.getSize());
		return new PagedResponse<>(info, result.getContent());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@SecurityRequirement(name = "bearerAuth")
	public PetResponse create(@AuthenticationPrincipal String username, @Valid @RequestBody PetCreateRequest request) {
		return petService.createMyPet(username, request);
	}

	@GetMapping("/{id}")
	@SecurityRequirement(name = "bearerAuth")
	public PetResponse getById(@AuthenticationPrincipal String username, @PathVariable Long id) {
		return petService.getMyPet(username, id);
	}

	@PutMapping("/{id}")
	@SecurityRequirement(name = "bearerAuth")
	public PetResponse update(
			@AuthenticationPrincipal String username,
			@PathVariable Long id,
			@Valid @RequestBody PetCreateRequest request) {
		return petService.updateMyPet(username, id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityRequirement(name = "bearerAuth")
	public void delete(@AuthenticationPrincipal String username, @PathVariable Long id) {
		petService.deleteMyPet(username, id);
	}
}
