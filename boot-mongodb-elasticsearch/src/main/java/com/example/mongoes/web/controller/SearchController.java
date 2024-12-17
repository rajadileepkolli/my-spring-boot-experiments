package com.example.mongoes.web.controller;

import com.example.mongoes.document.Restaurant;
import com.example.mongoes.response.AggregationSearchResponse;
import com.example.mongoes.response.ResultData;
import com.example.mongoes.web.service.SearchService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Timed
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search/borough")
    public Mono<ResponseEntity<Flux<Restaurant>>> searchPhrase(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return searchService.searchMatchBorough(query, offset, limit).map(ResponseEntity::ok);
    }

    @GetMapping("/search/multi")
    public Mono<ResponseEntity<SearchPage<Restaurant>>> searchMulti(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(value = "prefix_phrase_enabled", defaultValue = "false")
                    Boolean prefixPhraseEnabled) {
        return searchService
                .multiSearchQuery(query, offset, limit, prefixPhraseEnabled)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/search/term/borough")
    public Mono<ResponseEntity<SearchPage<Restaurant>>> searchTermForBorough(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return searchService.termQueryForBorough(query, offset, limit).map(ResponseEntity::ok);
    }

    @GetMapping("/search/terms")
    public Mono<ResponseEntity<SearchPage<Restaurant>>> searchTerms(
            @RequestParam("query") List<String> queries,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return searchService.termsQueryForBorough(queries, offset, limit).map(ResponseEntity::ok);
    }

    @GetMapping("/search/must/bool")
    public Mono<ResponseEntity<Flux<Restaurant>>> searchBoolMust(
            @RequestParam String borough,
            @RequestParam String cuisine,
            @RequestParam String name,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return searchService
                .queryBoolWithMust(borough, cuisine, name, offset, limit)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/search/should/bool")
    public Mono<ResponseEntity<SearchPage<Restaurant>>> searchBoolShould(
            @RequestParam String borough,
            @RequestParam String cuisine,
            @RequestParam String name,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return searchService
                .queryBoolWithShould(borough, cuisine, name, offset, limit)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/search/wildcard/borough")
    public Mono<ResponseEntity<SearchPage<Restaurant>>> searchBoolShould(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return searchService.wildcardSearch(query, offset, limit).map(ResponseEntity::ok);
    }

    @GetMapping("/search/regexp/borough")
    public Mono<ResponseEntity<SearchPage<Restaurant>>> searchRegularExpression(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return searchService.regExpSearch(query, offset, limit).map(ResponseEntity::ok);
    }

    @GetMapping("/search/simple")
    public Mono<ResponseEntity<SearchPage<Restaurant>>> searchSimpleQueryForBoroughAndCuisine(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return searchService
                .searchSimpleQueryForBoroughAndCuisine(query, offset, limit)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/search/restaurant/range")
    public Mono<ResponseEntity<SearchPage<Restaurant>>> searchRestaurantIdRange(
            @RequestParam Long lowerLimit,
            @RequestParam Long upperLimit,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return searchService
                .searchRestaurantIdRange(lowerLimit, upperLimit, offset, limit)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/search/date/range")
    public Mono<ResponseEntity<SearchPage<Restaurant>>> searchDateRange(
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        return searchService
                .searchDateRange(fromDate, toDate, offset, limit)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/search/aggregate")
    public Mono<ResponseEntity<AggregationSearchResponse>> aggregateSearch(
            @RequestParam String searchKeyword,
            @RequestParam List<String> fieldNames,
            @RequestParam(required = false, defaultValue = "15") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "DESC") String sortOrder,
            @RequestParam(required = false, defaultValue = "restaurant_id") String... sortFields) {
        return searchService
                .aggregateSearch(searchKeyword, fieldNames, sortOrder, limit, offset, sortFields)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Search restaurants within range",
            description = "Find restaurants within specified distance from given coordinates",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved restaurants",
                        content = @Content(schema = @Schema(implementation = ResultData.class))),
                @ApiResponse(responseCode = "400", description = "Invalid parameters provided")
            })
    @GetMapping("/search/restaurant/withInRange")
    public Flux<ResultData> searchRestaurantsWithInRange(
            @Parameter(
                            description = "Latitude coordinate (between -90 and 90)",
                            example = "40.7128")
                    @RequestParam
                    @Min(-90)
                    @Max(90)
                    Double lat,
            @Parameter(
                            description = "Longitude coordinate (between -180 and 180)",
                            example = "-74.0060")
                    @RequestParam
                    @Min(-180)
                    @Max(180)
                    Double lon,
            @Parameter(description = "Distance from coordinates (must be positive)")
                    @RequestParam
                    @Positive
                    Double distance,
            @Parameter(
                            description = "Unit of distance",
                            example = "km",
                            schema = @Schema(allowableValues = {"km", "mi"}))
                    @RequestParam(defaultValue = "km", required = false)
                    @Pattern(regexp = "^(km|mi)$", message = "Unit must be either 'km' or 'mi'")
                    String unit) {
        return this.searchService.searchRestaurantsWithInRange(lat, lon, distance, unit);
    }
}
