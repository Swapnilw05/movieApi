package com.movieflix.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieflix.dto.MovieDto;
import com.movieflix.dto.MoviePageResponse;
import com.movieflix.exceptions.EmptyFileException;
import com.movieflix.service.MovieService;
import com.movieflix.utils.AppConstants;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

	private final MovieService movieService;

	public MovieController(MovieService movieService) {
		this.movieService = movieService;
	}

	@PostMapping("/add-movie")
	public ResponseEntity<MovieDto> addMovieHanler(@RequestPart MultipartFile file,
					@RequestPart String movieDto) throws IOException{

		MovieDto dto = convertToMovieDto(movieDto);
		return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
	}

	@GetMapping("/{movieId}")
	public ResponseEntity<MovieDto> getMovieHandler(@PathVariable int movieId){
		return ResponseEntity.ok(movieService.getMovie(movieId));
	}

	@GetMapping("/all")
	public ResponseEntity<List<MovieDto>> getAllMoviesHandler(){
		return ResponseEntity.ok(movieService.getAllMovies());
	}

	@PutMapping("/update/{movieId}")
	public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable int movieId,
			@RequestPart MultipartFile file,
			@RequestPart String movieDtoObj) throws IOException, EmptyFileException{
		
		if (file.isEmpty()){
			throw new EmptyFileException("File is empty! Please send another file!");
		} 
		MovieDto movieDto = convertToMovieDto(movieDtoObj);
		return ResponseEntity.ok(movieService.updateMovie(movieId, movieDto, file));
	}

	@DeleteMapping("/delete/{movieId}")
	public ResponseEntity<String> deleteMovieHandler(@PathVariable int movieId) throws IOException{
		return ResponseEntity.ok(movieService.deleteMovie(movieId));
	}

	@GetMapping("/allMoviesPage")
	public ResponseEntity<MoviePageResponse> getAllMoviesWithPegination(
		@RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
		@RequestParam(defaultValue = AppConstants.PAGE_SIZE, required =  false) int pageSize){

			return ResponseEntity.ok(movieService.getAllMoviesWithPegination(pageNumber, pageSize));
	}

	@GetMapping("/allMoviesPageSort")
	public ResponseEntity<MoviePageResponse> getAllMoviesWithPeginationAndSorting(
		@RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
		@RequestParam(defaultValue = AppConstants.PAGE_SIZE, required =  false) int pageSize,
		@RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
		@RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String dir){

			return ResponseEntity.ok(movieService.getAllMoviesWithPeginationAnsSorting(pageNumber, pageSize, sortBy, dir));
	}

	private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException{
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(movieDtoObj, MovieDto.class);
		
	}
}
