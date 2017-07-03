package vn.kms.ngaythobet.web.util;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import vn.kms.ngaythobet.domain.competition.Competition;
import vn.kms.ngaythobet.domain.competition.Competitors;
import vn.kms.ngaythobet.domain.competition.Matches;

import static org.junit.Assert.*;


public class JsonResponseTest {
    private HttpHeaders headers;

    @Before
    public void startUp() {
        headers = new HttpHeaders();
        headers.set("X-Auth-Token", "a8696d87ccca492aaabc9fb7400d182e");
        headers.set("X-Response-Control", "minified");
    }

    @Test
    public void testRequestForCompetition() throws Exception {
        final String API_URI = "http://api.football-data.org/v1/competitions/424";
        JsonResponse<Competition> jsonResponse;

        jsonResponse = new JsonResponse<>(Competition.class);
        assertNotNull(jsonResponse.request(API_URI, headers));
    }

    @Test
    public void testRequestForCompetitors() throws Exception {
        final String API_URI = "http://api.football-data.org/v1/competitions/424/teams";
        JsonResponse<Competitors> jsonResponse;

        jsonResponse = new JsonResponse<>(Competitors.class);
        assertNotNull(jsonResponse.request(API_URI, headers));
    }

    @Test
    public void testRequestForMatches() throws Exception {
        final String API_URI = "http://api.football-data.org/v1/competitions/424/fixtures";
        JsonResponse<Matches> jsonResponse;

        jsonResponse = new JsonResponse<>(Matches.class);
        assertNotNull(jsonResponse.request(API_URI, headers));
    }

    @Test
    public void testSetClassType() throws Exception {
        final String API_URI = "http://api.football-data.org/v1/competitions/424";
        JsonResponse<Competition> jsonResponse;

        jsonResponse = new JsonResponse<>(Competition.class);
        assertEquals(jsonResponse.getType(), Competition.class);
    }
}
