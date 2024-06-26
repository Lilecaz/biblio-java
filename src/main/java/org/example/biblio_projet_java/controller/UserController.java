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
        /**
     * Constructeur du contrôleur de l'utilisateur.
     *
     * @param databaseManager Gestionnaire de la base de données pour la communication avec la base de données.
     */

    public UserController(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }


    /**
     * Méthode pour gérer l'inscription ou la connexion d'un utilisateur.
     *
     * @param result       Paire contenant le nom d'utilisateur et le mot de passe saisis.
     * @param databaseManager Gestionnaire de la base de données pour exécuter les opérations.
     * @param isSignUp     Boolean indiquant s'il s'agit d'une inscription (true) ou d'une connexion (false).
     * @return true si l'opération est réussie, sinon false.
     */

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
    /**
     * Configure le contenu de la boîte de dialogue d'inscription avec les champs requis.
     *
     * @param dialog           Boîte de dialogue pour l'inscription.
     * @param grid             Grille contenant les champs de saisie.
     * @param username         Champ de texte pour le nom d'utilisateur.
     * @param password         Champ de texte pour le mot de passe.
     * @param confirmPassword  Champ de texte pour la confirmation du mot de passe.
     */
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

     /**
     * Configure le contenu de la  boîte de dialogue de connexion avec les champs requis.
     *
     * @param dialog           Boîte de dialogue pour la connexion.
     * @param loginButtonType  Type de bouton pour la connexion.
     * @param grid             Grille contenant les champs de saisie.
     * @param username         Champ de texte pour le nom d'utilisateur.
     * @param password         Champ de texte pour le mot de passe.
     */
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

     /**
     * Méthode de gestionnaire pour la tentative de connexion avec les informations fournies.
     *
     * @param credentials  Paire contenant le nom d'utilisateur et le mot de passe.
     */
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
