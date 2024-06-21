package org.example.biblio_projet_java;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.Optional;

public class LoginDialog {
    private DatabaseManager databaseManager;

    /**
     * Cette classe représente une boîte de dialogue de connexion.
     * Elle permet à l'utilisateur de se connecter à la base de données en utilisant
     * un objet DatabaseManager.
     */
    public LoginDialog(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Affiche la boîte de dialogue de connexion.
     * 
     * @param primaryStage la fenêtre principale de l'application
     */
    public void show(Stage primaryStage) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Se connecter");
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
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
        result.ifPresent(credentials -> handleLogin(credentials, primaryStage));
    }

    /**
     * Gère la tentative de connexion de l'utilisateur avec les informations
     * d'identification fournies.
     * 
     * @param credentials  les informations d'identification de l'utilisateur (nom
     *                     d'utilisateur et mot de passe)
     * @param primaryStage la fenêtre principale de l'application
     */
    private void handleLogin(Pair<String, String> credentials, Stage primaryStage) {
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