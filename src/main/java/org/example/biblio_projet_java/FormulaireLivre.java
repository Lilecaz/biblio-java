package org.example.biblio_projet_java;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

import org.example.biblio_projet_java.Bibliotheque.Livre;

public class FormulaireLivre extends VBox {

    private TextField titreField;
    private TextField auteurField;
    private TextField presentationField;
    private TextField parutionField;
    private TextField colonneField;
    private TextField rangeeField;
    private Button ajouterButton;

    public FormulaireLivre(LivreTableView tableView) {
        Label titreLabel = new Label("Titre: ");
        titreField = new TextField();

        Label auteurLabel = new Label("Auteur: ");
        auteurField = new TextField();

        Label presentationLabel = new Label("Présentation: ");
        presentationField = new TextField();

        Label parutionLabel = new Label("Parution: ");
        parutionField = new TextField();
        parutionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                parutionField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Label colonneLabel = new Label("Colonne: ");
        colonneField = new TextField();
        colonneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                colonneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        Label rangeeLabel = new Label("Rangée: ");
        rangeeField = new TextField();
        rangeeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                rangeeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        ajouterButton = new Button("Ajouter");
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

                tableView.ajouterLivre(nouveauLivre);

                clearFields();
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
        gridPane.addRow(6, ajouterButton);

        this.getChildren().add(gridPane);

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

    private boolean alreadyExists(LivreTableView tableView) {
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
}
