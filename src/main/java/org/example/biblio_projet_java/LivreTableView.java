package org.example.biblio_projet_java;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.*;

public class LivreTableView extends TableView<Livre> {

    private TableView<String[]> tableView;

    public LivreTableView() {
        tableView = new TableView<>();
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

        // Ajout des colonnes au tableau
        tableView.getColumns().addAll(titreCol, auteurCol, presentationCol, parutionCol, colonneCol, rangeeCol);

    }

    public void ajouterLivre(String[] data) {
        System.out.println("ajout String " + data);
        tableView.getItems().add(data);
    }

    public void supprimerLivre(String[] data) {
        tableView.getItems().remove(data);
    }

    public void ajouterLivre(Livre livre) {
        String[] data = new String[] {
                livre.getTitre(),
                livre.getAuteur().getNom() + " " + livre.getAuteur().getPrenom(),
                livre.getPresentation(),
                String.valueOf(livre.getParution()),
                String.valueOf(livre.getColonne()),
                String.valueOf(livre.getRangee())
        };
        tableView.getItems().add(data);
    }

    public void supprimerLivre(Livre livre) {
        getItems().remove(livre); // Use getItems() for Livre type
    }

    public TableView<String[]> getTableView() {
        return tableView;
    }

}
