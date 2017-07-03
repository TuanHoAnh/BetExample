package vn.kms.ngaythobet.domain.competition;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import vn.kms.ngaythobet.domain.util.exception.InvalidCompetitionDataException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class CompetitionAPIDataTest {
    final String KEY = "424";
    final String NAME = "WORLD CUP";
    final String ALIAS_KEY = "123";

    @Rule
    public ExpectedException exception;

    CompetitionAPIData competitionAPIData;
    Method getCompetitionData;
    Field competitionField;
    Field competitorsField;
    Field matchesField;

    private void getData() throws Exception{
        competitionAPIData = new CompetitionAPIData(NAME, ALIAS_KEY, KEY);
        competitionAPIData.init();

        getCompetitionData = CompetitionAPIData.class.getDeclaredMethod("getCompetitionData", String.class);
        getCompetitionData.setAccessible(true);

        competitionField = CompetitionAPIData.class.getDeclaredField("competition");
        competitionField.setAccessible(true);

        competitorsField = CompetitionAPIData.class.getDeclaredField("competitors");
        competitorsField.setAccessible(true);

        matchesField = CompetitionAPIData.class.getDeclaredField("matches");
        matchesField.setAccessible(true);
    }

    @Test
    public void testInitGetCompetition() throws Exception{
        getData();

        assertTrue(competitionField.get(competitionAPIData) != null
            && competitorsField.get(competitionAPIData) != null
            && matchesField.get(competitionAPIData) != null);
    }

    @Test
    public void testInitCompetition() throws Exception {
        getData();

        Competition competition = (Competition) competitionField.get(competitionAPIData);

        assertTrue(competition.getName().equals(NAME)
            && competition.getAliasKey().equals(ALIAS_KEY));
    }

    @Test(expected = InvalidCompetitionDataException.class)
    public void testInitIDInvalid() throws Exception {
        competitionAPIData = new CompetitionAPIData(NAME, ALIAS_KEY, "424/teams");
        competitionAPIData.init();
    }

    @Test(expected = Exception.class)
    public void testInitIDNotFound() throws Exception {
        competitionAPIData = new CompetitionAPIData(NAME, ALIAS_KEY, "199x");
        competitionAPIData.init();
    }

}
