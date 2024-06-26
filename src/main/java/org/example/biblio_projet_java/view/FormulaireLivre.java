package org.example.biblio_projet_java.view;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.example.biblio_projet_java.controller.DatabaseManager;
import org.example.biblio_projet_java.controller.FormLivreController;

/**
 * Cette classe représente un formulaire pour ajouter un livre.
 */

public class FormulaireLivre extends VBox {

    public static final TextField titreField = new TextField();
    public static final TextField auteurField = new TextField();
    public static final TextField presentationField = new TextField();
    public static final TextField parutionField = new TextField();
    public static final TextField colonneField = new TextField();
    public static final TextField rangeeField = new TextField();
    public static final CheckBox empruntCheckBox = new CheckBox();
    public static final TextArea resumeArea = new TextArea();
    public static final TextField lienField = new TextField();
    public static final Button ajouterButton = new Button("Ajouter");

    public ImageView previewImageView;

    private DatabaseManager dbManager;

    /**
     * Cette classe représente un formulaire pour ajouter un livre.
     * 
     * @param tableView La table view dans laquelle le livre sera ajouté.
     * @param dbManager Le gestionnaire de base de données utilisé pour ajouter le
     *                  livre.
     */
    public FormulaireLivre(LivreTableView tableView, DatabaseManager dbManager) {
        this.dbManager = dbManager;

        previewImageView = new ImageView();
        previewImageView.setFitWidth(200); // Ajustez la largeur de l'aperçu selon vos besoins
        previewImageView.setPreserveRatio(true);
        Label titreLabel = new Label("Titre: ");

        Label auteurLabel = new Label("Auteur: ");

        Label presentationLabel = new Label("Présentation: ");

        Label parutionLabel = new Label("Parution: ");
        FormLivreController.validateParutionField(parutionField);

        Label colonneLabel = new Label("Colonne: ");
        FormLivreController.filterColonneInput(colonneField);

        Label rangeeLabel = new Label("Rangée: ");
        FormLivreController.filterRangeeInput(rangeeField);

        Label empruntLabel = new Label("Emprunt: ");

        Label resumeLabel = new Label("Résumé: ");
        resumeArea.setWrapText(true);
        resumeArea.setPrefRowCount(3);

        Label lienLabel = new Label("Lien: ");
        FormLivreController.handleLinkChange(lienField, previewImageView);

        ajouterButton.setOnAction(event -> {
            FormLivreController.handleNewBookSubmission(tableView, dbManager, titreField, auteurField,
                    presentationField, parutionField, colonneField, rangeeField, empruntCheckBox, resumeArea,
                    lienField);
        });

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        gridPane.addRow(0, titreLabel, titreField);
        gridPane.addRow(1, auteurLabel, auteurField);
        gridPane.addRow(2, presentationLabel, presentationField);
        gridPane.addRow(3, parutionLabel, parutionField);
        gridPane.addRow(4, colonneLabel, colonneField);
        gridPane.addRow(5, rangeeLabel, rangeeField);
        gridPane.addRow(6, empruntLabel, empruntCheckBox);
        gridPane.addRow(7, resumeLabel, resumeArea);
        gridPane.addRow(8, lienLabel, lienField);
        gridPane.addRow(9, ajouterButton);
        gridPane.addRow(10, previewImageView);

        this.getChildren().add(gridPane);
        chargerLivresDansTableView(tableView);

        this.getStyleClass().add("formulaire");
    }

    /**
     * Affiche une alerte de type avertissement avec le message spécifié.
     *
     * @param message le message à afficher dans l'alerte
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Charge les livres depuis la base de données et les ajoute à la TableView
     * spécifiée.
     *
     * @param tableView la TableView dans laquelle les livres doivent être ajoutés
     */
    public void chargerLivresDansTableView(LivreTableView tableView) {
        try {
            List<Livre> livres = dbManager.getLivres();
            tableView.getItems().addAll(livres);
        } catch (SQLException e) {
            showAlert("Erreur lors du chargement des livres depuis la base de données : " + e.getMessage());
        }
    }
}