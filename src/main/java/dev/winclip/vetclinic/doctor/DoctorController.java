package dev.winclip.vetclinic.doctor;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import dev.winclip.vetclinic.doctor.dto.DoctorCreateRequest;
import dev.winclip.vetclinic.doctor.dto.DoctorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctors")
@Tag(name = "doctors", description = "Doctors directory")
@RequiredArgsConstructor
public class DoctorController {

	private static final int DEFAULT_SIZE = 20;
	private static final int MAX_SIZE = 100;

	private final DoctorService doctorService;

	@GetMapping
	@Operation(summary = "List doctors", description = "Returns a paged list of active doctors.")
	public PagedResponse<DoctorResponse> list(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int size) {
		int safePage = Math.max(1, page);
		int safeSize = (size <= 0) ? DEFAULT_SIZE : Math.min(MAX_SIZE, size);

		int internalPage = safePage - 1;
		Pageable pageable = PageRequest.of(internalPage, safeSize, Sort.by(Sort.Direction.ASC, "lastName", "firstName"));
		Page<DoctorResponse> result = doctorService.findAllActive(pageable);
		PagedResponse.Info info = new PagedResponse.Info(result.getTotalElements(), result.getTotalPages(),
				result.getNumber() + 1, result.getSize());
		return new PagedResponse<>(info, result.getContent());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get doctor", description = "Returns a single doctor by id.")
	public DoctorResponse getById(@PathVariable Long id) {
		return doctorService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Create doctor", description = "Creates a new doctor (ADMIN only).")
	public DoctorResponse create(@Valid @RequestBody DoctorCreateRequest request) {
		return doctorService.create(request);
	}

	@PutMapping("/{id}")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Update doctor", description = "Updates a doctor (ADMIN only).")
	public DoctorResponse update(@PathVariable Long id, @Valid @RequestBody DoctorCreateRequest request) {
		return doctorService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Delete doctor", description = "Soft-deletes a doctor (ADMIN only).")
	public void softDelete(@PathVariable Long id) {
		doctorService.softDelete(id);
	}
}
