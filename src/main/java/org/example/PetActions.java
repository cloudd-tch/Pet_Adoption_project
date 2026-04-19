package org.example;
import java.util.Scanner;

import static org.example.PetShelterGUI.db;

public class PetActions {
    private static final Scanner sc = new Scanner(System.in);
    static String typeForShow;

    // CREATE
    static void add(String[] parts, DatabaseHandler db) {
        Pet newPet = null;
        boolean done = false;
        String type;
        String name;
        int totalMonths;
        String features = "";
        while (!done) {
            if (parts.length < 2) {
                System.out.println("Please enter the type of pet to add:");
                type = sc.nextLine().trim().toLowerCase();
                if (!type.equals("dog") && !type.equals("cat") && !type.equals("parrot")) {
                    System.out.println("Unknown type! Returning to menu...");
                    return;
                }
            } else if (parts.length > 2) {
                System.out.println("You can only add one pet at a time.");
                return;
            } else {
                type = parts[1].trim();
                if (!type.equals("dog") && !type.equals("cat") && !type.equals("parrot")) {
                    System.out.println("Unknown type! Returning to menu...");
                    return;
                }
            }
                System.out.println("Give the pet a name ^^");
                name = sc.nextLine();
            if (db.isValueExists("pets", "name", name)) {
                System.out.println("A pet with this name already exists!" +
                        "Back to menu..");
                return;
            }
                Validator.validateOnlyLetters(name, "Name");
                System.out.println("How old is the pet?");
                System.out.println("Full years: ");
                int years = Integer.parseInt(sc.nextLine());
                Validator.validateOnlyNumbers(years, "Years");
                System.out.println("Months: ");
                int months = Integer.parseInt(sc.nextLine());
                Validator.validateOnlyNumbers(months, "Months");
                totalMonths = (years * 12) + months;
                Validator.totalMonths(totalMonths);
                if (type.equals("dog")) {
                    System.out.println("What breed is the dog?");
                    features = sc.nextLine().toLowerCase();
                    Validator.validateOnlyLetters(features, "Breed");
                    newPet = new Dog(0, name, totalMonths, features);
                } else if (type.equalsIgnoreCase("cat")) {
                    System.out.println("What color is the cat?");
                    features = sc.nextLine().toLowerCase();
                    Validator.validateOnlyLetters(features, "Color");
                    newPet = new Cat(0, name, totalMonths, features);
                } else if(type.equals("parrot")) {
                    System.out.println("What specie is the parrot?");
                    String specie = sc.nextLine();
                    Validator.validateOnlyLetters(specie, "Specie");
                    System.out.println("Can the parrot talk?");
                    String talking = sc.nextLine().trim().toLowerCase();
                    boolean canTalk = talking.equals("yes") || talking.equals("y");
                    newPet = new Parrot(0, name, totalMonths, specie, canTalk);
                    features = specie;
                }

            if (newPet != null) {
                boolean currentCanTalk = (newPet instanceof Parrot p) && p.isCanTalk();
                db.addPet(type, name, totalMonths, features, currentCanTalk);
                System.out.println("A new pet with ID: " + db.generatedId + " has been added!");
                System.out.println("_ _ _ _ _ _ *");
                System.out.println(newPet.getName() + ":");
                newPet.makeSound();
                System.out.println("_ _ _ _ _ _ *");
            }
                System.out.println("Add one more pet?");
                System.out.println("(Yes (add) / No (back to menu))");
                String answer = sc.nextLine();
                if (answer.equalsIgnoreCase("yes")) {
                    parts = new String[]{"add"};
                } else {
                    System.out.println("Returning to menu...");
                    done = true;
                }
        }
    }

        // READ
        static void showAll (DatabaseHandler db) {
            System.out.println("======= PET LIST =======");
            if (!db.petsByType("all")) {
                System.out.println("The shelter is empty :(");
            }
        }

        static void showDogs (DatabaseHandler db) {
            System.out.println(" --- (U^ I ^U) ---");
            boolean found = db.petsByType("Dog");
            if (!found) {
                System.out.println("No dogs in the list yet :(");
            }
        }
        static void showCats (DatabaseHandler db) {
            System.out.println(" --- (^. _ .^) ---");
            boolean found = db.petsByType("Cat");
            if (!found) {
                System.out.println("No cats in the list yet :(");
            }
        }
        static void showParrots (DatabaseHandler db) {
            System.out.println(" --- <(``)// ---");
            boolean found = db.petsByType("Parrot");
            if (!found) {
                System.out.println("No parrots in the list yet :(");
            }
        }

        // UPDATE
        public static void update (String[]parts, DatabaseHandler db){
            boolean done = false;
            int idToUpdate;
            Pet foundPet;
            if (parts.length < 2) {
                System.out.println("Please enter the pet's ID:");
                idToUpdate = Integer.parseInt(sc.nextLine());
            } else if (parts.length > 2) {
                System.out.println("You can only update one pet at a time.");
                return;
            } else {
                idToUpdate = Integer.parseInt(parts[1]);
            }
            foundPet = db.getPetById(idToUpdate);
            if (foundPet == null) {
                System.out.println("Error: Pet with ID " + idToUpdate + " does not exist!");
                return;
            }
            while (!done) {
                System.out.println("Available: name, age, features.");
                System.out.println("'Exit' to save and return to menu.");
                System.out.print("Your choice: ");
                String information = sc.nextLine();
                switch (information.toLowerCase()) {
                    case "name":
                        System.out.println("Type the new name: ");
                        String name = sc.nextLine();
                        if (db.isValueExists("pets", "name", name)) {
                            System.out.println("A pet with this name already exists!");
                            return;
                        }
                        foundPet.setName(name);
                        break;
                    case "age":
                        System.out.println("Current age: " +
                                "Years: " + foundPet.getYears() + ", Months: " + foundPet.getMonths());
                        System.out.println("Enter the updated age-- ");
                        System.out.println("Full years: ");
                        int years = Integer.parseInt(sc.nextLine());
                        Validator.validateOnlyNumbers(years, "Years");
                        System.out.println("Months: ");
                        int months = Integer.parseInt(sc.nextLine());
                        Validator.validateOnlyNumbers(months, "Months");
                        int totalMonths = (years * 12) + months;
                        Validator.totalMonths(totalMonths);
                        foundPet.setTotalMonths(totalMonths);
                        break;
                    case "features":
                        if (foundPet.getType().equalsIgnoreCase("Parrot")) {
                            Parrot p = (Parrot) foundPet;
                            System.out.println("Current features: -- " + p.getFeatures());
                            System.out.println("Update the parrot's specie or speech?");
                            String updateChoice = sc.nextLine().trim().toLowerCase();
                            switch (updateChoice) {
                                case "specie":
                                    System.out.println("Enter new specie: ");
                                    String newSpecie = sc.nextLine().trim().toLowerCase();
                                    Validator.validateOnlyLetters(newSpecie, "Specie");
                                    p.setSpecie(newSpecie);
                                    break;
                                case "speech":
                                    System.out.println("Can " + p.getName() + " talk now? (yes/no): ");
                                    String answer = sc.nextLine().trim().toLowerCase();
                                    boolean talks = answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y");
                                    p.setCanTalk(talks);
                                    if (!answer.equals("yes") && !answer.equals("y") && !answer.equals("no") && !answer.equals("n"))
                                        System.out.println("Invalid input. Noted as 'No'.");
                                    break;
                                default:
                                    System.out.println("Unknown option.");
                                    break;
                            }
                        } else {
                            System.out.println("Current features: " + foundPet.getFeatures());
                            System.out.println("Enter the updated feature: ");
                            String feature = sc.nextLine().trim().toLowerCase();
                            Validator.validateOnlyLetters(feature, "Feature");
                            foundPet.setFeature(feature);
                        }
                       break;
                    case "exit":
                        boolean canTalk = false;
                        if (foundPet instanceof Parrot p)
                            canTalk = p.isCanTalk();
                        DatabaseHandler.updatePet(idToUpdate, foundPet.getName(),
                                foundPet.getTotalMonths(),
                                foundPet.getFeatures(), canTalk);
                        done = true;
                        break;
                    default:
                        System.out.println("This feature does not exist.");
                        break;
                }
                System.out.println("Updated!" +
                        "Changes have been saved ^^");
                System.out.println("Update more features?" +
                        "(Yes (update) / No (back to menu))");
                String answer = sc.nextLine();
                if (answer.equalsIgnoreCase("yes")) {
                    System.out.println("Please enter the pet's ID:");
                    idToUpdate = Integer.parseInt(sc.nextLine());
                } else {
                    System.out.println("Returning to menu...");
                    done = true;
                }
            }
        }

        // DELETE
        public static void delete (String[]parts, DatabaseHandler db){
            int idToDelete;
            if (parts.length < 2) {
                System.out.println("Please enter the pet's ID:");
                idToDelete = Integer.parseInt(sc.nextLine());
            } else if (parts.length > 2) {
                System.out.println("You can only remove one pet at a time.");
                return;
            } else {
                idToDelete = Integer.parseInt(parts[1]);
            }
            System.out.println("Remove pet# " + idToDelete + "?");
            System.out.println("(Yes / No ?)");
            String answer = sc.nextLine();
            if (answer.equalsIgnoreCase("yes")) {
                db.deletePet(idToDelete);
                System.out.println("The pet has been removed.");
            } else {
                System.out.println("Deletion has been cancelled.");
            }
        }
    }

