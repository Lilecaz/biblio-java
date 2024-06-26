package org.example.biblio_projet_java.view;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.geometry.Insets;

import java.sql.SQLException;
import java.util.Optional;

import org.example.biblio_projet_java.controller.DatabaseManager;
import org.example.biblio_projet_java.controller.UserController;
import org.example.biblio_projet_java.utils.AlertUtils;

/**
 * Cette classe représente une boîte de dialogue pour l'inscription et la
 * connexion d'un utilisateur.
 */
public class UserDialog {

    public UserDialog(Stage primaryStage, DatabaseManager databaseManager) {
    }

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
        ButtonType resetPasswordButtonType = new ButtonType("Reset Password", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().add(resetPasswordButtonType);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resetPasswordButtonType) {
                showResetPasswordDialog(primaryStage, databaseManager);
                return null;
            }
            return dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE
                    ? new Pair<>(username.getText(), password.getText())
                    : null;
        });

        processDialogResult(dialog.showAndWait(), databaseManager, false);
    }

    public static void showResetPasswordDialog(Stage primaryStage, DatabaseManager databaseManager) {
        Dialog<Pair<String, String>> dialog = createDialog("Reset Password", "Reset");
        GridPane grid = createGridPane();
        TextField username = new TextField();
        TextField email = new TextField();
        addResetPasswordFieldsToGrid(grid, username, email);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE
                ? new Pair<>(username.getText(), email.getText())
                : null);

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(credentials -> {
            // Implémentez ici la logique pour la réinitialisation du mot de passe
            String user = credentials.getKey();
            String paswword = credentials.getValue();
            UserController controller = new UserController(databaseManager);
            try {
                boolean success = controller.resetPassword(user, paswword);
                if (success) {
                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Reset Password",
                            "Le mot de passe a été réinitialisé avec succès.");
                } else {
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Reset Password",
                            "Échec de la réinitialisation du mot de passe.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Erreur lors de la réinitialisation du mot de passe.");
            }
        });
    }

    private static void addResetPasswordFieldsToGrid(GridPane grid, TextField username, TextField email) {
        username.setPromptText("Username");
        email.setPromptText("Email");
        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(email, 1, 1);
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

        UserController.configureDialog(dialog, grid, username, password, confirmPassword);

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
        UserController controller = new UserController(databaseManager);
        controller.Loger(result, databaseManager, isSignUp);
    }
}
