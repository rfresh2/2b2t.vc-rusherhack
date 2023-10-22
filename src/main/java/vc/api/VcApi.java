package vc.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.rusherhack.core.logging.ILogger;
import vc.api.model.PlaytimeResponse;
import vc.api.model.SeenResponse;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;

public class VcApi {
    private final HttpClient httpClient;
    private final ILogger logger;
    private final ObjectMapper objectMapper;

    public VcApi(final ILogger logger) {
        this.logger = logger;
        this.httpClient = HttpClient.newBuilder()
            .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public Optional<SeenResponse> getLastSeen(final String playerName) {
        return get("https://api.2b2t.vc/seen?playerName=" + playerName, SeenResponse.class);
    }

    public Optional<SeenResponse> getFirstSeen(final String playerName) {
        return get("https://api.2b2t.vc/firstSeen?playerName=" + playerName, SeenResponse.class);
    }

    public Optional<PlaytimeResponse> getPlaytime(final String playerName) {
        return get("https://api.2b2t.vc/playtime?playerName=" + playerName, PlaytimeResponse.class);
    }

    private <T> Optional<T> get(final String uri, final Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .setHeader("User-Agent", "rusherhack/1.0")
                .build();
            HttpResponse<InputStream> response = this.httpClient.send(request, ofInputStream());
            return Optional.ofNullable(this.objectMapper.readValue(response.body(), responseType));
        } catch (final Exception e) {
            logger.error("Failed querying " + uri);
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
