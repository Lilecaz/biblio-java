package org.example.biblio_projet_java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.biblio_projet_java.Bibliotheque;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class MainApp extends Application {

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
                chargerFichierXML(selectedFile, tableView);
            }
        });

        MenuItem menuItem2 = new MenuItem("Quitter");
        menuItem2.setOnAction(event -> primaryStage.close());

        MenuItem menuItem3 = new MenuItem("Sauvegarder");
        MenuItem menuItem4 = new MenuItem("Sauvegarder sous...");

        MenuItem menuItem5 = new MenuItem("Infos");

        Menu menu = new Menu("Fichier");
        menu.getItems().addAll(menuItem1, menuItem2);

        Menu menu2 = new Menu("Edition");
        menu2.getItems().addAll(menuItem3, menuItem4);

        Menu menu3 = new Menu("About");
        menu3.getItems().addAll(menuItem5);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu, menu2, menu3);

        VBox root = new VBox(10);
        root.getChildren().addAll(
                menuBar,
                formulaireLivre,
                tableView);

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Biblio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void chargerFichierXML(File file, LivreTableView tableView) {
        try {
            JAXBContext context = JAXBContext.newInstance(Bibliotheque.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Bibliotheque bibliotheque = (Bibliotheque) unmarshaller.unmarshal(file);
            bibliotheque.getLivre().forEach(tableView::ajouterLivre);
        } catch (JAXBException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement du fichier XML.");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
