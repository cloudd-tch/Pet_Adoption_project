package org.example;

public class Parrot extends Pet {
    private String specie;
    private boolean canTalk;

    public Parrot(int id, String name, int totalMonths, String specie, boolean canTalk) {
        super(id, name, totalMonths, "Parrot");
        setSpecie(specie);
        this.canTalk = canTalk;
    }
    public void setSpecie(String specie) {
        Validator.validateOnlyLetters(specie, "Specie");
        this.specie = specie;
    }
    @Override
    public void setFeature(String specie) {
        setSpecie(specie);
    }
    @Override
    public String getFeatures() { return this.specie; }
    public boolean isCanTalk() { return canTalk; }
    public void setCanTalk(boolean canTalk) { this.canTalk = canTalk; }
    @Override
    public String getDescription() {
        return super.getDescription() + " | Can talk: " + (canTalk ? "Yes" : "No");
    }
    @Override
    public void makeSound() {
        if(canTalk)
            System.out.println("Hello! Squawk.." + getName() + " wants a cracker!");
        else
            System.out.println("Screeech! Chirp!");
    }
}