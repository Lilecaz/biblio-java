package org.example.biblio_projet_java;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.geometry.Insets;

import java.sql.SQLException;
import java.util.Optional;

public class UserDialog {

    public static void showLoginDialog(Stage primaryStage, DatabaseManager databaseManager) {
        Dialog<Pair<String, String>> dialog = createDialog("Se connecter", "Login");
        GridPane grid = createGridPane();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        addFieldsToGrid(grid, username, password, null);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE
                ? new Pair<>(username.getText(), password.getText())
                : null);

        processDialogResult(dialog.showAndWait(), databaseManager, false);
    }

    public static void showSignUpDialog(Stage primaryStage, DatabaseManager databaseManager) {
        Dialog<Pair<String, String>> dialog = createDialog("S'inscrire", "Sign Up");
        GridPane grid = createGridPane();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        PasswordField confirmPassword = new PasswordField();
        addFieldsToGrid(grid, username, password, confirmPassword);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (!password.getText().equals(confirmPassword.getText())) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas.");
                    return null;
                }
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        processDialogResult(dialog.showAndWait(), databaseManager, true);
    }

    private static Dialog<Pair<String, String>> createDialog(String title, String buttonText) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType(buttonText, ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL);
        return dialog;
    }

    private static GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }

    private static void addFieldsToGrid(GridPane grid, TextField username, PasswordField password,
            PasswordField confirmPassword) {
        username.setPromptText("Username");
        password.setPromptText("Password");
        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        if (confirmPassword != null) {
            confirmPassword.setPromptText("Confirm Password");
            grid.add(new Label("Confirm Password:"), 0, 2);
            grid.add(confirmPassword, 1, 2);
        }
    }

    private static void processDialogResult(Optional<Pair<String, String>> result, DatabaseManager databaseManager,
            boolean isSignUp) {
        if (!result.isPresent())
            return;

        result.ifPresent(credentials -> {
            String usernameText = credentials.getKey();
            String passwordText = credentials.getValue();
            try {
                if ((isSignUp ? databaseManager.registerUser(usernameText, passwordText)
                        : databaseManager.loginUser(usernameText, passwordText))) {
                    showAlert(Alert.AlertType.INFORMATION, (isSignUp ? "Inscription réussie" : "Connexion réussie"),
                            "Vous êtes maintenant " + (isSignUp ? "inscrit" : "connecté") + ".");
                    databaseManager.setUserLoggedIn(true);
                } else {
                    showAlert(Alert.AlertType.ERROR, (isSignUp ? "Échec de l'inscription" : "Échec de la connexion"),
                            "Nom d'utilisateur ou mot de passe incorrect.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite.");
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
