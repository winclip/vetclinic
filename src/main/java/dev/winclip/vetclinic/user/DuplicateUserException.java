package dev.winclip.vetclinic.user;

public class DuplicateUserException extends RuntimeException {

	private final String code;

	public DuplicateUserException(String code, String message) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
