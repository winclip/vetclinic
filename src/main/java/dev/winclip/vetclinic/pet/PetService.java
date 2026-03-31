package dev.winclip.vetclinic.pet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.winclip.vetclinic.pet.dto.PetCreateRequest;
import dev.winclip.vetclinic.pet.dto.PetResponse;
import dev.winclip.vetclinic.pet.dto.AdminPetResponse;
import dev.winclip.vetclinic.user.User;
import dev.winclip.vetclinic.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

	private static final String USER_NOT_FOUND = "User not found";
	private static final String PET_NOT_FOUND = "Pet not found";

	private final PetRepository petRepository;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public Page<PetResponse> listMyPets(String username, Pageable pageable) {
		User owner = requireUser(username);
		return petRepository.findAllByOwnerIdAndActiveTrue(owner.getId(), pageable)
				.map(PetResponse::from);
	}

	@Transactional
	public PetResponse createMyPet(String username, PetCreateRequest request) {
		User owner = requireUser(username);
		Pet pet = new Pet();
		pet.setOwner(owner);
		pet.setName(request.name());
		pet.setSpecies(request.species());
		pet.setBreed(request.breed());
		pet.setSex(request.sex());
		pet.setDateOfBirth(request.dateOfBirth());
		pet.setNotes(request.notes());
		return PetResponse.from(petRepository.save(pet));
	}

	@Transactional(readOnly = true)
	public PetResponse getMyPet(String username, Long petId) {
		Pet pet = requireMyPet(username, petId);
		return PetResponse.from(pet);
	}

	@Transactional
	public PetResponse updateMyPet(String username, Long petId, PetCreateRequest request) {
		Pet pet = requireMyPet(username, petId);
		pet.setName(request.name());
		pet.setSpecies(request.species());
		pet.setBreed(request.breed());
		pet.setSex(request.sex());
		pet.setDateOfBirth(request.dateOfBirth());
		pet.setNotes(request.notes());
		return PetResponse.from(petRepository.save(pet));
	}

	@Transactional(readOnly = true)
	public Page<AdminPetResponse> listAllPetsForAdmin(Pageable pageable) {
		return petRepository.findAllByOrderByCreatedAtDesc(pageable)
				.map(AdminPetResponse::from);
	}

	@Transactional(readOnly = true)
	public Page<AdminPetResponse> listPetsForAdmin(Boolean active, Pageable pageable) {
		if (active == null) {
			return listAllPetsForAdmin(pageable);
		}
		return (active ? petRepository.findAllByActiveTrueOrderByCreatedAtDesc(pageable)
				: petRepository.findAllByActiveFalseOrderByCreatedAtDesc(pageable))
						.map(AdminPetResponse::from);
	}

	@Transactional
	public AdminPetResponse restorePetForAdmin(Long petId) {
		if (petId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pet id is required");
		}
		Pet pet = petRepository.findById(petId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PET_NOT_FOUND));
		if (!pet.isActive()) {
			pet.setActive(true);
			pet = petRepository.save(pet);
		}
		return AdminPetResponse.from(pet);
	}

	@Transactional
	public void deleteMyPet(String username, Long petId) {
		Pet pet = requireMyPet(username, petId);
		if (pet.isActive()) {
			pet.setActive(false);
			petRepository.save(pet);
		}
	}

	private User requireUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
	}

	private Pet requireMyPet(String username, Long petId) {
		User owner = requireUser(username);
		return petRepository.findByIdAndOwnerIdAndActiveTrue(petId, owner.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PET_NOT_FOUND));
	}
}
