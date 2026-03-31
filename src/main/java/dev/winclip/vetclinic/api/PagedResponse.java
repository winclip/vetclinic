package dev.winclip.vetclinic.api;

import java.util.List;

public record PagedResponse<T>(Info info, List<T> results) {

	public record Info(long count, int pages, int page, int size) {
	}
}

