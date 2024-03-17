package org.example.biblio_projet_java;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

import org.example.biblio_projet_java.Bibliotheque.Livre;

public class FormulaireLivre extends VBox {

    private TextField titreField;
    private TextField auteurNomField;
    private TextField auteurPrenomField;
    private TextField presentationField;
    private TextField parutionField;
    private TextField colonneField;
    private TextField rangeeField;
    private Button ajouterButton;

    public FormulaireLivre(LivreTableView tableView) {
        titreField = new TextField();
        titreField.setPromptText("Titre");

        auteurNomField = new TextField();
        auteurNomField.setPromptText("Nom de l'auteur");

        auteurPrenomField = new TextField();
        auteurPrenomField.setPromptText("Prénom de l'auteur");

        presentationField = new TextField();
        presentationField.setPromptText("Présentation");

        parutionField = new TextField();
        parutionField.setPromptText("Parution");
        parutionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                parutionField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        colonneField = new TextField();
        colonneField.setPromptText("Colonne");
        colonneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                colonneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        rangeeField = new TextField();
        rangeeField.setPromptText("Rangée");
        rangeeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                rangeeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        ajouterButton = new Button("Ajouter");
        ajouterButton.setOnAction(event -> {
            if (validateFields() && !alreadyExists(tableView)) {
                // Création d'un nouvel objet Livre avec les données du formulaire
                Livre nouveauLivre = new Livre();
                nouveauLivre.setTitre(titreField.getText());
                Livre.Auteur auteur = new Livre.Auteur();
                auteur.setNom(auteurNomField.getText());
                auteur.setPrenom(auteurPrenomField.getText());
                nouveauLivre.setAuteur(auteur);
                nouveauLivre.setPresentation(presentationField.getText());
                nouveauLivre.setParution(Integer.parseInt(parutionField.getText()));
                nouveauLivre.setColonne(Short.parseShort(colonneField.getText()));
                nouveauLivre.setRangee(Short.parseShort(rangeeField.getText()));

                // Ajout du nouveau livre à la table
                tableView.ajouterLivre(nouveauLivre);

                // Effacement des champs du formulaire
                clearFields();
            }
        });

        this.getChildren().addAll(titreField, auteurNomField, auteurPrenomField, presentationField,
                parutionField, colonneField, rangeeField, ajouterButton);
    }

    private boolean validateFields() {
        if (titreField.getText().isEmpty() || auteurNomField.getText().isEmpty() ||
                auteurPrenomField.getText().isEmpty() || presentationField.getText().isEmpty() ||
                parutionField.getText().isEmpty() || colonneField.getText().isEmpty() ||
                rangeeField.getText().isEmpty()) {
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
        auteurNomField.clear();
        auteurPrenomField.clear();
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
                    livre.getAuteur().getNom().equals(auteurNomField.getText()) &&
                    livre.getAuteur().getPrenom().equals(auteurPrenomField.getText()) &&
                    livre.getParution() == anneeParution) {
                showAlert("Ce livre existe déjà dans la bibliothèque.");
                return true;
            }
        }
        return false;
    }
}
