package dev.winclip.vetclinic.doctor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.winclip.vetclinic.doctor.dto.DoctorCreateRequest;
import dev.winclip.vetclinic.doctor.dto.DoctorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

	private final DoctorService doctorService;

	@GetMapping
	public List<DoctorResponse> list() {
		return doctorService.findAllActive();
	}

	@GetMapping("/{id}")
	public DoctorResponse getById(@PathVariable Long id) {
		return doctorService.getById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DoctorResponse create(@Valid @RequestBody DoctorCreateRequest request) {
		return doctorService.create(request);
	}

	@PutMapping("/{id}")
	public DoctorResponse update(@PathVariable Long id, @Valid @RequestBody DoctorCreateRequest request) {
		return doctorService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void softDelete(@PathVariable Long id) {
		doctorService.softDelete(id);
	}
}
