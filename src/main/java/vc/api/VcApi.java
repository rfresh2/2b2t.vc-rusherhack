package vc.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.stream.JsonReader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.rusherhack.core.logging.ILogger;
import vc.api.model.PlaytimeResponse;
import vc.api.model.SeenResponse;

import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.util.Optional;

public class VcApi {
    private final CloseableHttpClient httpClient;
    private final ILogger logger;
    private final Gson gson;

    public VcApi(final ILogger logger) {
        this.logger = logger;
        this.httpClient = HttpClientBuilder.create()
            .setUserAgent("rusherhack/1.0")
            .build();
        this.gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>) (json, typeOfT, context) -> OffsetDateTime.parse(json.getAsString()))
            .create();
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
                try (JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()))) {
                    return Optional.ofNullable(this.gson.fromJson(reader, responseType));
                }
            }
        } catch (final Exception e) {
            logger.error("Failed querying " + uri);
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
