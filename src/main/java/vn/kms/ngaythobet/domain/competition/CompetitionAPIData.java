package vn.kms.ngaythobet.domain.competition;


import lombok.Data;
import org.springframework.http.HttpHeaders;
import vn.kms.ngaythobet.domain.util.exception.InvalidCompetitionDataException;
import vn.kms.ngaythobet.web.util.JsonResponse;

@Data
public class CompetitionAPIData {
    private final String API_URI = "http://api.football-data.org";
    private final String API_COMPETITION = API_URI + "/v1/competitions/%s";
    private final String API_COMPETITORS = API_URI + "/v1/competitions/%s/teams";
    private final String API_MATCHES = API_URI + "/v1/competitions/%s/fixtures";
    private final String API_AUTH_TOKEN = "a8696d87ccca492aaabc9fb7400d182e";

    Competition competition;
    Competitors competitors;
    Matches matches;
    String competitionName;
    String aliasKey;
    String externalApiId;
    private JsonResponse competitionResp;
    private JsonResponse competitorsResp;
    private JsonResponse matchesResp;

    public CompetitionAPIData(String competitionName, String aliasKey, String externalApiId) {
        this.competitionResp = new JsonResponse<>(Competition.class);
        this.competitorsResp = new JsonResponse<>(Competitors.class);
        this.matchesResp = new JsonResponse<>(Matches.class);
        this.competitionName = competitionName;
        this.aliasKey = aliasKey;
        this.externalApiId = externalApiId;

    }

    public void init() throws InvalidCompetitionDataException {
        getCompetitionData(externalApiId);

        competition.setName(competitionName);
        competition.setAliasKey(aliasKey);
    }


    private void getCompetitionData(String key) throws InvalidCompetitionDataException {
        try {
            if (key.contains("/")) {
                throw new InvalidCompetitionDataException("Invalid competition ID.");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Token", API_AUTH_TOKEN);
            headers.set("X-Response-Control", "minified");

            competition = (Competition) competitionResp.request(String.format(API_COMPETITION, key), headers);
            competitors = (Competitors) competitorsResp.request(String.format(API_COMPETITORS, key), headers);
            matches = (Matches) matchesResp.request(String.format(API_MATCHES, key), headers);
        } catch (InvalidCompetitionDataException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidCompetitionDataException("Either invalid ID or invalid json format.", e);
        }
    }
}
