package org.example.biblio_projet_java;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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

        // Création des contrôles de formulaire
        TextField titreField = new TextField();
        TextField auteurField = new TextField();
        TextField presentationField = new TextField();
        TextField parutionField = new TextField();
        TextField colonneField = new TextField();
        TextField rangeeField = new TextField();

        // Création du tableau pour afficher les données saisies
        TableView<String[]> tableView = new TableView<>();
        tableView.setEditable(true);

        // Création des colonnes du tableau
        TableColumn<String[], String> titreCol = new TableColumn<>("Titre");
        TableColumn<String[], String> auteurCol = new TableColumn<>("Auteur");
        TableColumn<String[], String> presentationCol = new TableColumn<>("Presentation");
        TableColumn<String[], String> parutionCol = new TableColumn<>("Parution");
        TableColumn<String[], String> colonneCol = new TableColumn<>("Colonne");
        TableColumn<String[], String> rangeeCol = new TableColumn<>("Rangee");

        // Liaison des colonnes aux données
        titreCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()[0]));
        auteurCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()[1]));
        presentationCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()[2]));
        parutionCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()[3]));
        colonneCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()[4]));
        rangeeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()[5]));

        // Ajout des colonnes à la table
        tableView.getColumns().addAll(titreCol, auteurCol, presentationCol, parutionCol, colonneCol, rangeeCol);

        // Création du bouton d'ajout
        Button addButton = new Button("Ajouter");
        addButton.setOnAction(event -> {
            String[] data = new String[]{
                    titreField.getText(),
                    auteurField.getText(),
                    presentationField.getText(),
                    parutionField.getText(),
                    colonneField.getText(),
                    rangeeField.getText()
            };
            tableView.getItems().add(data);
            // Effacer les champs après l'ajout
            titreField.clear();
            auteurField.clear();
            presentationField.clear();
            parutionField.clear();
            colonneField.clear();
            rangeeField.clear();
        });

        // Création du GridPane pour le formulaire
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(5);
        gridPane.addRow(0, new Label("Titre:"), titreField);
        gridPane.addRow(1, new Label("Auteur:"), auteurField);
        gridPane.addRow(2, new Label("Presentation:"), presentationField);
        gridPane.addRow(3, new Label("Parution:"), parutionField);
        gridPane.addRow(4, new Label("Colonne:"), colonneField);
        gridPane.addRow(5, new Label("Rangee:"), rangeeField);

        // Création de la disposition
        VBox root = new VBox(10);
        root.getChildren().addAll(
                menuBar,
                gridPane,
                addButton,
                tableView
        );

        // Création de la scène
        Scene scene = new Scene(root, 600, 400);

        // Configuration de la scène et affichage de la fenêtre
        primaryStage.setTitle("Biblio");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
