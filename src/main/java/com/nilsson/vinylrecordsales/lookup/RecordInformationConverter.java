package com.nilsson.vinylrecordsales.lookup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nilsson.vinylrecordsales.domain.RecordInformation;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.nilsson.vinylrecordsales.lookup.ExternalIdentifier.*;

public class RecordInformationConverter {

    public Optional<RecordInformation> getRecordInformation(String title, JsonObject releaseResponse) {
        return Optional.of(RecordInformation.builder()
                .withTitle(title)
                .withTracklist(extractTracklist(releaseResponse))
                .withYear(extractYear(releaseResponse))
                .withStyle(extractStyle(releaseResponse))
                .withGenre(extractGenre(releaseResponse))
                .build());
    }

    private List<String> extractGenre(JsonObject releaseResponse) {
        var genres = releaseResponse.get(GENRES.toString()).getAsJsonArray();
        return StreamSupport.stream(genres.spliterator(), true)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
    }

    private List<String> extractStyle(JsonObject releaseResponse) {
        JsonElement styleJson = releaseResponse.get(STYLES.toString());
        if (styleJson == null || styleJson.isJsonNull()) {
            return Collections.emptyList();
        }
        var styles = styleJson.getAsJsonArray();
        return StreamSupport.stream(styles.spliterator(), true)
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
    }

    private Year extractYear(JsonObject releaseResponse) {
        var element = releaseResponse.get(YEAR.toString());
        if (element == null || element.getAsInt() == 0) return null;

        return Year.of(element.getAsInt());
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
}