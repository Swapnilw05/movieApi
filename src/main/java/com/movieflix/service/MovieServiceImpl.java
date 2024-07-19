package com.movieflix.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.movieflix.dto.MovieDto;
import com.movieflix.dto.MoviePageResponse;
import com.movieflix.entities.Movie;
import com.movieflix.exceptions.FileExistsException;
import com.movieflix.exceptions.MovieNotFoundException;
import com.movieflix.repositories.MovieRepository;

@Service
public class MovieServiceImpl implements MovieService{

	private final MovieRepository movieRepository;

	private final FileService fileService;

	@Value("${project.poster}")
	private String path;

	@Value("${base.url}")
	private String baseUrl;

	public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
		this.movieRepository = movieRepository;
		this.fileService = fileService;
	}

	@Override
	public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
		// 1. upload file
		if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
			throw new FileExistsException("File already exist! Please enter another file name!");
		}
		String uploadedFileName = fileService.uploadFile(path, file);

		// 2. set the value of field 'poster' as a filename
		movieDto.setPoster(uploadedFileName);

		// 3. map dto to movie object
		Movie movie = new Movie(
			(Integer) null,
			movieDto.getTitle(),
			movieDto.getDirector(),
			movieDto.getStudio(),
			movieDto.getMovieCast(),
			movieDto.getReleaseYear(),
			movieDto.getPoster()
		);

		// 4. save movie object -> saved movie object
		Movie savedMovie = movieRepository.save(movie);

		// 5. generate posterUrl
		String posterUrl = baseUrl + "/file/" + uploadedFileName;

		// 6. map Movie object to Dto object and return it.
		MovieDto response = new MovieDto(
			savedMovie.getMovieId(),
			savedMovie.getTitle(),
			savedMovie.getDirector(),
			savedMovie.getStudio(),
			savedMovie.getMovieCast(),
			savedMovie.getReleaseYear(),
			savedMovie.getPoster(),
			posterUrl
		);

		return response;
	}

	@Override
	public MovieDto getMovie(int movieId) {

		// 1. check data in db and if exists, fetch data of given id
		Movie movie = movieRepository.findById(movieId)
		.orElseThrow(() -> new MovieNotFoundException("Movie not found with id = " + movieId));

		// 2. generate posterUrl
		String posterUrl = baseUrl + "/file/" + movie.getPoster();

		// 3. map to MovieDto object and return it
		MovieDto response = new MovieDto(
			movie.getMovieId(),
			movie.getTitle(),
			movie.getDirector(),
			movie.getStudio(),
			movie.getMovieCast(),
			movie.getReleaseYear(),
			movie.getPoster(),
			posterUrl
		);

		return response;	
	}

	@Override
	public List<MovieDto> getAllMovies() {
		// 1. fetch all data from db
		List<Movie> movies = movieRepository.findAll();

		List<MovieDto> movieDtos = new ArrayList<>();

		// 2. iterate through list, generate posterUrl fro each movie obj, 
		// and map to MovieDto obj
		for(Movie movie : movies){
			String posterUrl = baseUrl + "/file/" + movie.getPoster();
			MovieDto movieDto = new MovieDto(
			movie.getMovieId(),
			movie.getTitle(),
			movie.getDirector(),
			movie.getStudio(),
			movie.getMovieCast(),
			movie.getReleaseYear(),
			movie.getPoster(),
			posterUrl
		);
		movieDtos.add(movieDto);
		}

		return movieDtos;
	}

	@Override
	public MovieDto updateMovie(int movieId, MovieDto movieDto, MultipartFile file) throws IOException {
		// 1. check if movie obj exist with given movieId
		Movie mv = movieRepository.findById(movieId)
		.orElseThrow(() -> new MovieNotFoundException("Movie not found with id = " + movieId));
		
		// 2. if file is null, do nothing
		// if file is not null, then delete existing file associted with the record
		// and upload the new file
		String fileName = mv.getPoster();
		if (file != null) {
			Files.deleteIfExists(Paths.get(path + File.separator + fileName));
			fileName = fileService.uploadFile(path, file);
		}

		// 3. set movieDto poster value, according to step 2
		movieDto.setPoster(fileName);

		// 4. mao it to movie obj
		Movie movie = new Movie(
			mv.getMovieId(),
			movieDto.getTitle(),
			movieDto.getDirector(),
			movieDto.getStudio(),
			movieDto.getMovieCast(),
			movieDto.getReleaseYear(),
			movieDto.getPoster()
		);

		// 5. save the movie obj -> return saved movie obj
		Movie updatedMovie = movieRepository.save(movie);

		// 6. generate posterUrl
		String posterUrl = baseUrl + "/file/" + fileName;

		// 7. map to movieDto and return it
		MovieDto response = new MovieDto(
			movie.getMovieId(),
			movie.getTitle(),
			movie.getDirector(),
			movie.getStudio(),
			movie.getMovieCast(),
			movie.getReleaseYear(),
			movie.getPoster(),
			posterUrl
		);

		return response;
	}

	@Override
	public String deleteMovie(int movieId) throws IOException {
		// 1. if movie obj exist in db	
		Movie mv = movieRepository.findById(movieId)
		.orElseThrow(() -> new MovieNotFoundException("Movie not found with id = " + movieId));
		int id = mv.getMovieId();

		// 2. delete the file associated with thid obj
		Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));

		// 3 . delete movie obj
		movieRepository.delete(mv);

		return "Movie deleted with id = " + id;
	}

	@Override
	public MoviePageResponse getAllMoviesWithPegination(int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		Page<Movie> moviePages = movieRepository.findAll(pageable);
		List<Movie> movies = moviePages.getContent();

		List<MovieDto> movieDtos = new ArrayList<>();

		// 2. iterate through list, generate posterUrl fro each movie obj, 
		// and map to MovieDto obj
		for(Movie movie : movies){
			String posterUrl = baseUrl + "/file/" + movie.getPoster();
			MovieDto movieDto = new MovieDto(
			movie.getMovieId(),
			movie.getTitle(),
			movie.getDirector(),
			movie.getStudio(),
			movie.getMovieCast(),
			movie.getReleaseYear(),
			movie.getPoster(),
			posterUrl
		);
		movieDtos.add(movieDto);
		}

		return new MoviePageResponse(movieDtos, pageNumber, pageSize,
		moviePages.getTotalElements(),
		moviePages.getTotalPages(),
		moviePages.isLast());	
	}

	@Override
	public MoviePageResponse getAllMoviesWithPeginationAnsSorting(int pageNumber, int pageSize, 
	String sortBy, String dir) {
		Sort sort = dir.equalsIgnoreCase("asc")
			 ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
			
			 Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

		Page<Movie> moviePages = movieRepository.findAll(pageable);
		List<Movie> movies = moviePages.getContent();

		List<MovieDto> movieDtos = new ArrayList<>();

		// 2. iterate through list, generate posterUrl fro each movie obj, 
		// and map to MovieDto obj
		for(Movie movie : movies){
			String posterUrl = baseUrl + "/file/" + movie.getPoster();
			MovieDto movieDto = new MovieDto(
			movie.getMovieId(),
			movie.getTitle(),
			movie.getDirector(),
			movie.getStudio(),
			movie.getMovieCast(),
			movie.getReleaseYear(),
			movie.getPoster(),
			posterUrl
		);
		movieDtos.add(movieDto);
		}

		return new MoviePageResponse(movieDtos, pageNumber, pageSize,
		moviePages.getTotalElements(),
		moviePages.getTotalPages(),
		moviePages.isLast());	
	}

}
