package uk.gov.homeoffice.cts.behaviour;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.gov.homeoffice.cts.model.CorrespondenceType.*;

/**
 * Created by chris on 29/08/2014.
 */
public class CreateCaseBehaviourTest {

    CreateCaseBehaviour createCaseBehaviour;
    String parlyUnit = "GROUP_Parly Unit";
    String parlyTeam = "GROUP_Parly Team";

    @Before
    public void setUp(){
        createCaseBehaviour = new CreateCaseBehaviour();

        Map<String,String> unitsMap = new HashMap<>();
        unitsMap.put(ORDINARY_WRITTEN.getCode(), parlyUnit);
        unitsMap.put(LORDS_WRITTEN.getCode(), parlyUnit);
        unitsMap.put(NAMED_DAY.getCode(), parlyUnit);

        createCaseBehaviour.setUnits(unitsMap);

        Map<String,String> teamsMap = new HashMap<>();
        teamsMap.put(ORDINARY_WRITTEN.getCode(), parlyTeam);
        teamsMap.put(LORDS_WRITTEN.getCode(), parlyTeam);
        teamsMap.put(NAMED_DAY.getCode(), parlyTeam);

        createCaseBehaviour.setTeams(teamsMap);
    }
    @Test
    public void testUnitAllocate(){
        String group = createCaseBehaviour.getAutoAllocateUnit(ORDINARY_WRITTEN.getCode());
        assertEquals(parlyUnit, group);
    }
    @Test
    public void testUnitAllocateNull(){
        assertNull(createCaseBehaviour.getAutoAllocateUnit(null));
    }

    @Test
    public void testUnitAllocateEmpty(){
        assertNull(createCaseBehaviour.getAutoAllocateUnit(""));
    }

    @Test
    public void testUnitAllocateNoUnit(){
        assertNull(createCaseBehaviour.getAutoAllocateUnit("aunit"));
    }


    @Test
    public void testTeamAllocate(){
        String group = createCaseBehaviour.getAutoAllocateTeam(ORDINARY_WRITTEN.getCode());
        assertEquals(parlyTeam, group);
    }
    @Test
    public void testTeamAllocateNull(){
        assertNull(createCaseBehaviour.getAutoAllocateTeam(null));
    }

    @Test
    public void testTeamAllocateEmpty(){
        assertNull(createCaseBehaviour.getAutoAllocateTeam(""));
    }

    @Test
    public void testTeamAllocateNoUnit(){
        assertNull(createCaseBehaviour.getAutoAllocateTeam("ateam"));
    }

}
