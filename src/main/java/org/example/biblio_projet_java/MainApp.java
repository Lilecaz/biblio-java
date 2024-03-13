package org.example.biblio_projet_java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Création des éléments de menu
        MenuItem menuItem1 = new MenuItem("Ouvrir");
        MenuItem menuItem2 = new MenuItem("Quitter");

        MenuItem menuItem3 = new MenuItem("Sauvegarder");
        MenuItem menuItem4 = new MenuItem("Sauvegarder sous...");

        MenuItem menuItem5 = new MenuItem("Infos");

        // Création du menu
        Menu menu = new Menu("Fichier");
        menu.getItems().addAll(menuItem1, menuItem2);

        Menu menu2 = new Menu("Edition");
        menu2.getItems().addAll(menuItem3, menuItem4);

        Menu menu3 = new Menu("About");
        menu3.getItems().addAll(menuItem5);

        // Création de la barre de menu et ajout du menu
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menu, menu2, menu3);

        // Création de la mise en page principale
        VBox root = new VBox();
        root.getChildren().add(menuBar);

        // Création de la scène
        Scene scene = new Scene(root, 300, 250);

        // Configuration de la scène et affichage de la fenêtre
        primaryStage.setTitle("Biblio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
