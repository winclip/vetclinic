package dev.winclip.vetclinic.pet;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dev.winclip.vetclinic.pet.dto.PetCreateRequest;
import dev.winclip.vetclinic.pet.dto.PetResponse;
import dev.winclip.vetclinic.user.User;
import dev.winclip.vetclinic.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

	private static final String USER_NOT_FOUND = "User not found";

	private final PetRepository petRepository;
	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public List<PetResponse> listMyPets(String username) {
		User owner = requireUser(username);
		return petRepository.findAllByOwnerIdOrderByCreatedAtDesc(owner.getId()).stream()
				.map(PetResponse::from)
				.toList();
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

	private User requireUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
	}
}
