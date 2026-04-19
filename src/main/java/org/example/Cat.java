package org.example;

public class Cat extends Pet {
    private String color;
    public Cat(int id, String name, int totalMonths, String color) {
        super(id, name, totalMonths, "Cat");
        this.color = color;
    }
    @Override
    public void setFeature(String color) {
        this.color = color;
    }
    @Override
    public String getFeatures() { return this.color; }
    @Override
    public void makeSound() {
        System.out.println("Meow...");
    }
}