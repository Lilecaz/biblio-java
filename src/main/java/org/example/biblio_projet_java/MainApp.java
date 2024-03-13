package org.example.biblio_projet_java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        LivreTableView tableView = new LivreTableView();

        FormulaireLivre formulaireLivre = new FormulaireLivre(tableView);

        MenuItem menuItem1 = new MenuItem("Ouvrir");
        MenuItem menuItem2 = new MenuItem("Quitter");

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
                tableView.getTableView());

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Biblio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
