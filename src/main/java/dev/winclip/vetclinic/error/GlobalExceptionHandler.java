package dev.winclip.vetclinic.error;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import dev.winclip.vetclinic.doctor.DuplicateDoctorException;
import dev.winclip.vetclinic.user.DuplicateUserException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final String UQ_EMAIL = "uq_doctors_email";
	private static final String UQ_LICENSE = "uq_doctors_veterinary_license";
	private static final String UQ_USERS_EMAIL = "uq_users_email";

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse("INVALID_CREDENTIALS", "Invalid username or password"));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest()
				.body(new ErrorResponse("MALFORMED_JSON",
						"Request body is missing, invalid JSON, or does not match the expected shape"));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		String required = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "value";
		String message = "Parameter '%s' must be a valid %s".formatted(ex.getName(), required);
		return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_PARAMETER", message));
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
				.body(new ErrorResponse("METHOD_NOT_ALLOWED",
						"HTTP method is not supported for this path"));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
		HttpStatusCode status = ex.getStatusCode();
		String message = ex.getReason() != null ? ex.getReason() : "Request failed";
		String code = status instanceof HttpStatus http
				? http.name()
				: "HTTP_" + status.value();
		return ResponseEntity.status(status).body(new ErrorResponse(code, message));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fields = new LinkedHashMap<>();
		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			String msg = fe.getDefaultMessage();
			fields.put(fe.getField(), msg != null ? msg : "Invalid value");
		}
		String summary = switch (fields.size()) {
			case 0 -> "Validation failed";
			case 1 -> "One field has an invalid value (see the fields map for details)";
			default -> fields.size() + " fields have invalid values (see the fields map for details)";
		};
		return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_FAILED", summary, fields));
	}

	@ExceptionHandler(DuplicateDoctorException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateDoctor(DuplicateDoctorException ex) {
		return conflict(ex.getCode(), ex.getMessage());
	}

	@ExceptionHandler(DuplicateUserException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException ex) {
		return conflict(ex.getCode(), ex.getMessage());
	}

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
		if (constraint != null && containsIgnoreCase(constraint, UQ_USERS_EMAIL)) {
			return conflict("DUPLICATE_EMAIL", "This email is already registered");
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
		if (msg.contains(UQ_USERS_EMAIL)) {
			return UQ_USERS_EMAIL;
		}
		return null;
	}

	private static boolean containsIgnoreCase(String haystack, String needle) {
		return haystack.toLowerCase().contains(needle.toLowerCase());
	}
}
