package org.example;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Dog.class, name = "dog"),
        @JsonSubTypes.Type(value = Cat.class, name = "cat")
})
abstract public class Pet {
    private int id;
    private String name;
    private int totalMonths;
    private String type;

    public Pet(int id, String name, int totalMonths,  String type) {
        this.id = id;
        setName(name);
        setTotalMonths(totalMonths);
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setFeature(String feature) {
        Validator.validateOnlyLetters(feature, "Features");
        getFeatures();
    }
    public void setTotalMonths(int totalMonths) {
        this.totalMonths = totalMonths;
    }
    public String getType() {
        return type;
    }
    public String getName() { return name; }
    public int getYears() { return totalMonths / 12; }
    public int getMonths() { return totalMonths % 12; }
    public int getTotalMonths() { return totalMonths; }
    public abstract String getFeatures();
    public String getDescription() {
        return "Type: " + type + " | Name: " + name +
                " | Age: " + getYears() + "y " + getMonths() + "m" +
                " | Features: " + getFeatures();
    }
    public abstract void makeSound();
}
