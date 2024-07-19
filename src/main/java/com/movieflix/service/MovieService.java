package com.movieflix.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.movieflix.dto.MovieDto;
import com.movieflix.dto.MoviePageResponse;

public interface MovieService {

	MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

	MovieDto getMovie(int movieId);

	List<MovieDto> getAllMovies();

	MovieDto updateMovie(int movieId, MovieDto movieDto, MultipartFile file) throws IOException;

	String deleteMovie(int movieId) throws IOException;

	MoviePageResponse getAllMoviesWithPegination(int pageNumber, int pageSize);

	MoviePageResponse getAllMoviesWithPeginationAnsSorting(int pageNumber, int pageSize, String sortBy, String dir); //dir - direction
}
