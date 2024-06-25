package org.example.biblio_projet_java;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.geometry.Insets;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Cette classe représente une boîte de dialogue pour l'inscription et la
 * connexion d'un utilisateur.
 */
public class UserDialog {

    /**
     * Affiche une boîte de dialogue de connexion.
     * 
     * @param primaryStage    la fenêtre principale de l'application
     * @param databaseManager le gestionnaire de base de données
     */
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

    /**
     * Affiche une boîte de dialogue pour l'inscription d'un utilisateur.
     *
     * @param primaryStage    la fenêtre principale de l'application
     * @param databaseManager le gestionnaire de base de données
     */
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

    /**
     * Crée une boîte de dialogue avec un titre et un bouton personnalisés.
     *
     * @param title      Le titre de la boîte de dialogue.
     * @param buttonText Le texte du bouton personnalisé.
     * @return La boîte de dialogue créée.
     */
    private static Dialog<Pair<String, String>> createDialog(String title, String buttonText) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType(buttonText, ButtonBar.ButtonData.OK_DONE),
                ButtonType.CANCEL);
        return dialog;
    }

    /**
     * Crée et retourne un objet GridPane avec les paramètres par défaut.
     *
     * @return L'objet GridPane créé.
     */
    private static GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }

    /**
     * Ajoute les champs à la grille spécifiée.
     *
     * @param grid            la grille dans laquelle les champs doivent être
     *                        ajoutés
     * @param username        le champ de texte pour le nom d'utilisateur
     * @param password        le champ de texte pour le mot de passe
     * @param confirmPassword le champ de texte pour la confirmation du mot de passe
     *                        (peut être null)
     */
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

    /**
     * Traite le résultat d'une boîte de dialogue.
     *
     * @param result          Le résultat de la boîte de dialogue, contenant une
     *                        paire de nom d'utilisateur et mot de passe.
     * @param databaseManager Le gestionnaire de base de données.
     * @param isSignUp        Indique si l'action est une inscription ou une
     *                        connexion.
     */
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

    /**
     * Affiche une boîte de dialogue avec le type d'alerte spécifié, le titre et le
     * message donnés.
     *
     * @param alertType le type d'alerte à afficher
     * @param title     le titre de la boîte de dialogue
     * @param message   le message à afficher dans la boîte de dialogue
     */
    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
