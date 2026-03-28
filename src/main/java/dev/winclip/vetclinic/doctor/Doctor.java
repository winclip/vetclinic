package dev.winclip.vetclinic.doctor;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
public class Doctor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 100)
	private String lastName;

	@Column(length = 255)
	private String specialization;

	@Column(length = 32)
	private String phone;

	@Column(unique = true, length = 255)
	private String email;

	@Column(name = "veterinary_license", unique = true, length = 64)
	private String veterinaryLicense;

	@Column(length = 2000)
	private String bio;

	@Column(name = "photo_url", length = 512)
	private String photoUrl;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Column(name = "years_of_experience")
	private Integer yearsOfExperience;

	@Column(name = "hired_on")
	private LocalDate hiredOn;

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private Instant createdAt;
}
