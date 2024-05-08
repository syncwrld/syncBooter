package me.syncwrld.booter.minecraft.updater.github;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import me.syncwrld.booter.common.tool.http.Request;

@Data
public class GithubUpdater {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/%s/%s";
    private static final String RELEASES_URL = "/releases";
    private static final String TAG_NAME = "tag_name";
    private static final String BODY = "body";
    private static final String ASSETS = "assets";
    private static final String BROWSER_DOWNLOAD_URL = "browser_download_url";

    private final String repositoryOwner;
    private final String repositoryName;
    private final String currentVersion;

    private String latestVersionTag;
    private String githubRepositoryURL;
    private List<String> modifications;

    public GithubUpdater(String repositoryOwner, String repositoryName, String currentVersion) {
        this.repositoryOwner = repositoryOwner;
        this.repositoryName = repositoryName;
        this.currentVersion = currentVersion.replace("v", "");
        this.githubRepositoryURL = String.format(GITHUB_API_URL, repositoryOwner, repositoryName);
        this.modifications = getModifications();
        this.latestVersionTag = getLatestVersion().replace("v", "");
    }

    public String getLatestVersion() {
        return getReleaseData(getLatestReleaseID()).get(TAG_NAME).getAsString();
    }

    public List<String> getModifications() {
        return parseModifications(getReleaseData(getLatestReleaseID()).get(BODY).getAsString());
    }

    public String getLatestReleaseID() {
        JsonArray jsonArray = getReleases().getAsJsonArray();
        return jsonArray.size() > 0 ? jsonArray.get(0).getAsJsonObject().get("id").getAsString() : null;
    }

    public String getLatestVersionDownloadURL() {
        JsonArray jsonArray = getReleaseData(getLatestReleaseID()).getAsJsonArray(ASSETS);
        return jsonArray.size() > 0 ? jsonArray.get(0).getAsJsonObject().get(BROWSER_DOWNLOAD_URL).getAsString() : null;
    }

    private JsonObject getReleaseData(String releaseID) {
        String releaseInfoURL = githubRepositoryURL + RELEASES_URL + "/" + releaseID;
        return getJsonObjectFromRequest(releaseInfoURL);
    }

    private JsonArray getReleases() {
        String releasesURL = githubRepositoryURL + RELEASES_URL;
        return getJsonArrayFromRequest(releasesURL);
    }

    private boolean isUpdateAvailable() {
        return !currentVersion.equals(latestVersionTag);
    }

    private List<String> parseModifications(String body) {
        List<String> changelog = new ArrayList<>();
        String[] changelogUnformatted = body.replace("\r", "").split("\n");

        for (String line : changelogUnformatted) {
            if (!line.isEmpty()) {
                if (!line.startsWith("-")) {
                    line = "- " + line;
                }
                changelog.add(line.replace("-", "•"));
            }
        }
        return changelog;
    }

    private JsonObject getJsonObjectFromRequest(String url) {
        Request<String> request = new Request<>(url);

        if (!request.execute()) {
            throw new RuntimeException("Cannot get release information for update checker: " + request.getException());
        }

        String responseData = request.getResponse().getData();
        return new JsonParser().parse(responseData).getAsJsonObject();
    }

    private JsonArray getJsonArrayFromRequest(String url) {
        Request<String> request = new Request<>(url);

        if (!request.execute()) {
            throw new RuntimeException("Cannot get releases for update checker: " + request.getException());
        }

        String responseData = request.getResponse().getData();
        return new JsonParser().parse(responseData).getAsJsonArray();
    }
}