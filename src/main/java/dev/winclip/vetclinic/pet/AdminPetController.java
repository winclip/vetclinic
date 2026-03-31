package dev.winclip.vetclinic.pet;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.pet.dto.AdminPetResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/pets")
@RequiredArgsConstructor
public class AdminPetController {

	private final PetService petService;

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<AdminPetResponse> listAll(@RequestParam(required = false) Boolean active) {
		return petService.listPetsForAdmin(active);
	}
}

