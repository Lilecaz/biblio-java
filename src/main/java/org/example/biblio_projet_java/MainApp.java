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
import java.util.Optional;

public class MainApp extends Application {

    private File currentFile;

    @Override
    public void start(Stage primaryStage) {
        LivreTableView tableView = new LivreTableView();
        FormulaireLivre formulaireLivre = new FormulaireLivre(tableView);

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
                    WordExporter.exportToWord(tableView.getItems(), name, primaryStage);
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

        MenuItem menuItem5 = new MenuItem("Infos");

        Menu menu = new Menu("Fichier");
        menu.getItems().addAll(menuItem1, menuItem2, menuItem6);

        Menu menu2 = new Menu("Edition");
        menu2.getItems().addAll(menuItem3, menuItem4);

        Menu menu3 = new Menu("About");
        menu3.getItems().addAll(menuItem5);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu, menu2, menu3);

        BorderPane root = new BorderPane(); // Utiliser un BorderPane comme conteneur principal

        root.setTop(menuBar); // Placer le menuBar en haut

        HBox formulaireLivreBox = new HBox(formulaireLivre);
        VBox tableViewBox = new VBox(tableView);

        root.setCenter(tableViewBox);
        root.setRight(formulaireLivreBox); // Placer le formulaire à droite

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Biblio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}