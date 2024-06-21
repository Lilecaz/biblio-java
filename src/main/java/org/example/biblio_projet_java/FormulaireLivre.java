package org.example.biblio_projet_java;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.example.biblio_projet_java.Bibliotheque.Livre;

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

    public FormulaireLivre(LivreTableView tableView, DatabaseManager dbManager) {
        this.dbManager = dbManager;

        previewImageView = new ImageView();
        previewImageView.setFitWidth(200); // Ajustez la largeur de l'aperçu selon vos besoins
        previewImageView.setPreserveRatio(true);
        Label titreLabel = new Label("Titre: ");

        Label auteurLabel = new Label("Auteur: ");

        Label presentationLabel = new Label("Présentation: ");

        Label parutionLabel = new Label("Parution: ");
        parutionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                parutionField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Label colonneLabel = new Label("Colonne: ");
        colonneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                colonneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Label rangeeLabel = new Label("Rangée: ");
        rangeeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                rangeeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Label empruntLabel = new Label("Emprunt: ");

        Label resumeLabel = new Label("Résumé: ");
        resumeArea.setWrapText(true);
        resumeArea.setPrefRowCount(3);

        Label lienLabel = new Label("Lien: ");
        lienField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                Image image = new Image(newValue);
                previewImageView.setImage(image);
            } else {
                // Effacer l'aperçu de l'image s'il n'y a pas de lien
                previewImageView.setImage(null);
            }
        });

        ajouterButton.setOnAction(event -> {
            if (validateFields() && !alreadyExists(tableView)) {
                Livre nouveauLivre = new Livre();
                nouveauLivre.setTitre(titreField.getText());
                String[] auteur = auteurField.getText().split(" ");
                Livre.Auteur auteurObj = new Livre.Auteur();
                if (auteur.length > 1) {
                    auteurObj.setNom(auteur[0]);
                    auteurObj.setPrenom(auteur[1]);
                } else {
                    auteurObj.setNom(auteur[0]);
                }
                nouveauLivre.setAuteur(auteurObj);
                nouveauLivre.setPresentation(presentationField.getText());
                nouveauLivre.setParution(Integer.parseInt(parutionField.getText()));
                nouveauLivre.setColonne(Short.parseShort(colonneField.getText()));
                nouveauLivre.setRangee(Short.parseShort(rangeeField.getText()));
                nouveauLivre.setEmprunt(empruntCheckBox.isSelected());
                nouveauLivre.setResume(resumeArea.getText());
                nouveauLivre.setLien(lienField.getText());

                try {
                    if (dbManager.ajouterLivre(nouveauLivre)) {
                        tableView.ajouterLivre(nouveauLivre);

                        clearFields();
                    } else {
                        showAlert("Erreur lors de l'ajout du livre à la base de données.");
                    }
                } catch (SQLException e) {
                    showAlert("Erreur lors de l'ajout du livre à la base de données : " + e.getMessage());
                }
            }
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

    private boolean validateFields() {
        if (titreField.getText().isEmpty() || auteurField.getText().isEmpty() ||
                presentationField.getText().isEmpty() || parutionField.getText().isEmpty() ||
                colonneField.getText().isEmpty() || rangeeField.getText().isEmpty()) {
            showAlert("Tous les champs sont obligatoires.");
            return false;
        }

        int parution = Integer.parseInt(parutionField.getText());
        if (parution > LocalDate.now().getYear()) {
            showAlert("L'année de parution ne peut pas être supérieure à la date actuelle.");
            return false;
        }

        int rangee = Integer.parseInt(rangeeField.getText());
        if (rangee < 1 || rangee > 5) {
            showAlert("La rangée du livre doit être comprise entre 1 et 5.");
            return false;
        }

        int colonne = Integer.parseInt(colonneField.getText());
        if (colonne < 0 || colonne > 7) {
            showAlert("La colonne doit être comprise entre 0 et 7.");
            return false;
        }

        return true;
    }

    private void clearFields() {
        titreField.clear();
        auteurField.clear();
        presentationField.clear();
        parutionField.clear();
        colonneField.clear();
        rangeeField.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean alreadyExists(LivreTableView tableView) {
        String nomLivre = titreField.getText();
        int anneeParution = Integer.parseInt(parutionField.getText());

        for (Livre livre : tableView.getItems()) {
            if (livre.getTitre().equals(nomLivre) &&
                    livre.getAuteur().getNom().equals(auteurField.getText().split(" ")[0]) &&
                    livre.getAuteur().getPrenom().equals(auteurField.getText().split(" ")[1]) &&
                    livre.getParution() == anneeParution) {
                showAlert("Ce livre existe déjà dans la bibliothèque.");
                return true;
            }
        }
        return false;
    }

    public void chargerLivresDansTableView(LivreTableView tableView) {
        try {
            List<Livre> livres = dbManager.getLivres();
            tableView.getItems().addAll(livres);
        } catch (SQLException e) {
            showAlert("Erreur lors du chargement des livres depuis la base de données : " + e.getMessage());
        }
    }
}