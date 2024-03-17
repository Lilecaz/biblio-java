package org.example.biblio_projet_java;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.example.biblio_projet_java.Bibliotheque.*;

public class LivreTableView extends TableView<Bibliotheque.Livre> {

    public LivreTableView() {
        setEditable(true);

        // Création des colonnes du tableau
        TableColumn<Bibliotheque.Livre, String> titreCol = new TableColumn<>("Titre");
        TableColumn<Bibliotheque.Livre, String> auteurCol = new TableColumn<>("Auteur");
        TableColumn<Bibliotheque.Livre, String> presentationCol = new TableColumn<>("Presentation");
        TableColumn<Bibliotheque.Livre, Integer> parutionCol = new TableColumn<>("Parution");
        TableColumn<Bibliotheque.Livre, Short> colonneCol = new TableColumn<>("Colonne");
        TableColumn<Bibliotheque.Livre, Short> rangeeCol = new TableColumn<>("Rangee");

        // Liaison des colonnes aux données
        titreCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTitre()));
        auteurCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(
                data.getValue().getAuteur().getNom() + " " + data.getValue().getAuteur().getPrenom()));
        presentationCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPresentation()));
        parutionCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getParution()));
        colonneCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getColonne()));
        rangeeCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getRangee()));

        // Ajout des colonnes au tableau
        getColumns().addAll(titreCol, auteurCol, presentationCol, parutionCol, colonneCol, rangeeCol);
    }

    public void ajouterLivre(Bibliotheque.Livre livre) {
        getItems().add(livre);
    }

    public void supprimerLivre(Bibliotheque.Livre livre) {
        getItems().remove(livre);
    }

    public void modifierLivre(Bibliotheque.Livre livre) {
        getItems().set(getItems().indexOf(livre), livre);
    }

    public void afficherLivre(Bibliotheque.Livre livre) {
        getSelectionModel().select(livre);
    }

    public Node getTableView() {
        return this;
    }

}