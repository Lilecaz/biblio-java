package org.example.biblio_projet_java.controller;

import java.sql.SQLException;
import java.time.LocalDate;

import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.example.biblio_projet_java.utils.AlertUtils;
import org.example.biblio_projet_java.view.LivreTableView;

import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FormLivreController {

    /**
     * Cette méthode permet de valider le contenu du TexteField
     * 
     * @param parutionField
     */
    public static void validateParutionField(TextField parutionField) {
        parutionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                parutionField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Cette méthode permet de valider le contenu de colonneField
     * 
     * @param colonneField
     */
    public static void filterColonneInput(TextField colonneField) {
        colonneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                colonneField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Cette méthode permet de valider le contenu du rangeeField
     * 
     * @param rangeeField
     */
    public static void filterRangeeInput(TextField rangeeField) {
        rangeeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                rangeeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Cette méthode permet de mettre à jour previewImageView avec une image à
     * partir du lien URL saisi dans le champ de texte
     * 
     * @param lienField
     * @param previewImageView
     */
    public static void handleLinkChange(TextField lienField, ImageView previewImageView) {
        lienField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    Image image = new Image(newValue);
                    previewImageView.setImage(image);
                    lienField.setStyle("-fx-text-fill: black;");
                } catch (IllegalArgumentException e) {
                    lienField.setStyle("-fx-text-fill: red;");
                }
            } else {
                // Effacer l'aperçu de l'image s'il n'y a pas de lien
                previewImageView.setImage(null);
            }
        });
    }

    /**
     * Cette méthode permet de gérer la soumission d'un nouveau livre à ajouter à la
     * db et à la vue du tableau et de valider les TextField et controler l'ajout
     * d'un livre
     * 
     * @param tableView
     * @param dbManager
     * @param titreField
     * @param auteurField
     * @param presentationField
     * @param parutionField
     * @param colonneField
     * @param rangeeField
     * @param empruntCheckBox
     * @param resumeArea
     * @param lienField
     */
    public static void handleNewBookSubmission(LivreTableView tableView, DatabaseManager dbManager,
            TextField titreField,
            TextField auteurField, TextField presentationField, TextField parutionField, TextField colonneField,
            TextField rangeeField, CheckBox empruntCheckBox, TextArea resumeArea, TextField lienField) {
        if (validateFields(titreField, auteurField, presentationField, parutionField, colonneField, rangeeField,
                empruntCheckBox, resumeArea, lienField)
                && !alreadyExists(tableView, titreField, auteurField, parutionField)) {
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

                    clearFields(titreField, auteurField, presentationField, parutionField, colonneField, rangeeField,
                            empruntCheckBox, resumeArea, lienField);
                } else {
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Erreur lors de l'ajout du livre à la base de données.");
                }
            } catch (SQLException e) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Une erreur s'est produite lors de l'ajout du livre à la base de données.");
            }
        }
    }

    /**
     * Cett méthode permet de valider les TextField (champs obligatoires)
     * 
     * @param titreField
     * @param auteurField
     * @param presentationField
     * @param parutionField
     * @param colonneField
     * @param rangeeField
     * @param empruntCheckBox
     * @param resumeArea
     * @param lienField
     * @return
     */
    private static boolean validateFields(TextField titreField, TextField auteurField, TextField presentationField,
            TextField parutionField, TextField colonneField, TextField rangeeField, CheckBox empruntCheckBox,
            TextArea resumeArea, TextField lienField) {
        if (titreField.getText().isEmpty() || auteurField.getText().isEmpty() ||
                presentationField.getText().isEmpty() || parutionField.getText().isEmpty() ||
                colonneField.getText().isEmpty() || rangeeField.getText().isEmpty()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Champs vides",
                    "Veuillez remplir tous les champs obligatoires.");
            return false;
        }

        int parution = Integer.parseInt(parutionField.getText());
        if (parution > LocalDate.now().getYear()) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Année de parution invalide",
                    "L'année de parution ne peut pas être supérieure à l'année actuelle.");
            return false;
        }

        int rangee = Integer.parseInt(rangeeField.getText());
        if (rangee < 1 || rangee > 5) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Numéro de rangée invalide",
                    "Le numéro de rangée doit être compris entre 1 et 5.");
            return false;
        }

        int colonne = Integer.parseInt(colonneField.getText());
        if (colonne < 0 || colonne > 7) {
            AlertUtils.showAlert(Alert.AlertType.WARNING, "Numéro de colonne invalide",
                    "Le numéro de colonne doit être compris entre 0 et 7.");
            return false;
        }

        return true;
    }

    /**
     * Cette méthode permet de vérifier si un livre avec le même titre, même auteur
     * et même année de parution existe déjà
     * 
     * @param tableView
     * @param titreField
     * @param auteurField
     * @param parutionField
     * @return
     */
    public static boolean alreadyExists(LivreTableView tableView, TextField titreField, TextField auteurField,
            TextField parutionField) {
        String nomLivre = titreField.getText();
        int anneeParution = Integer.parseInt(parutionField.getText());

        for (Livre livre : tableView.getItems()) {
            if (livre.getTitre().equals(nomLivre) &&
                    livre.getAuteur().getNom().equals(auteurField.getText().split(" ")[0]) &&
                    livre.getAuteur().getPrenom().equals(auteurField.getText().split(" ")[1]) &&
                    livre.getParution() == anneeParution) {

                return true;
            }
        }
        return false;
    }

    /**
     * Cette méthode permet d'effacer le contenu des champs de saisie et de la zone
     * de texte spécifiés
     * 
     * @param titreField
     * @param auteurField
     * @param presentationField
     * @param parutionField
     * @param colonneField
     * @param rangeeField
     * @param empruntCheckBox
     * @param resumeArea
     * @param lienField
     */
    private static void clearFields(TextField titreField, TextField auteurField, TextField presentationField,
            TextField parutionField, TextField colonneField, TextField rangeeField, CheckBox empruntCheckBox,
            TextArea resumeArea, TextField lienField) {
        titreField.clear();
        auteurField.clear();
        presentationField.clear();
        parutionField.clear();
        colonneField.clear();
        rangeeField.clear();
    }
}
