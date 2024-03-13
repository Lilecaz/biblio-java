package org.example.biblio_projet_java;

import org.w3c.dom.Node;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class FormulaireLivre extends VBox {

    private TextField titreField;
    private TextField auteurField;
    private TextField presentationField;
    private TextField parutionField;
    private TextField colonneField;
    private TextField rangeeField;
    private Button ajouterButton;

    public FormulaireLivre(LivreTableView tableView) {
        titreField = new TextField();
        titreField.setPromptText("Titre");
        auteurField = new TextField();
        auteurField.setPromptText("Auteur");
        presentationField = new TextField();
        presentationField.setPromptText("Presentation");
        parutionField = new TextField();
        parutionField.setPromptText("Parution");
        colonneField = new TextField();
        colonneField.setPromptText("Colonne");
        rangeeField = new TextField();
        rangeeField.setPromptText("Rangee");
        ajouterButton = new Button("Ajouter");
        ajouterButton.setOnAction(event -> {
            // Création d'un nouvel objet Livre avec les données du formulaire
            Livre nouveauLivre = new Livre();
            nouveauLivre.setTitre(titreField.getText());
            String auteurNom = auteurField.getText().split(" ")[0];
            String auteurPrenom = auteurField.getText().split(" ")[1];

            nouveauLivre.setAuteur(new Auteur(auteurNom, auteurPrenom));
            nouveauLivre.setPresentation(presentationField.getText());
            nouveauLivre.setParution(Integer.parseInt(parutionField.getText()));
            nouveauLivre.setColonne(Integer.parseInt(colonneField.getText()));
            nouveauLivre.setRangee(Integer.parseInt(rangeeField.getText()));

            // Ajout du nouveau livre à la table
            tableView.ajouterLivre(nouveauLivre);
        });

        this.getChildren().addAll(titreField, auteurField, presentationField, parutionField, colonneField, rangeeField,
                ajouterButton);

    }

}