package org.example.biblio_projet_java.view;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

import java.sql.SQLException;
import java.util.List;

import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.example.biblio_projet_java.controller.DatabaseManager;
import org.example.biblio_projet_java.controller.FormLivreController;
import org.example.biblio_projet_java.utils.AlertUtils;

/**
 * Cette classe représente un formulaire pour ajouter un livre.
 * Elle étend la classe VBox et contient des champs de texte pour le titre,
 * l'auteur,
 * la présentation, la parution, la colonne, la rangée, le résumé, le lien et un
 * bouton
 * pour ajouter le livre.
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
     * Constructeur de la classe FormulaireLivre.
     * 
     * @param tableView
     * @param dbManager
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
        Label colonneLabel = new Label("Colonne: ");
        Label rangeeLabel = new Label("Rangée: ");
        Label empruntLabel = new Label("Emprunt: ");
        Label resumeLabel = new Label("Résumé: ");
        Label lienLabel = new Label("Lien: ");

        titreField.getStyleClass().add("text-field");
        auteurField.getStyleClass().add("text-field");
        presentationField.getStyleClass().add("text-field");
        parutionField.getStyleClass().add("text-field");
        colonneField.getStyleClass().add("text-field");
        rangeeField.getStyleClass().add("text-field");
        empruntCheckBox.getStyleClass().add("check-box");
        resumeArea.getStyleClass().add("text-area");
        lienField.getStyleClass().add("text-field");
        ajouterButton.getStyleClass().add("button");

        titreLabel.getStyleClass().add("label");
        auteurLabel.getStyleClass().add("label");
        presentationLabel.getStyleClass().add("label");
        parutionLabel.getStyleClass().add("label");
        colonneLabel.getStyleClass().add("label");
        rangeeLabel.getStyleClass().add("label");
        empruntLabel.getStyleClass().add("label");
        resumeLabel.getStyleClass().add("label");
        lienLabel.getStyleClass().add("label");

        previewImageView.getStyleClass().add("image-view");

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

        gridPane.getStyleClass().add("grid-pane");

        this.getChildren().add(gridPane);
        chargerLivresDansTableView(tableView);

        this.getStyleClass().add("formulaire");
    }

    /**
     * Cette méthode charge les livres dans le TableView.
     * 
     * @param tableView
     */

    public void chargerLivresDansTableView(LivreTableView tableView) {
        try {
            List<Livre> livres = dbManager.getLivres();
            tableView.getItems().addAll(livres);
        } catch (SQLException e) {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des livres");
        }
    }
}
