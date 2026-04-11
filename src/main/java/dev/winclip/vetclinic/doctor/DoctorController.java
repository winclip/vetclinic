package dev.winclip.vetclinic.doctor;

import java.util.List;

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
import dev.winclip.vetclinic.error.ErrorResponse;
import dev.winclip.vetclinic.doctor.dto.DoctorCreateRequest;
import dev.winclip.vetclinic.doctor.dto.DoctorResponse;
import dev.winclip.vetclinic.doctor.dto.DoctorWorkingHoursReplaceRequest;
import dev.winclip.vetclinic.doctor.dto.DoctorWorkingHoursResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
	private final DoctorWorkingHoursService doctorWorkingHoursService;

	@GetMapping
	@Operation(summary = "List doctors", description = "Returns a paged list of active doctors.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK")
	})
	public PagedResponse<DoctorResponse> list(
			@Parameter(description = "1-based page number", example = "1")
			@RequestParam(defaultValue = "1") int page,
			@Parameter(description = "Page size (max 100)", example = "20")
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
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public DoctorResponse getById(@PathVariable Long id) {
		return doctorService.getById(id);
	}

	@GetMapping("/{id}/working-hours")
	@Operation(summary = "Get doctor working hours", description = "Weekly template (ISO day 1=Monday … 7=Sunday).")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public List<DoctorWorkingHoursResponse> getWorkingHours(@PathVariable Long id) {
		return doctorWorkingHoursService.getWorkingHours(id);
	}

	@PutMapping("/{id}/working-hours")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Replace doctor working hours", description = "Replaces the weekly schedule (ADMIN only). Send {\"intervals\":[]} to clear.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Replaced"),
			@ApiResponse(responseCode = "400", description = "Invalid intervals or overlap",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden (ADMIN only)",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void replaceWorkingHours(@PathVariable Long id, @Valid @RequestBody DoctorWorkingHoursReplaceRequest body) {
		doctorWorkingHoursService.replaceWorkingHours(id, body.intervals());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Create doctor", description = "Creates a new doctor (ADMIN only).")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "Validation failed",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden (ADMIN only)",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Duplicate email/license",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public DoctorResponse create(@Valid @RequestBody DoctorCreateRequest request) {
		return doctorService.create(request);
	}

	@PutMapping("/{id}")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Update doctor", description = "Updates a doctor (ADMIN only).")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Validation failed",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden (ADMIN only)",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "409", description = "Duplicate email/license",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public DoctorResponse update(@PathVariable Long id, @Valid @RequestBody DoctorCreateRequest request) {
		return doctorService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Delete doctor", description = "Soft-deletes a doctor (ADMIN only).")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Deleted"),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Forbidden (ADMIN only)",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Doctor not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void softDelete(@PathVariable Long id) {
		doctorService.softDelete(id);
	}
}
