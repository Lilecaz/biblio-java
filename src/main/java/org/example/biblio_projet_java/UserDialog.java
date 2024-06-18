package org.example.biblio_projet_java;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.Optional;
import javafx.geometry.Insets;

public class UserDialog {

    public static void showLoginDialog(Stage primaryStage, DatabaseManager databaseManager) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Se connecter");

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (!result.isPresent()) {
            dialog.close();
            return;
        }

        result.ifPresent(credentials -> {
            String usernameText = credentials.getKey();
            String passwordText = credentials.getValue();
            try {
                if (databaseManager.loginUser(usernameText, passwordText)) {
                    showAlert(Alert.AlertType.INFORMATION, "Connexion réussie", "Vous êtes maintenant connecté.");
                    databaseManager.setUserLoggedIn(true);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Échec de la connexion",
                            "Nom d'utilisateur ou mot de passe incorrect.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur de connexion",
                        "Une erreur s'est produite lors de la connexion.");
            }
        });
    }

    public static void showSignUpDialog(Stage primaryStage, DatabaseManager databaseManager) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("S'inscrire");

        ButtonType signUpButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(signUpButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPassword, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == signUpButtonType) {
                if (!password.getText().equals(confirmPassword.getText())) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas.");
                    return null;
                }
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (!result.isPresent()) {
            dialog.close();
            return;
        }

        result.ifPresent(credentials -> {
            String usernameText = credentials.getKey();
            String passwordText = credentials.getValue();
            try {
                if (databaseManager.loginUser(usernameText, passwordText)) {
                    showAlert(Alert.AlertType.INFORMATION, "Inscription réussie", "Vous êtes maintenant inscrit.");
                    databaseManager.setUserLoggedIn(true);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Échec de l'inscription", "Nom d'utilisateur déjà pris.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur d'inscription",
                        "Une erreur s'est produite lors de l'inscription.");
            }
        });
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}