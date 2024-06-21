package org.example.biblio_projet_java.view;

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

import org.example.biblio_projet_java.DatabaseManager;
import org.example.biblio_projet_java.UserDialog;
import org.example.biblio_projet_java.WordExporter;
import org.example.biblio_projet_java.XMLFileManager;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class MainWindow extends Application {

    private File currentFile;
    private DatabaseManager databaseManager;
    private LivreTableView tableView;

    /**
     * Démarre l'application en créant une nouvelle fenêtre principale.
     * 
     * @param primaryStage la fenêtre principale de l'application
     * @throws SQLException si une erreur se produit lors de la connexion à la base
     *                      de données
     */
    @Override
    public void start(Stage primaryStage) throws SQLException {
        databaseManager = new DatabaseManager();

        VBox startBox = createStartBox(primaryStage);

        Scene startScene = new Scene(startBox);
        primaryStage.setTitle("Biblio - Choisissez une option");
        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    /**
     * Crée une boîte de démarrage contenant des boutons pour se connecter,
     * s'inscrire et ouvrir un fichier.
     * 
     * @param primaryStage la fenêtre principale de l'application
     * @return la boîte de démarrage créée
     */
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

    /**
     * Ouvre une boîte de dialogue pour sélectionner un fichier XML, puis affiche la
     * fenêtre principale avec le fichier sélectionné.
     *
     * @param primaryStage la fenêtre principale de l'application
     */
    private void openFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un fichier XML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers XML", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            showMainWindow(primaryStage, selectedFile);
        }
    }

    /**
     * Affiche la fenêtre principale de l'application.
     * 
     * @param primaryStage la fenêtre principale de l'application
     * @param fileToLoad   le fichier à charger (peut être null)
     */
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

    /**
     * Crée et retourne une barre de menu pour la fenêtre principale.
     *
     * @param primaryStage la fenêtre principale de l'application
     * @return la barre de menu créée
     */
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

    /**
     * Sauvegarde le fichier XML actuellement ouvert.
     * Si aucun fichier n'est actuellement ouvert, affiche une alerte d'information.
     */
    private void saveFile() {
        if (currentFile != null) {
            XMLFileManager.sauvegarderFichierXML(currentFile, tableView.getItems());
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Aucun fichier n'est actuellement ouvert.");
        }
    }

    /**
     * Sauvegarde le fichier XML en tant que nouveau fichier.
     * 
     * @param primaryStage la fenêtre principale de l'application
     */
    private void saveFileAs(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder un fichier XML");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers XML", "*.xml"));
        File selectedFile = fileChooser.showSaveDialog(primaryStage);
        if (selectedFile != null) {
            currentFile = XMLFileManager.sauvegarderFichierXML(selectedFile, tableView.getItems());
        }
    }

    /**
     * Exporte les données de la table dans un document Word.
     * 
     * @param primaryStage la fenêtre principale de l'application
     */
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

    /**
     * Déconnecte l'utilisateur actuellement connecté.
     * Réinitialise la scène principale avec une nouvelle VBox vide.
     * Met à jour l'état de connexion de l'utilisateur dans le gestionnaire de base
     * de données.
     *
     * @param primaryStage la fenêtre principale de l'application
     */
    private void logout(Stage primaryStage) {
        databaseManager.logout();
        primaryStage.setScene(new Scene(new VBox(10)));
        databaseManager.setUserLoggedIn(false);
    }

    /**
     * Affiche la boîte de dialogue de connexion.
     * 
     * @param primaryStage la fenêtre principale de l'application
     */
    public void showLoginDialog(Stage primaryStage) {
        UserDialog.showLoginDialog(primaryStage, databaseManager);
    }

    /**
     * Affiche la boîte de dialogue d'inscription.
     * 
     * @param primaryStage la fenêtre principale de l'application
     */
    public void showSignUpDialog(Stage primaryStage) {
        UserDialog.showSignUpDialog(primaryStage, databaseManager);
    }

    /**
     * Affiche une boîte de dialogue d'alerte avec le type d'alerte spécifié, le
     * titre et le message donnés.
     *
     * @param alertType le type d'alerte à afficher (Alert.AlertType)
     * @param title     le titre de l'alerte
     * @param message   le message de l'alerte
     */
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