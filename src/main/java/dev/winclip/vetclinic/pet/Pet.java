package dev.winclip.vetclinic.pet;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import dev.winclip.vetclinic.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
public class Pet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@Column(nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private PetSpecies species;

	@Column(length = 100)
	private String breed;

	@Enumerated(EnumType.STRING)
	@Column(length = 16)
	private PetSex sex;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Column(length = 2000)
	private String notes;

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private Instant createdAt;
}
