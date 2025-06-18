package com.replan.domain.objects;

public enum PracticeFocus {
    AIM_TRAINING("Aim Training", "Improve accuracy and precision"),
    GAME_SENSE("Game Sense", "Develop decision-making and awareness"),
    MOVEMENT_MECHANICS("Movement", "Master character movement and positioning"),
    MAP_KNOWLEDGE("Map Knowledge", "Learn callouts, angles, and rotations"),
    TEAM_COORDINATION("Team Coordination", "Practice team plays and communication"),
    STRATEGY_REVIEW("Strategy Review", "Study tactics and team compositions"),
    VOD_ANALYSIS("VOD Analysis", "Review gameplay footage for improvement"),
    COMMUNICATION("Communication", "Improve callouts and team communication"),
    POST_PLANT_SCENARIOS("Post-Plant Scenarios", "Practice post-plant positioning and retakes"),
    SITE_HOLDS("Site Holds", "Master defensive positioning and site anchoring");

    private final String displayName;
    private final String description;

    PracticeFocus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}