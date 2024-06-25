package org.example.biblio_projet_java.controller;

import java.sql.SQLException;
import java.util.Optional;

import org.example.biblio_projet_java.utils.AlertUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class UserController {
    private static DatabaseManager databaseManager;

    public UserController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean Loger(Optional<Pair<String, String>> result, DatabaseManager databaseManager,
            boolean isSignUp) {
        result.ifPresent(credentials -> {
            String usernameText = credentials.getKey();
            String passwordText = credentials.getValue();
            try {
                if ((isSignUp ? databaseManager.registerUser(usernameText, passwordText)
                        : databaseManager.loginUser(usernameText, passwordText))) {
                    AlertUtils.showAlert(Alert.AlertType.INFORMATION,
                            (isSignUp ? "Inscription réussie" : "Connexion réussie"),
                            "Vous êtes maintenant " + (isSignUp ? "inscrit" : "connecté") + ".");
                    databaseManager.setUserLoggedIn(true);
                } else {
                    AlertUtils.showAlert(Alert.AlertType.ERROR,
                            (isSignUp ? "Échec de l'inscription" : "Échec de la connexion"),
                            "Nom d'utilisateur ou mot de passe incorrect.");
                }
            } catch (SQLException e) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite.");
            }
        });
        return true;
    }

    public static void configureDialog(Dialog<Pair<String, String>> dialog, GridPane grid, TextField username,
            PasswordField password, PasswordField confirmPassword) {
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (!password.getText().equals(confirmPassword.getText())) {
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas.");
                    return null;
                }
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });
    }

    public static void configureDialog(Dialog<Pair<String, String>> dialog, ButtonType loginButtonType, GridPane grid,
            TextField username, PasswordField password) {
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });
    }

    public static void loginHandler(Pair<String, String> credentials) {
        try {
            if (databaseManager.loginUser(credentials.getKey(), credentials.getValue())) {
                AlertUtils.showInformation("Connexion réussie", "Vous êtes maintenant connecté.");
                databaseManager.setUserLoggedIn(true);
            } else {
                AlertUtils.showError("Échec de la connexion", "Username ou mot de passe incorrect.");
            }
        } catch (SQLException e) {
            AlertUtils.showError("Erreur de connexion", "Une erreur s'est produite lors de la connexion.");
        }
    }
}
