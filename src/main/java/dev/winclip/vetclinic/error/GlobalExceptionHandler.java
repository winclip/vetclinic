package dev.winclip.vetclinic.error;

import java.sql.SQLException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String UQ_EMAIL = "uq_doctors_email";
	private static final String UQ_LICENSE = "uq_doctors_veterinary_license";

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
		if (!isUniqueViolation(ex)) {
			return ResponseEntity.badRequest()
					.body(new ErrorResponse("INVALID_DATA", "Request could not be processed due to data constraints"));
		}
		String constraint = resolveConstraintName(ex);
		if (constraint != null && containsIgnoreCase(constraint, UQ_EMAIL)) {
			return conflict("DUPLICATE_EMAIL", "A doctor with this email already exists");
		}
		if (constraint != null && containsIgnoreCase(constraint, UQ_LICENSE)) {
			return conflict("DUPLICATE_VETERINARY_LICENSE", "This veterinary license is already registered");
		}
		return conflict("DUPLICATE_KEY", "This value already exists");
	}

	private static ResponseEntity<ErrorResponse> conflict(String code, String message) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(code, message));
	}

	private static boolean isUniqueViolation(DataIntegrityViolationException ex) {
		for (Throwable t = ex.getMostSpecificCause(); t != null; t = t.getCause()) {
			if (t instanceof SQLException sql && "23505".equals(sql.getSQLState())) {
				return true;
			}
		}
		String msg = ex.getMostSpecificCause().getMessage();
		return msg != null && (msg.contains("unique constraint") || msg.contains("Duplicate entry")
				|| msg.contains("duplicate key"));
	}

	private static String resolveConstraintName(DataIntegrityViolationException ex) {
		Throwable t = ex.getMostSpecificCause();
		if (t instanceof ConstraintViolationException cv) {
			return cv.getConstraintName();
		}
		while (t != null) {
			if (t instanceof ConstraintViolationException cv) {
				return cv.getConstraintName();
			}
			t = t.getCause();
		}
		String msg = ex.getMostSpecificCause().getMessage();
		if (msg == null) {
			return null;
		}
		if (msg.contains(UQ_EMAIL)) {
			return UQ_EMAIL;
		}
		if (msg.contains(UQ_LICENSE)) {
			return UQ_LICENSE;
		}
		return null;
	}

	private static boolean containsIgnoreCase(String haystack, String needle) {
		return haystack.toLowerCase().contains(needle.toLowerCase());
	}
}
