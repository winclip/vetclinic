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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

	private static final int DEFAULT_SIZE = 20;
	private static final int MAX_SIZE = 100;

	private final DoctorService doctorService;

	@GetMapping
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
	public DoctorResponse getById(@PathVariable Long id) {
		return doctorService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@SecurityRequirement(name = "bearerAuth")
	public DoctorResponse create(@Valid @RequestBody DoctorCreateRequest request) {
		return doctorService.create(request);
	}

	@PutMapping("/{id}")
	@SecurityRequirement(name = "bearerAuth")
	public DoctorResponse update(@PathVariable Long id, @Valid @RequestBody DoctorCreateRequest request) {
		return doctorService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityRequirement(name = "bearerAuth")
	public void softDelete(@PathVariable Long id) {
		doctorService.softDelete(id);
	}
}
