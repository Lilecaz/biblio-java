package org.example.biblio_projet_java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class MainWindow extends Application {

    private File currentFile;
    private DatabaseManager databaseManager;
    private LivreTableView tableView;

    @Override
    public void start(Stage primaryStage) throws SQLException {
        databaseManager = new DatabaseManager();
        databaseManager.getUsername();
        // Initial scene with buttons
        VBox startBox = new VBox(10);
        startBox.setPrefSize(300, 200);
        startBox.setSpacing(10);

        Button btnConnect = new Button("Se connecter");
        Button btnSignUp = new Button("S'inscrire");
        Button btnOpenFile = new Button("Ouvrir un fichier");

        startBox.getChildren().addAll(btnConnect, btnSignUp, btnOpenFile);
        Scene startScene = new Scene(startBox);

        btnConnect.setOnAction(event -> {
            showLoginDialog(primaryStage);
            showMainWindow(primaryStage, null);
        });

        btnSignUp.setOnAction(event -> {
            showSignUpDialog(primaryStage);
            showMainWindow(primaryStage, null);
        });

        btnOpenFile.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Ouvrir un fichier XML");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Fichiers XML", "*.xml"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                showMainWindow(primaryStage, selectedFile);
            }
        });

        primaryStage.setTitle("Biblio - Choisissez une option");
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    private void showMainWindow(Stage primaryStage, File fileToLoad) {
        tableView = new LivreTableView();
        FormulaireLivre formulaireLivre = new FormulaireLivre(tableView, databaseManager);

        if (fileToLoad != null) {
            currentFile = XMLFileManager.chargerFichierXML(fileToLoad, tableView);
        }

        MenuItem menuItem1 = new MenuItem("Ouvrir");
        menuItem1.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Ouvrir un fichier XML");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Fichiers XML", "*.xml"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                currentFile = XMLFileManager.chargerFichierXML(selectedFile, tableView);
            }
        });
        MenuItem menuItem2 = new MenuItem("Quitter");
        menuItem2.setOnAction(event -> {
            currentFile = null;
            tableView.getItems().clear();
        });
        MenuItem menuItem6 = new MenuItem("Exporter");
        menuItem6.setOnAction(event -> {
            if (!tableView.getItems().isEmpty()) { // Vérifie si la liste de livres n'est pas vide
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Exporter le document Word");
                dialog.setHeaderText("Veuillez saisir le nom du document :");
                dialog.setContentText("Nom du document:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(name -> {
                    WordExporter wordExporter = new WordExporter();
                    wordExporter.export(tableView.getItems(), name, primaryStage);
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Exporter le document Word");
                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("Fichiers Word", "*.docx"));
                    File selectedFile = fileChooser.showSaveDialog(primaryStage);
                    if (selectedFile != null) {
                        wordExporter.save(selectedFile);
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Aucun livre à exporter.");
                alert.showAndWait();
            }
        });

        MenuItem menuItem3 = new MenuItem("Sauvegarder");
        menuItem3.setOnAction(event -> {
            if (currentFile != null) {
                XMLFileManager.sauvegarderFichierXML(currentFile, tableView.getItems());
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Aucun fichier n'est actuellement ouvert.");
                alert.showAndWait();
            }
        });
        MenuItem menuItem4 = new MenuItem("Sauvegarder sous...");
        menuItem4.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sauvegarder un fichier XML");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Fichiers XML", "*.xml"));
            File selectedFile = fileChooser.showSaveDialog(primaryStage);
            if (selectedFile != null) {
                currentFile = XMLFileManager.sauvegarderFichierXML(selectedFile, tableView.getItems());
            }
        });

        MenuItem decoMenuItem = new MenuItem("Se déconnecter");
        decoMenuItem.setOnAction(event -> {
            // show the start scene
            primaryStage.setScene(new Scene(new VBox(10)));
            databaseManager.setUserLoggedIn(false);
        });

        MenuItem menuItem5 = new MenuItem("Infos");

        Menu menu = new Menu("Fichier");
        menu.getItems().addAll(menuItem1, menuItem2, menuItem6);

        Menu menu2 = new Menu("Edition");
        menu2.getItems().addAll(menuItem3, menuItem4);

        Menu menu3 = new Menu("About");
        menu3.getItems().addAll(menuItem5);

        Menu menuUser = new Menu("Utilisateur");
        menuUser.getItems().addAll(decoMenuItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu, menu2, menu3, menuUser);

        BorderPane root = new BorderPane(); // Utiliser un BorderPane comme conteneur principal

        root.setTop(menuBar); // Placer le menuBar en haut

        HBox formulaireLivreBox = new HBox(formulaireLivre);
        formulaireLivreBox.setPrefSize(300, 300);
        VBox tableViewBox = new VBox(tableView);

        root.setCenter(tableViewBox);
        if (databaseManager.getUserType().equals("admin")) {
            root.setRight(formulaireLivreBox); // Placer le formulaire à droite
        } else {
            root.setRight(null);
        }

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Biblio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showLoginDialog(Stage primaryStage) {
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
            // User closed the dialog or clicked Cancel, exit the application
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
                    // Load main window or proceed to next step
                } else {
                    showAlert(Alert.AlertType.ERROR, "Échec de la connexion", "Username ou mot de passe incorrect.");
                    dialog.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur de connexion",
                        "Une erreur s'est produite lors de la connexion.");
                dialog.close();
            }
        });
    }

    public void showSignUpDialog(Stage primaryStage) {
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
        password.setPromptText("Mot de passe");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(password, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == signUpButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (!result.isPresent()) {
            // User closed the dialog or clicked Cancel, exit the application
            dialog.close();
            return;
        }

        result.ifPresent(credentials -> {
            String usernameText = credentials.getKey();
            String passwordText = credentials.getValue();
            try {
                if (databaseManager.registerUser(usernameText, passwordText)) {
                    showAlert(Alert.AlertType.INFORMATION, "Inscription réussie", "Vous êtes maintenant inscrit.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Échec de l'inscription", "L'inscription a échoué.");
                    dialog.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur d'inscription",
                        "Une erreur s'est produite lors de l'inscription.");
                dialog.close();
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
