package com.movieflix.dto;

import java.util.List;

public record MoviePageResponse(List<MovieDto> movieDto, int pageNumber,
	int pageSize, long totalElements, int totalPages, boolean isLast){

	

						
}

								