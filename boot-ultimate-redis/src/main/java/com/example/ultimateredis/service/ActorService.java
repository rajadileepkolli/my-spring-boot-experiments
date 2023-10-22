package com.example.ultimateredis.service;

import com.example.ultimateredis.model.Actor;
import com.example.ultimateredis.repository.ActorRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    public Optional<Actor> findActorByName(String name) {
        return actorRepository.findByName(name);
    }

    public Optional<Actor> findActorByNameAndAge(String sampleName, int age) {
        return actorRepository.findByNameAndAge(sampleName, age);
    }

    public Optional<Actor> findActorById(String id) {
        return actorRepository.findById(id);
    }

    public Actor saveActor(Actor Actor) {
        return actorRepository.save(Actor);
    }

    public List<Actor> saveActors(List<Actor> actors) {
        return (List<Actor>) actorRepository.saveAll(actors);
    }

    public void deleteActorById(String id) {
        actorRepository.deleteById(id);
    }

    public void deleteActorByName(String name) {
        actorRepository.deleteByName(name);
    }
}
