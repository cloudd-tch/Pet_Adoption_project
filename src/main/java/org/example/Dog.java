package org.example;

public class Dog extends Pet {
    private String breed;
    public Dog(int id, String name, int totalMonths, String breed) {
        super(id, name, totalMonths,"Dog");
        this.breed = breed;
    }
    @Override
    public void setFeature(String breed) {
        this.breed = breed;
    }
    @Override
    public String getFeatures() { return this.breed; }
    @Override
    public void makeSound() {
        System.out.println("Bark! Bark!");
    }
}
