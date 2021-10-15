package com.nilsson.vinylrecordsales.lookup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nilsson.vinylrecordsales.domain.ApiToken;
import com.nilsson.vinylrecordsales.domain.ApiTokenFactory;
import com.nilsson.vinylrecordsales.domain.RecordInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.invoke.MethodHandles;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.nilsson.vinylrecordsales.lookup.ExternalIdentifier.*;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class LookupFacadeImpl implements LookupFacade {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApiToken apiToken;
    private final WebClient client;

    public LookupFacadeImpl(ApiToken apiToken, WebClient client) {
        this.apiToken = apiToken;
        this.client = client;
    }

    @Override
    public Optional<RecordInformation> getRecordInformationByCatalogueNumber(String catalogueNumber) {
        JsonArray catalogueNumberResponse = getResponseFromCatalogueNumber(catalogueNumber);
        if (!hasDistinctTitle(catalogueNumberResponse)) {
            return Optional.empty();
        }
        var chosenRecord = catalogueNumberResponse.get(0).getAsJsonObject();
        var releaseId = extractReleaseId(chosenRecord);
        JsonObject releaseResponse = getResponseFromReleaseId(releaseId);
        return Optional.of(RecordInformation.builder()
                        .withTitle(extractTitle(chosenRecord))
                        .withTracklist(extractTracklist(releaseResponse))
                        .withYear(extractYear(releaseResponse))
                        .withStyle(extractStyle(releaseResponse))
                        .withGenre(extractGenre(releaseResponse))
                .build());

    }

    private String extractReleaseId(JsonObject chosenRecord) {
        return chosenRecord.get(RELEASE_ID.toString()).getAsString();
    }

    private String extractTitle(JsonObject releaseResponse) {
        return releaseResponse.get(RECORD_TITLE.toString()).getAsString();
    }

    private List<String> extractGenre(JsonObject releaseResponse) {
        var genres = releaseResponse.get(GENRES.toString()).getAsJsonArray();
        return StreamSupport.stream(genres.spliterator(), true)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
    }

    private List<String> extractStyle(JsonObject releaseResponse) {
        var styles = releaseResponse.get(STYLES.toString()).getAsJsonArray();
        return StreamSupport.stream(styles.spliterator(), true)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
    }

    private Year extractYear(JsonObject releaseResponse) {
        var element = releaseResponse.get(YEAR.toString());
        return element == null? null : Year.of(element.getAsInt());
    }

    private Map<String, String> extractTracklist(JsonObject jsonObject) {
        Map<String, String> tracklist = new LinkedHashMap<>();
        var tracklistArray = jsonObject.get(TRACKLIST.toString()).getAsJsonArray();

        for (JsonElement track : tracklistArray) {
            var trackObj = track.getAsJsonObject();
            tracklist.put(trackObj.get(TRACK_TITLE.toString()).getAsString(), trackObj.get(TRACK_DURATION.toString()).getAsString());
        }
        return tracklist;
    }

    private JsonObject getResponseFromReleaseId(String releaseId) {
        String response = getByReleaseId(releaseId).block();
        assert response != null;
        LOG.debug(response);
        return JsonParser.parseString(response).getAsJsonObject();
    }

    private JsonArray getResponseFromCatalogueNumber(String catalogueNumber) {
        String response = findByCatalogueNumber(catalogueNumber).block();
        assert response != null;
        LOG.debug(response);
        var asJsonObject = JsonParser.parseString(response).getAsJsonObject();
        return asJsonObject.getAsJsonArray("results");
    }

    private boolean hasDistinctTitle(JsonArray results) {
        var titles = new HashSet<String>();
        for (JsonElement result : results) {
            var recordObj = result.getAsJsonObject();
            titles.add(recordObj.get(RECORD_TITLE.toString()).getAsString());
        }

        boolean distinctTitle = titles.size() == 1;
        if (!distinctTitle) {
            LOG.warn("Cannot determine which title to use. Found titles <{}>", titles);
        }

        return distinctTitle;
    }

    Mono<String> findByCatalogueNumber(String catalogueNumber) {

        WebClient.RequestHeadersUriSpec<?> uriSpec = client.get();

        WebClient.RequestHeadersSpec<?> headersSpec = uriSpec.uri("/database/search?catno=" + catalogueNumber);
        return headersSpec.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Discogs token=" + apiToken.getValue())
                .retrieve()
                .bodyToMono(String.class);
    }

    Mono<String> getByReleaseId(String releaseId) {
        WebClient.RequestHeadersUriSpec<?> uriSpec = client.get();

        WebClient.RequestHeadersSpec<?> headersSpec = uriSpec.uri("/releases/" + releaseId);
        return headersSpec.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Discogs token=" + apiToken.getValue())
                .retrieve()
                .bodyToMono(String.class);
    }

    public static void main(String[] args) {
        LookupFacade lookupFacade = new LookupFacadeImpl(ApiTokenFactory.getApiToken(), WebClient.create("https://api.discogs.com"));
        var catalogueNumber = "MLPH 1622";
        var recordInformation = lookupFacade.getRecordInformationByCatalogueNumber(catalogueNumber).orElseThrow();
        LOG.info("Catalogue number {} returns record information {}", catalogueNumber,recordInformation);
    }
}
