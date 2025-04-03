package xyz.tomorrowlearncamp.bookking.domain.user.auth.dto;

public class RefreshTokenResponse {

    private final String accessToken;

    private RefreshTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public static RefreshTokenResponse of(String accessToken) {
        return new RefreshTokenResponse(accessToken);
    }

    public String getAccessToken() {
        return accessToken;
    }
}