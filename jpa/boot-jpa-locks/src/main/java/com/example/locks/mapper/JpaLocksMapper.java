package com.example.locks.mapper;

import com.example.locks.entities.Actor;
import com.example.locks.entities.Genre;
import com.example.locks.entities.Movie;
import com.example.locks.entities.Review;
import com.example.locks.model.request.ActorRequest;
import com.example.locks.model.request.MovieRequest;
import com.example.locks.model.response.ActorResponse;
import com.example.locks.model.response.MovieResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface JpaLocksMapper {

    Movie movieRequestToMovie(MovieRequest movieRequest);

    default Movie movieRequestToMovieEntity(MovieRequest movieRequest) {
        var movie = movieRequestToMovie(movieRequest);
        for (Actor a : movie.getActors()) a.setMovies(List.of(movie));
        for (Review r : movie.getReviews()) r.setMovie(movie);
        for (Genre g : movie.getGenres()) g.setMovies(List.of(movie));
        return movie;
    }

    default Movie movieRequestToMovieWithId(MovieRequest movieRequest, Long movieId) {
        var movie = movieRequestToMovie(movieRequest);
        movie.setMovieId(movieId);
        return movie;
    }

    MovieResponse movieToMovieResponse(Movie movie);

    default List<MovieResponse> moviesListToMovieResponseList(List<Movie> moviesList) {
        return moviesList.stream().map(this::movieToMovieResponse).collect(Collectors.toList());
    }

    ActorResponse actorToActorResponse(Actor actor);

    List<ActorResponse> actorToActorResponseList(List<Actor> actors);

    Actor toActorEntity(ActorRequest actorRequest);

    void mapActorWithRequest(ActorRequest actorRequest, @MappingTarget Actor actor);
}