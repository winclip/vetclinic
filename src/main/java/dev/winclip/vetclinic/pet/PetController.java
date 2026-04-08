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
import dev.winclip.vetclinic.error.ErrorResponse;
import dev.winclip.vetclinic.pet.dto.PetCreateRequest;
import dev.winclip.vetclinic.pet.dto.PetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/pets/me")
@Tag(name = "pets", description = "Pets of the current user")
@RequiredArgsConstructor
public class PetController {

	private static final int DEFAULT_SIZE = 20;
	private static final int MAX_SIZE = 100;

	private final PetService petService;

	@GetMapping
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "List my pets", description = "Returns a paged list of pets owned by the current authenticated user.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public PagedResponse<PetResponse> list(
			@AuthenticationPrincipal String username,
			@Parameter(description = "1-based page number", example = "1")
			@RequestParam(defaultValue = "1") int pageNumber,
			@Parameter(description = "Page size (max 100)", example = "20")
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
	@Operation(summary = "Create my pet", description = "Creates a new pet owned by the current authenticated user.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "Validation failed",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
							examples = @ExampleObject(value = """
									{"code":"VALIDATION_FAILED","message":"One field has an invalid value (see the fields map for details)","fields":{"name":"Name is required"}}
									"""))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public PetResponse create(@AuthenticationPrincipal String username, @Valid @RequestBody PetCreateRequest request) {
		return petService.createMyPet(username, request);
	}

	@GetMapping("/{id}")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Get my pet", description = "Returns a single pet owned by the current authenticated user.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Pet not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public PetResponse getById(@AuthenticationPrincipal String username, @PathVariable Long id) {
		return petService.getMyPet(username, id);
	}

	@PutMapping("/{id}")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Update my pet", description = "Updates a pet owned by the current authenticated user.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Validation failed",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Pet not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public PetResponse update(
			@AuthenticationPrincipal String username,
			@PathVariable Long id,
			@Valid @RequestBody PetCreateRequest request) {
		return petService.updateMyPet(username, id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Delete my pet", description = "Soft-deletes (or deletes) a pet owned by the current authenticated user.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Deleted"),
			@ApiResponse(responseCode = "401", description = "Missing or invalid JWT",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Pet not found",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	})
	public void delete(@AuthenticationPrincipal String username, @PathVariable Long id) {
		petService.deleteMyPet(username, id);
	}
}
