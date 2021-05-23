package com.nilsson.vinylrecognition.domain;

public class ApiTokenFactory {
    private ApiTokenFactory() {
    }

    public static ApiToken getApiToken() {
        return ApiToken.fromFile("/home/andreas/discogs/discogs_api_token.txt");
    }

}
