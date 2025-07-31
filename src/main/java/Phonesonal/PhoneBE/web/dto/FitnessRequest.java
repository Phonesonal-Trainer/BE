package Phonesonal.PhoneBE.web.dto;

// DTO 클래스
public class FitnessRequest {
    private int weight;
    private int height;
//    private double bodyFatPercentage;
//    private double muscleMass;

    // getter, setter
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

//    public double getBodyFatPercentage() { return bodyFatPercentage; }
//    public void setBodyFatPercentage(double bodyFatPercentage) { this.bodyFatPercentage = bodyFatPercentage; }
//
//    public double getMuscleMass() { return muscleMass; }
//    public void setMuscleMass(double muscleMass) { this.muscleMass = muscleMass; }
}