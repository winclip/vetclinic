package dev.winclip.vetclinic.pet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.api.PagedResponse;
import dev.winclip.vetclinic.pet.dto.AdminPetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/admin/pets")
@RequiredArgsConstructor
public class AdminPetController {

	private final PetService petService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public PagedResponse<AdminPetResponse> listAll(
			@RequestParam(required = false) Boolean active,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int size) {
		int internalPage = Math.max(0, page - 1);
		Pageable pageable = PageRequest.of(internalPage, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<AdminPetResponse> result = petService.listPetsForAdmin(active, pageable);
		PagedResponse.Info info = new PagedResponse.Info(result.getTotalElements(), result.getTotalPages(),
				result.getNumber() + 1, result.getSize());
		return new PagedResponse<>(info, result.getContent());
	}

	@PostMapping("/{id}/restore")
	@PreAuthorize("hasRole('ADMIN')")
	public AdminPetResponse restore(@PathVariable Long id) {
		return petService.restorePetForAdmin(id);
	}
}

