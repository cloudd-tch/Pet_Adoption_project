package org.example;
import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import static org.example.Validator.validateCredentials;

public class PetShelterGUI {
    static DatabaseHandler db = new DatabaseHandler();
    static User currentUser = null;
    static Pet newPet = null;

    static class CustomOutputStream extends OutputStream {
        private JTextArea textArea;
        public CustomOutputStream(JTextArea textArea) { this.textArea = textArea; }
        @Override
        public void write(int b) { textArea.append(String.valueOf((char)b)); }
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusable(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        return button;
    }

    public static JFrame createWindow(String title, int width, int height) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setLayout(new GridLayout(0, 1, 10, 10));
        frame.setLocationRelativeTo(null);
        return frame;
    }

    public static void showAddPetWindow(String type, String featureName) {
        JFrame addingWindow = createWindow("Add " + type + " ^ ^", 600, 400);
        addingWindow.setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextField nameField = new JTextField();
        JTextField yearsField = new JTextField("Years_");
        JTextField monthsField = new JTextField("Months_");
        JLabel fLabel = new JLabel(featureName);
        JTextField featureField = new JTextField();
        JButton addBtn = createButton("Add Pet to Shelter");
        inputPanel.add(new JLabel("Pet Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(yearsField);
        inputPanel.add(monthsField);
        inputPanel.add(fLabel);
        inputPanel.add(featureField);
        addingWindow.add(inputPanel, BorderLayout.CENTER);
        JComboBox<String> talkChoice = new JComboBox<>(new String[]{"Yes", "No"});
        if (type.equalsIgnoreCase("Parrot")) {
            inputPanel.add(new JLabel("Can talk?"));
            inputPanel.add(talkChoice);
        }
        JTextArea consoleArea = new JTextArea(15, 20);
        consoleArea.setEditable(false);
        consoleArea.setBackground(Color.BLACK);
        consoleArea.setForeground(Color.PINK);
        addingWindow.add(inputPanel, BorderLayout.WEST);
        addingWindow.add(new JScrollPane(consoleArea), BorderLayout.CENTER);
        addingWindow.add(addBtn, BorderLayout.SOUTH);
        addBtn.addActionListener(_ -> {
            try {
                String finalName = nameField.getText();
                String finalFeature = featureField.getText().toLowerCase();
                int finalYears = Integer.parseInt(yearsField.getText().trim());
                int finalMonths = Integer.parseInt(monthsField.getText().trim());
                int totalMonths = (finalYears * 12) + finalMonths;
                Validator.validateOnlyLetters(finalName, "Name");
                Validator.validateOnlyLetters(finalFeature, featureName);
                Validator.totalMonths(totalMonths);
                boolean canTalkStatus = false;
                if (type.equalsIgnoreCase("Dog")) {
                    newPet = new Dog(0, finalName, totalMonths, finalFeature);
                } else if (type.equalsIgnoreCase("Cat")) {
                    newPet = new Cat(0, finalName, totalMonths, finalFeature);
                } else if (type.equalsIgnoreCase("Parrot")) {
                    canTalkStatus = "Yes".equals(talkChoice.getSelectedItem());
                    newPet = new Parrot(0, finalName, totalMonths, finalFeature, canTalkStatus);
                }
                if (newPet != null) {
                    System.setOut(new PrintStream(new CustomOutputStream(consoleArea)));
                    if (db.isValueExists("pets", "name", finalName)) {
                        JOptionPane.showMessageDialog(addingWindow, "A pet with this name already exists!");
                        return;
                    }
                    db.addPet(type, finalName, totalMonths, finalFeature, canTalkStatus);
                    System.out.println("A new pet with ID: " + db.generatedId + " has been added!");
                    System.out.println("_ _ _ _ _ _ *");
                    System.out.println(newPet.getName() + ":");
                    newPet.makeSound();
                    System.out.println("_ _ _ _ _ _ *");
                    nameField.setText("");
                    yearsField.setText("Years_ ");
                    monthsField.setText("Months_ ");
                    featureField.setText("");
                    if (type.equalsIgnoreCase("Parrot")) {
                        talkChoice.setSelectedIndex(1);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addingWindow, "Please enter integer numbers for age!");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(addingWindow, ex.getMessage());
            } catch (Exception ex) {
                consoleArea.append("Error: " + ex.getMessage() + "\n");
            }
        });
        addingWindow.setVisible(true);
    }

    public static JFrame openMenu(JFrame frame, User currentUser) {
        frame.getContentPane().removeAll();
        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        JButton create = createButton("Add Pets");
        JButton read = createButton("Show Pets");
        JButton update = createButton("Update Pets");
        JButton delete = createButton("Remove Pets");
        if(currentUser.getRole().equals("ADMIN")) {
            menuPanel.add(create);
            menuPanel.add(read);
            menuPanel.add(update);
            menuPanel.add(delete);
        } else {
            menuPanel.add(read);
        }

        // CREATE
        create.addActionListener(_ -> {
            JFrame addFrame = createWindow("Add Pets", 500, 300);
            JLabel choose = new JLabel("Please choose the type of pet to add ^^");
            JButton dog = createButton("Dog (U^ I ^U)");
            JButton cat = createButton("Cat (^. _ .^)");
            JButton parrot = createButton("Parrot <(``)//");
            addFrame.setLayout(new BorderLayout(10, 10));
            JPanel choosePanel = new JPanel(new FlowLayout());
            choosePanel.add(choose);
            choosePanel.add(dog);
            choosePanel.add(cat);
            choosePanel.add(parrot);
            addFrame.add(choosePanel, BorderLayout.CENTER);
            dog.addActionListener(_ -> {
                addFrame.dispose();
                showAddPetWindow("Dog", "Breed:");
            });
            cat.addActionListener(_ -> {
                addFrame.dispose();
                showAddPetWindow("Cat", "Color:");
            });
            parrot.addActionListener(_ -> {
                addFrame.dispose();
                showAddPetWindow("Parrot", "Specie:");
            });
            addFrame.setVisible(true);
        });

        // READ
        read.addActionListener(_ -> {
            JFrame showFrame = createWindow("Pet List", 700, 500);
            showFrame.setLayout(new BorderLayout(10, 10));
            JPanel topPanel = new JPanel(new FlowLayout());
            JButton all = createButton("ShowAll");
            JButton dogs = createButton("Dogs");
            JButton cats = createButton("Cats");
            JButton parrots = createButton("Parrots");
            topPanel.add(all);
            topPanel.add(dogs);
            topPanel.add(cats);
            topPanel.add(parrots);
            JTextArea consoleArea = new JTextArea();
            consoleArea.setBackground(Color.BLACK);
            consoleArea.setForeground(Color.PINK);
            consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            consoleArea.setEditable(false);
            JScrollPane scroll = new JScrollPane(consoleArea);
            showFrame.add(topPanel, BorderLayout.NORTH);
            showFrame.add(scroll, BorderLayout.CENTER);

            all.addActionListener(_ -> {
                consoleArea.setText("");
                PrintStream stream = new PrintStream(new CustomOutputStream(consoleArea));
                System.setOut(stream);
                PetActions.showAll(db);
            });

            dogs.addActionListener(_ -> {
                consoleArea.setText("");
                PrintStream stream = new PrintStream(new CustomOutputStream(consoleArea));
                System.setOut(stream);
                PetActions.showDogs(db);
            });

            cats.addActionListener(_ -> {
                consoleArea.setText("");
                PrintStream stream = new PrintStream(new CustomOutputStream(consoleArea));
                System.setOut(stream);
                PetActions.showCats(db);
            });

            parrots.addActionListener(_ -> {
                consoleArea.setText("");
                PrintStream stream = new PrintStream(new CustomOutputStream(consoleArea));
                System.setOut(stream);
                PetActions.showParrots(db);
            });
            showFrame.setVisible(true);
        });

        // UPDATE
        update.addActionListener(_ -> {
            JFrame updateFrame = createWindow("Update Pet Info", 700, 500);
            updateFrame.setLayout(new BorderLayout());
            JPanel inputPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            JLabel uu = new JLabel("Please enter the pet's ID:");
            JTextField idField = new JTextField();
            JButton findBtn = createButton("Find Pet");
            JTextField nameField = new JTextField();
            JLabel age = new JLabel("Age:");
            JLabel yearsL = new JLabel("Full years");
            JTextField yearField = new JTextField();
            JLabel monthsL = new JLabel("Months");
            JTextField monthsField = new JTextField();
            JTextField featureField = new JTextField();

            inputPanel.add(uu);
            inputPanel.add(idField);
            inputPanel.add(findBtn);
            inputPanel.add(nameField);
            inputPanel.add(age);
            inputPanel.add(yearsL);
            inputPanel.add(yearField);
            inputPanel.add(monthsL);
            inputPanel.add(monthsField);
            inputPanel.add(featureField);

            updateFrame.add(new JScrollPane(inputPanel), BorderLayout.CENTER);
            JButton saveBtn = createButton("Confirm and Update");

            JLabel talkLabel = new JLabel("Can the parrot talk?");
            JComboBox<String> talkChoice = new JComboBox<>(new String[]{"Yes", "No"});
            talkLabel.setVisible(false);
            talkChoice.setVisible(false);
            inputPanel.add(talkLabel);
            inputPanel.add(talkChoice);
            inputPanel.setVisible(true);

            findBtn.addActionListener(_ -> {
                try {
                    int id = Integer.parseInt(idField.getText().trim());
                    Pet pet = db.getPetById(id);
                    if (pet != null) {
                        uu.setVisible(false);
                        idField.setEditable(false);
                        findBtn.setVisible(false);
                        nameField.setText(pet.getName());
                        yearField.setText(String.valueOf(pet.getYears()));
                        monthsField.setText(String.valueOf(pet.getMonths()));
                        if (pet instanceof Parrot) {
                            featureField.setText(((Parrot) pet).getFeatures());
                            talkChoice.setVisible(true);
                            talkChoice.setVisible(true);
                            talkLabel.setVisible(true);
                            talkChoice.setSelectedItem(((Parrot) pet).isCanTalk() ? "Yes" : "No");
                        } else {
                            featureField.setText(pet.getFeatures());
                        }
                        inputPanel.add(saveBtn);
                        inputPanel.revalidate();
                        inputPanel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(updateFrame, "Pet not found!");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(updateFrame, "Enter a valid ID");
                }
            });
            saveBtn.addActionListener(_ -> {
                try {
                    int id = Integer.parseInt(idField.getText());
                    Pet pet = db.getPetById(id);
                    String newName = nameField.getText().trim();
                    String finalFeatures = featureField.getText().trim();
                    Validator.validateOnlyLetters(newName, "Name");
                    Validator.validateOnlyLetters(finalFeatures, "Features");
                    int years = Integer.parseInt(yearField.getText().trim());
                    int months = Integer.parseInt(monthsField.getText().trim());
                    int totalMonths = (years * 12) + months;
                    Validator.totalMonths(totalMonths);

                    boolean canTalk = false;
                    if (pet instanceof Parrot)
                        canTalk = "Yes".equals(talkChoice.getSelectedItem());

                    DatabaseHandler.updatePet(id, newName, totalMonths, finalFeatures, canTalk);
                    JOptionPane.showMessageDialog(updateFrame, "Successfully updated!");

                    uu.setVisible(true);
                    idField.setEditable(true);
                    idField.setText("");
                    findBtn.setVisible(true);
                    nameField.setText("");
                    yearField.setText("");
                    monthsField.setText("");
                    featureField.setText("");
                    talkLabel.setVisible(false);
                    talkChoice.setVisible(false);
                    saveBtn.setVisible(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(updateFrame, "Update failed. Check your inputs.");
                }
            });
            updateFrame.setVisible(true);
        });

        // DELETE
        delete.addActionListener(_ -> {
            JFrame deleteFrame = createWindow("Delete", 700, 500);
            JLabel dd = new JLabel("Please enter the ID of the pet:");
            JTextField id = new JTextField();
            JButton remove = new JButton("Remove");
            deleteFrame.add(dd);
            deleteFrame.add(id);
            deleteFrame.add(remove);
            remove.addActionListener(_ -> {
                try {
                    int idd = Integer.parseInt(id.getText());
                    int response = JOptionPane.showConfirmDialog(
                            deleteFrame,
                            "Are you sure you want to remove this pet?",
                            "Confirmation",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (response == JOptionPane.YES_OPTION) {
                        if (db.deletePet(idd)) {
                            JOptionPane.showMessageDialog(deleteFrame, "Pet removed successfully!");
                            deleteFrame.dispose();
                        } else
                            JOptionPane.showMessageDialog(deleteFrame, "Pet with ID " + idd + " not found!");
                    } else {
                        JOptionPane.showMessageDialog(deleteFrame, "The operation has been cancelled.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(deleteFrame, "Please enter a valid number ID.");
                }
            });
            deleteFrame.setVisible(true);
        });

        frame.add(menuPanel);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
        return frame;
    }

    public static JFrame window1(String message, User user, JFrame mainFrame) {
        JFrame afterAuthorization = createWindow("Authorization succeeded!", 200, 100);
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        JButton enterBtn = createButton("enter ->");
        enterBtn.addActionListener(_ -> {
            afterAuthorization.dispose();
            openMenu(mainFrame, currentUser);
            mainFrame.setVisible(true);
        });
        afterAuthorization.setLayout(new BorderLayout());
        afterAuthorization.add(label, BorderLayout.CENTER);
        afterAuthorization.add(enterBtn, BorderLayout.SOUTH);
        afterAuthorization.setVisible(true);
        return afterAuthorization;
    }

    public static void main(String[] args) {
        db.initialize();
        JFrame mainFrame = new JFrame("My Pet Shelter");
        mainFrame.setSize(800, 500);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new FlowLayout());
        JLabel statusLabel = new JLabel("To enter, please log on or register ^^");
        mainFrame.add(statusLabel);
        JButton register = createButton("register");
        JButton login = createButton("log in");
        mainFrame.add(register);
        mainFrame.add(login);
        mainFrame.setVisible(true);

        register.addActionListener(_ -> {
            JFrame regFrame = createWindow("registration ^^", 400, 300);
            regFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JButton button = createButton("confirm");
            JTextField newField = new JTextField();
            JPasswordField newPass = new JPasswordField();
            regFrame.add(new JLabel(" Set a username:"));
            regFrame.add(newField);
            regFrame.add(new JLabel(" Set a password:"));
            regFrame.add(newPass);
            regFrame.add(button);
            button.addActionListener(_ -> {
                try {
                    String user = newField.getText();
                    if (db.isValueExists("users", "username", user)) {
                        JOptionPane.showMessageDialog(regFrame, "User already exists!");
                        return;
                    }
                    String pass = new String(newPass.getPassword());
                    try {
                        validateCredentials(user, newField.getText());
                        validateCredentials(pass, new String(newPass.getPassword()));
                    } catch (Exception r) {
                        JOptionPane.showMessageDialog(regFrame, r);
                        return;
                    }
                    String role = "USER";
                    db.registerUser(user, pass, role);
                    currentUser = new User(user, pass, "USER");
                    window1("Registration complete ^^", currentUser, mainFrame);
                    regFrame.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(regFrame, "registration error: " + ex.getMessage());
                }
            });
            regFrame.setVisible(true);
        });

        login.addActionListener(_ -> {
            JFrame loginFrame = createWindow("log in ^^", 400, 300);
            loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JButton button = createButton("confirm");
            JTextField newField = new JTextField();
            JPasswordField newPass = new JPasswordField();
            loginFrame.add(new JLabel(" Enter a username:"));
            loginFrame.add(newField);
            loginFrame.add(new JLabel(" Enter a password:"));
            loginFrame.add(newPass);
            loginFrame.add(button);
            button.addActionListener(_ -> {
                String user = newField.getText();
                String pass = new String(newPass.getPassword());
                currentUser = db.loginUser(user, pass);
                if (currentUser != null) {
                    window1("Welcome back ^^", currentUser, mainFrame);
                    loginFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid login or password.");
                }
            });
            loginFrame.setVisible(true);
        });
    }
}
