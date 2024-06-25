package org.example.biblio_projet_java.view;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.Optional;

import org.example.biblio_projet_java.controller.DatabaseManager;
import org.example.biblio_projet_java.controller.UserController;
import org.example.biblio_projet_java.utils.AlertUtils;

/**
 * Cette classe représente une boîte de dialogue de connexion.
 * Elle permet à l'utilisateur de se connecter à la base de données en utilisant
 * un objet DatabaseManager.
 */
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
     * Affiche une boîte de dialogue de connexion.
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

        UserController.configureDialog(dialog, loginButtonType, grid, username, password);

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(credentials -> handleLogin(credentials, primaryStage));
    }

    /**
     * @param credentials
     * @param primaryStage
     */
    private void handleLogin(Pair<String, String> credentials, Stage primaryStage) {
        UserController.loginHandler(credentials);
    }

}