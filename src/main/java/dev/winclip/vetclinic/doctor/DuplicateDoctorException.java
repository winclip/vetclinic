package dev.winclip.vetclinic.doctor;

/** Thrown when email or veterinary license is already taken by another doctor. */
public class DuplicateDoctorException extends RuntimeException {

	private final String code;

	public DuplicateDoctorException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
