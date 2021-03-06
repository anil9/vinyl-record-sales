package com.nilsson.vinylrecordsales.lookup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nilsson.vinylrecordsales.domain.RecordInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.nilsson.vinylrecordsales.lookup.ExternalIdentifier.*;
import static java.util.Objects.requireNonNull;

public class LookupServiceImpl implements LookupService {
    private final LookupFacade lookupFacade;
    private final RecordInformationConverter recordInformationConverter;
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public LookupServiceImpl(LookupFacade lookupFacade, RecordInformationConverter recordInformationConverter) {
        this.lookupFacade = requireNonNull(lookupFacade, "lookupFacade");
        this.recordInformationConverter = requireNonNull(recordInformationConverter, "recordInformationConverter");
    }

    @Override
    public Mono<Optional<RecordInformation>> getRecordInformationByCatalogueNumber(String catalogueNumber,
                                                                                   String... extraTitleWords) {
        return lookupFacade.findByCatalogueNumber(catalogueNumber)
                .map(JsonParser::parseString)
                .map(JsonElement::getAsJsonObject)
                .map(jsonObject -> jsonObject.getAsJsonArray("results"))
                .flatMap(catalogueNumberResponse -> createRecordInformation(catalogueNumberResponse, extraTitleWords));
    }

    private Mono<Optional<RecordInformation>> createRecordInformation(JsonArray catalogueNumberResponse, String... extraTitleWords) {
        Optional<Integer> releaseId = findCorrectReleaseId(catalogueNumberResponse, extraTitleWords);
        if (releaseId.isEmpty()) {
            return Mono.just(Optional.empty());
        }
        String title = findTitle(catalogueNumberResponse, releaseId.get());
        return lookupFacade.getByReleaseId(releaseId.get())
                .map(JsonParser::parseString)
                .map(JsonElement::getAsJsonObject)
                .map(releaseResponse -> recordInformationConverter.getRecordInformation(title, releaseResponse));
    }

    private String findTitle(JsonArray catalogueNumberResponse, Integer releaseId) {
        return IntStream.range(0, catalogueNumberResponse.size())
                .mapToObj(catalogueNumberResponse::get)
                .map(JsonElement::getAsJsonObject)
                .filter(response -> response.get(RELEASE_ID.toString()).getAsInt() == releaseId)
                .findFirst()
                .map(correctResponse -> correctResponse.get(RECORD_TITLE.toString()))
                .map(JsonElement::getAsString)
                .orElseThrow();
    }

    private Optional<Integer> findCorrectReleaseId(JsonArray catalogueNumberResponse, String... extraTitleWords) {
        if (extraTitleWords.length <= 0 && !hasDistinctTitle(catalogueNumberResponse)) {
            return Optional.empty();
        }
        return findReleaseId(catalogueNumberResponse, extraTitleWords);


    }

    private Optional<Integer> findReleaseId(JsonArray catalogueNumberResponse, String... extraTitleWords) {
        List<JsonObject> releaseRecordsWithTitlesMatchingExtraWords = IntStream.range(0, catalogueNumberResponse.size())
                .mapToObj(catalogueNumberResponse::get)
                .map(JsonElement::getAsJsonObject)
                .filter(isReleaseRecord())
                .filter(titleMatchesExtraWords(extraTitleWords))
                .collect(Collectors.toList());
        if (hasDistinctTitle(releaseRecordsWithTitlesMatchingExtraWords)) {
            return releaseRecordsWithTitlesMatchingExtraWords.stream()
                    .findFirst()
                    .map(correctResponse -> correctResponse.get(RELEASE_ID.toString()))
                    .map(JsonElement::getAsInt);
        }
        return Optional.empty();
    }

    private Predicate<JsonObject> titleMatchesExtraWords(String[] extraTitleWords) {
        return response -> Arrays.stream(extraTitleWords)
                .map(String::toLowerCase)
                .allMatch(response.get(TRACK_TITLE.toString()).getAsString().toLowerCase()::contains);
    }

    private Predicate<JsonObject> isReleaseRecord() {
        return response -> response.get(TYPE.toString()).getAsString().equals(RELEASE.toString());
    }

    private boolean hasDistinctTitle(JsonArray results) {
        return hasDistinctTitle(IntStream.range(0, results.size())
                .mapToObj(results::get)
                .map(JsonElement::getAsJsonObject)
                .collect(Collectors.toList()));
    }

    private boolean hasDistinctTitle(List<JsonObject> jsonObjects) {
        var titles = new HashSet<String>();
        for (JsonObject element : jsonObjects) {
            titles.add(element.get(RECORD_TITLE.toString()).getAsString());
        }

        boolean distinctTitle = titles.size() == 1;
        if (!distinctTitle) {
            LOG.warn("Cannot determine which title to use. Found titles <{}>", titles);
        }

        return distinctTitle;
    }
}