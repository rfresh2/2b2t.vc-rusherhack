package vc.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.stream.JsonReader;
import org.rusherhack.client.api.feature.command.arg.PlayerReference;
import org.rusherhack.core.logging.ILogger;
import vc.api.model.PlaytimeResponse;
import vc.api.model.QueueStatus;
import vc.api.model.SeenResponse;
import vc.api.model.StatsResponse;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.Optional;

import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;

public class VcApi {
    private final HttpClient httpClient;
    private final ILogger logger;
    private final Gson gson;
    private final String version;

    public VcApi(final ILogger logger, final String version) {
        this.logger = logger;
        this.version = version;
        this.httpClient = HttpClient.newBuilder()
            .build();
        this.gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>) (json, typeOfT, context) -> OffsetDateTime.parse(json.getAsString()))
            .create();
    }

    public Optional<SeenResponse> getSeen(final PlayerReference player) {
        return get("https://api.2b2t.vc/seen?playerName=" + player.name(), SeenResponse.class);
    }

    public Optional<PlaytimeResponse> getPlaytime(final PlayerReference player) {
        return get("https://api.2b2t.vc/playtime?playerName=" + player.name(), PlaytimeResponse.class);
    }

    public Optional<QueueStatus> getQueueStatus() {
        return get("https://api.2b2t.vc/queue", QueueStatus.class);
    }

    public Optional<StatsResponse> getStats(final PlayerReference player) {
        return get("https://api.2b2t.vc/stats/player?playerName=" + player.name(), StatsResponse.class);
    }

    private <T> Optional<T> get(final String uri, final Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .setHeader("User-Agent", "rusherhack/1.0")
                .build();
            HttpResponse<InputStream> response = this.httpClient.send(request, ofInputStream());
            try (JsonReader reader = new JsonReader(new InputStreamReader(response.body()))) {
                return Optional.ofNullable(this.gson.fromJson(reader, responseType));
            }
        } catch (final Exception e) {
            logger.error("Failed querying " + uri);
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
