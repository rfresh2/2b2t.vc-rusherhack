package vc.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.rusherhack.core.logging.ILogger;
import vc.api.model.PlaytimeResponse;
import vc.api.model.SeenResponse;

import java.util.Optional;

public class VcApi {
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ILogger logger;

    public VcApi(final ILogger logger) {
        this.logger = logger;
        this.httpClient = HttpClientBuilder.create()
            .setUserAgent("rusherhack/1.0")
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
            final HttpGet request = new HttpGet(uri);
            try (CloseableHttpResponse response = this.httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    logger.error("Failed querying " + uri + " with status code " + response.getStatusLine().getStatusCode());
                    return Optional.empty();
                }
                return Optional.ofNullable(this.objectMapper.readValue(response.getEntity().getContent(), responseType));
            }
        } catch (final Exception e) {
            logger.error("Failed querying " + uri);
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
