package com.movieflix.entities;

import java.util.Set;

import jakarta.annotation.Generated;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int movieId;

	@Column(nullable = false)
	@NotBlank(message = "Please provide movie's title!")
	private String title;

	@Column(nullable = false)
	@NotBlank(message = "Please provide movie's director!")
	private String director;

	@Column(nullable = false)
	@NotBlank(message = "Please provide movie's studio!")
	private String studio;

	@ElementCollection
	@CollectionTable(name = "movie_cast")
	private Set<String> movieCast;

	@Column(nullable = false)
	private int releaseYear;

	@Column(nullable = false)
	@NotBlank(message = "Please provide movie's poster!")
	private String poster;
}
