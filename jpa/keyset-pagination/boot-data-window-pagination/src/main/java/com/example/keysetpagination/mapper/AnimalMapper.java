package com.example.keysetpagination.mapper;

import com.example.keysetpagination.entities.Animal;
import com.example.keysetpagination.model.request.AnimalRequest;
import com.example.keysetpagination.model.response.AnimalResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AnimalMapper {

    public Animal toEntity(AnimalRequest animalRequest) {
        Animal animal = new Animal();
        animal.setName(animalRequest.name());
        return animal;
    }

    public void mapAnimalWithRequest(Animal animal, AnimalRequest animalRequest) {
        animal.setName(animalRequest.name());
    }

    public AnimalResponse toResponse(Animal animal) {
        return new AnimalResponse(animal.getId(), animal.getName());
    }

    public List<AnimalResponse> toResponseList(List<Animal> animalList) {
        return animalList.stream().map(this::toResponse).toList();
    }
}