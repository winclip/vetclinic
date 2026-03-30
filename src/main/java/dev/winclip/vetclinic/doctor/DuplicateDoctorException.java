package dev.winclip.vetclinic.doctor;

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
