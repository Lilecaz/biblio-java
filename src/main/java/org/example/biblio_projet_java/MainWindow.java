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

        VBox startBox = createStartBox(primaryStage);

        Scene startScene = new Scene(startBox);
        primaryStage.setTitle("Biblio - Choisissez une option");
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    private VBox createStartBox(Stage primaryStage) {
        VBox startBox = new VBox(10);
        startBox.setPrefSize(300, 200);
        startBox.setSpacing(10);

        Button btnConnect = new Button("Se connecter");
        Button btnSignUp = new Button("S'inscrire");
        Button btnOpenFile = new Button("Ouvrir un fichier");

        btnConnect.setOnAction(event -> {
            showLoginDialog(primaryStage);
            showMainWindow(primaryStage, null);
        });

        btnSignUp.setOnAction(event -> {
            showSignUpDialog(primaryStage);
            showMainWindow(primaryStage, null);
        });

        btnOpenFile.setOnAction(event -> openFile(primaryStage));

        startBox.getChildren().addAll(btnConnect, btnSignUp, btnOpenFile);
        return startBox;
    }

    private void openFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un fichier XML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers XML", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            showMainWindow(primaryStage, selectedFile);
        }
    }

    private void showMainWindow(Stage primaryStage, File fileToLoad) {
        tableView = new LivreTableView();
        FormulaireLivre formulaireLivre = new FormulaireLivre(tableView, databaseManager);

        if (fileToLoad != null) {
            currentFile = XMLFileManager.chargerFichierXML(fileToLoad, tableView);
        }

        MenuBar menuBar = createMenuBar(primaryStage);
        BorderPane root = new BorderPane();
        root.setTop(menuBar);

        HBox formulaireLivreBox = new HBox(formulaireLivre);
        formulaireLivreBox.setPrefSize(300, 300);
        VBox tableViewBox = new VBox(tableView);

        root.setCenter(tableViewBox);
        if (databaseManager.getUserType().equals("admin")) {
            root.setRight(formulaireLivreBox);
        }

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Biblio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar(Stage primaryStage) {
        MenuItem menuItem1 = new MenuItem("Ouvrir");
        menuItem1.setOnAction(event -> openFile(primaryStage));

        MenuItem menuItem2 = new MenuItem("Quitter");
        menuItem2.setOnAction(event -> {
            currentFile = null;
            tableView.getItems().clear();
        });

        MenuItem menuItem6 = new MenuItem("Exporter");
        menuItem6.setOnAction(event -> exportDocument(primaryStage));

        MenuItem menuItem3 = new MenuItem("Sauvegarder");
        menuItem3.setOnAction(event -> saveFile());

        MenuItem menuItem4 = new MenuItem("Sauvegarder sous...");
        menuItem4.setOnAction(event -> saveFileAs(primaryStage));

        MenuItem decoMenuItem = new MenuItem("Se déconnecter");
        decoMenuItem.setOnAction(event -> logout(primaryStage));

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

        return menuBar;
    }

    private void saveFile() {
        if (currentFile != null) {
            XMLFileManager.sauvegarderFichierXML(currentFile, tableView.getItems());
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Aucun fichier n'est actuellement ouvert.");
        }
    }

    private void saveFileAs(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder un fichier XML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers XML", "*.xml"));
        File selectedFile = fileChooser.showSaveDialog(primaryStage);
        if (selectedFile != null) {
            currentFile = XMLFileManager.sauvegarderFichierXML(selectedFile, tableView.getItems());
        }
    }

    private void exportDocument(Stage primaryStage) {
        if (!tableView.getItems().isEmpty()) {
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
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Word", "*.docx"));
                File selectedFile = fileChooser.showSaveDialog(primaryStage);
                if (selectedFile != null) {
                    wordExporter.save(selectedFile);
                }
            });
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Aucun livre à exporter.");
        }
    }

    private void logout(Stage primaryStage) {
        primaryStage.setScene(new Scene(new VBox(10)));
        databaseManager.setUserLoggedIn(false);
    }

    public void showLoginDialog(Stage primaryStage) {
        UserDialog.showLoginDialog(primaryStage, databaseManager);
    }

    public void showSignUpDialog(Stage primaryStage) {
        UserDialog.showSignUpDialog(primaryStage, databaseManager);
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