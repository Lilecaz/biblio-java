package org.example.biblio_projet_java;

import org.w3c.dom.Node;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.biblio_projet_java.*;
import org.example.biblio_projet_java.Bibliotheque.*;

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
        colonneField = new TextField();
        colonneField.setPromptText("Colonne");
        rangeeField = new TextField();
        rangeeField.setPromptText("Rangée");
        ajouterButton = new Button("Ajouter");
        ajouterButton.setOnAction(event -> {
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
            titreField.clear();
            auteurNomField.clear();
            auteurPrenomField.clear();
            presentationField.clear();
            parutionField.clear();
            colonneField.clear();
            rangeeField.clear();
        });

        this.getChildren().addAll(titreField, auteurNomField, auteurPrenomField, presentationField, parutionField,
                colonneField, rangeeField,
                ajouterButton);

    }

}