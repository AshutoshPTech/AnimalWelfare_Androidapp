package com.Pie4u.animalcare;

public class RescueCard {

    private String animalType;
    private String animalLocationLandmark;
    private String rescueStatus;

    public RescueCard(String animalType, String animalLocationLandmark, String rescueStatus) {
        this.animalType = animalType;
        this.animalLocationLandmark = animalLocationLandmark;
        this.rescueStatus = rescueStatus;
    }

    public String getAnimalType() {
        return animalType;
    }

    public String getAnimalLocationLandmark() {
        return animalLocationLandmark;
    }

    public String getRescueStatus() {
        return rescueStatus;
    }
}
