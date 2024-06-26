
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
import java.sql.SQLException;

import org.example.biblio_projet_java.controller.DatabaseManager;
import org.example.biblio_projet_java.controller.MainWindowController;
import org.example.biblio_projet_java.utils.AlertUtils;
import org.example.biblio_projet_java.utils.XMLFileManager;

/**
 * La classe MainWindow représente la fenêtre principale de l'application.
 * Elle étend la classe Application de la bibliothèque JavaFX.
 * Cette classe est responsable de la création et de la gestion de l'interface
 * utilisateur de l'application.
 * Elle fournit des méthodes pour gérer les actions de l'utilisateur, telles que
 * la connexion à la base de données,
 * l'ouverture et l'enregistrement de fichiers, l'exportation de données et la
 * gestion des sessions utilisateur.
 */
public class MainWindow extends Application {

    private File currentFile;
    private DatabaseManager databaseManager;
    private LivreTableView tableView;
    MainWindowController mainWindowController = new MainWindowController();

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

        Scene startScene = new Scene(startBox, 800, 600);
        startScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setTitle("Bibliotheque - Choisissez une option");
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
        startBox.setPrefSize(500, 300);
        startBox.setSpacing(10);
        startBox.getStyleClass().add("vbox");

        Button btnConnect = new Button("Se connecter");
        Button btnSignUp = new Button("S'inscrire");
        Button btnOpenFile = new Button("Ouvrir un fichier");

        btnConnect.getStyleClass().add("button");
        btnSignUp.getStyleClass().add("button");
        btnOpenFile.getStyleClass().add("button");

        btnConnect.setOnAction(event -> {
            showLoginDialog(primaryStage);
            boolean rep = mainWindowController.handleUserAdminConnection(primaryStage, databaseManager);
            if (rep) {
                showMainWindow(primaryStage, null, databaseManager.isAdmin());
            } else {
                showMainWindow(primaryStage, currentFile, rep);
            }
        });

        btnSignUp.setOnAction(event -> {
            showSignUpDialog(primaryStage);
            if (databaseManager.isUserConnected()) {
                showMainWindow(primaryStage, null, databaseManager.isAdmin());
            }
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
            showMainWindow(primaryStage, selectedFile, true);
        }
    }

    /**
     * Affiche la fenêtre principale de l'application.
     * 
     * @param primaryStage La fenêtre principale de l'application.
     * @param fileToLoad   Le fichier à charger (peut être null).
     * @param isAdmin      Indique si l'utilisateur est un administrateur.
     */
    private void showMainWindow(Stage primaryStage, File fileToLoad, boolean isAdmin) {
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
        if (databaseManager.isUserConnected() && isAdmin || !databaseManager.isUserConnected()) {
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
        menuItem1.getStyleClass().add("menu-item");

        MenuItem menuItem2 = new MenuItem("Quitter");
        menuItem2.setOnAction(event -> {
            currentFile = null;
            tableView.getItems().clear();
        });
        menuItem2.getStyleClass().add("menu-item");

        MenuItem menuItem6 = new MenuItem("Exporter");
        menuItem6.setOnAction(event -> exportDocument(primaryStage));
        menuItem6.getStyleClass().add("menu-item");

        MenuItem menuItem3 = new MenuItem("Sauvegarder");
        menuItem3.setOnAction(event -> saveFile());
        menuItem3.getStyleClass().add("menu-item");

        MenuItem menuItem4 = new MenuItem("Sauvegarder sous...");
        menuItem4.setOnAction(event -> saveFileAs(primaryStage));
        menuItem4.getStyleClass().add("menu-item");

        MenuItem decoMenuItem = new MenuItem("Se déconnecter");
        decoMenuItem.setOnAction(event -> {
            try {
                logout(primaryStage);
            } catch (SQLException e) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion.");
            }
        });
        decoMenuItem.getStyleClass().add("menu-item");

        MenuItem connectMenuItem = new MenuItem("Se connecter");
        connectMenuItem.setOnAction(event -> showLoginDialog(primaryStage));
        connectMenuItem.getStyleClass().add("menu-item");

        MenuItem menuItem5 = new MenuItem("Infos");
        menuItem5.getStyleClass().add("menu-item");

        MenuItem syncMenuItem = new MenuItem("Synchroniser");
        syncMenuItem.setOnAction(event -> syncData(primaryStage, databaseManager, tableView));
        syncMenuItem.getStyleClass().add("menu-item");
        Menu menu = new Menu("Fichier");
        if (!databaseManager.isAdmin()) {
            // Ajoute uniquement "Exporter" pour les utilisateurs non admins
            menu.getItems().add(menuItem6);
        } else {
            // Ajoute tous les éléments pour les admins
            menu.getItems().addAll(menuItem1, menuItem2, syncMenuItem, menuItem6);
        }
        menu.getStyleClass().add("menu");

        Menu menu2 = new Menu("Edition");
        menu2.getItems().addAll(menuItem3, menuItem4);
        menu2.getStyleClass().add("menu");

        Menu menu3 = new Menu("About");
        menu3.getItems().addAll(menuItem5);
        menu3.getStyleClass().add("menu");

        Menu menuUser = new Menu(databaseManager.isUserConnected() ? databaseManager.getUsername() : "Se connecter");
        if (databaseManager.isUserConnected()) {
            menuUser.getItems().add(decoMenuItem);
        } else {
            menuUser.getItems().add(connectMenuItem);
        }
        menuUser.getStyleClass().add("menu");

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu, menu2, menu3, menuUser);
        menuBar.getStyleClass().add("menu-bar");
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
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Information",
                    "Aucun fichier n'est actuellement ouvert.");
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
        mainWindowController.exportDocumentToWord(primaryStage, tableView);
    }

    /**
     * Déconnecte l'utilisateur actuellement connecté, ferme la fenêtre principale
     * et redémarre l'application.
     * Si l'utilisateur est toujours connecté après la déconnexion, affiche la
     * fenêtre principale avec les autorisations appropriées.
     *
     * @param primaryStage La fenêtre principale de l'application.
     * @throws SQLException Si une erreur se produit lors de la déconnexion de la
     *                      base de données.
     */
    private void logout(Stage primaryStage) throws SQLException {
        databaseManager.logout();
        primaryStage.close();
        start(primaryStage);
        if (databaseManager.isUserConnected()) {
            showMainWindow(primaryStage, null, databaseManager.isAdmin());
        }
    }

    /**
     * Affiche la boîte de dialogue de connexion.
     * Ferme la fenêtre principale.
     * Si l'utilisateur est connecté, affiche la fenêtre principale avec les
     * autorisations appropriées.
     * Sinon, affiche une alerte d'information indiquant que la connexion a échoué.
     *
     * @param primaryStage la fenêtre principale de l'application
     */
    public void showLoginDialog(Stage primaryStage) {
        primaryStage.close();
        UserDialog.showLoginDialog(primaryStage, databaseManager);
        if (databaseManager.isUserConnected()) {
            showMainWindow(primaryStage, null, (databaseManager.isAdmin()));
        } else {
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Information", "Connexion échouée.");
        }
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
     * Syncronise les données de la table avec la base de données.
     * 
     * @param primaryStage
     * @param databaseManager
     * @param tableView
     */
    private void syncData(Stage primaryStage, DatabaseManager databaseManager, LivreTableView tableView) {
        boolean rep = mainWindowController.syncData(primaryStage, databaseManager, tableView);
        if (rep) {
            showMainWindow(primaryStage, null, databaseManager.isAdmin());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}