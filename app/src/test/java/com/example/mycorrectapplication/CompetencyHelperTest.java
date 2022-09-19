package com.example.mycorrectapplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class CompetencyHelperTest {
    CompetencyHelper ch = new CompetencyHelper();
    @Test
    public void calculateNextDifficultyLevel_ReturnsTrue() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("difficulty_level", "L");
        obj.put("total_weight", 0);
        obj.put("final_score", 0F);
        obj.put("cumulative_total", 0);
        obj.put("attempt", 1);

        // method call
        JSONObject returnedFromClass = ch.calculateNextDifficultyLevel(obj, 1);

        // expect - actual
        assertEquals("H", returnedFromClass.getString("difficulty_level"));
    }

    @Test
    public void setScore_Assert() {
        int returnedFromClass = ch.setScore(3);
        assertEquals(returnedFromClass, 60);
        assertNotEquals(returnedFromClass, 30);
    }

    @Test
    public void setWeight_Assert() {
        int returnedFromClass = ch.setWeight("L");
        assertEquals(returnedFromClass, 1);
    }

    @Test
    public void setAdjustedDifficultyLevel_Assert() {
        String returnedFromClass = ch.setAdjustedDifficultyLevel("M", 0);
        assertEquals("L", returnedFromClass);
    }
}


