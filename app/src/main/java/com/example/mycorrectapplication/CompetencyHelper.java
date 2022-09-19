package com.example.mycorrectapplication;

import org.json.JSONException;
import org.json.JSONObject;

public class CompetencyHelper {

    public JSONObject calculateNextDifficultyLevel(JSONObject obj, int count) throws JSONException {
        String difficulty_level = obj.getString("difficulty_level");
        int attempt = obj.getInt("attempt");
        int total_weight = obj.getInt("total_weight");
        double final_score = obj.getDouble("final_score");
        double cumulative_total = obj.getDouble("cumulative_total");

        int weight = setWeight(difficulty_level);
        double score = setScore(attempt);
        double total = score * weight;
        total_weight += weight;

        if (count > 1 && score == 0) {
            difficulty_level = setAdjustedDifficultyLevel(difficulty_level, final_score);
            cumulative_total = final_score * weight;
        }
        else {
            cumulative_total += total;
            final_score = cumulative_total/total_weight;
            difficulty_level = setNewDifficultyLevel(difficulty_level, final_score, count);
        }

        obj.put("difficulty_level", difficulty_level);
        obj.put("total_weight", total_weight);
        obj.put("final_score", final_score);
        obj.put("cumulative_total", cumulative_total);
        return obj;
    }

    public int setScore(int attempt) {
        if (attempt == 1) {
            return 100;
        } else if (attempt == 2) {
            return 80;
        } else if (attempt == 3) {
            return 60;
        } else if (attempt == 4) {
            return 40;
        } else if (attempt == 5) {
            return 20;
        } else {
            return 0;
        }
    }

    public int setWeight(String difficulty_level) {
        if (difficulty_level == "L") {
            return 1;
        } else if (difficulty_level == "M") {
            return 2;
        } else {
            return 3;
        }
    }

    public String setAdjustedDifficultyLevel(String difficulty_level, double final_score) {
        String[] array = new String[0];
        if (difficulty_level == "H") {
            difficulty_level = "M";
            final_score = ((30+70/2) + 70)/2;
        } else if (difficulty_level == "M") {
            difficulty_level = "L";
            final_score = ((0+30/2) + 30)/2;
        } else if (difficulty_level == "L" && final_score > ((0+30/2) + 30)/2) {
            final_score = ((0+30/2) + 30)/2;
        }
        return difficulty_level;
    }

    private String setNewDifficultyLevel(String difficulty_level, double final_score, int count) {
        if (final_score > 0 && final_score <= 30) {
            difficulty_level = "L";
        } else if (final_score > 30 && final_score <= 70) {
            difficulty_level = "M";
        } else if (final_score > 70 && final_score <= 100) {
            difficulty_level = "H";
        }
        return difficulty_level;
    }
}