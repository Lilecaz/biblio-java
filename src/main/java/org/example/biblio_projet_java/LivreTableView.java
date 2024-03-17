package org.example.biblio_projet_java;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.ShortStringConverter;

public class LivreTableView extends TableView<Bibliotheque.Livre> {

    @SuppressWarnings("unchecked")
    public LivreTableView() {
        setEditable(true);

        // Création des colonnes du tableau
        TableColumn<Bibliotheque.Livre, String> titreCol = new TableColumn<>("Titre");
        TableColumn<Bibliotheque.Livre, String> auteurCol = new TableColumn<>("Auteur");
        TableColumn<Bibliotheque.Livre, String> presentationCol = new TableColumn<>("Présentation");
        TableColumn<Bibliotheque.Livre, Integer> parutionCol = new TableColumn<>("Parution");
        TableColumn<Bibliotheque.Livre, Short> colonneCol = new TableColumn<>("Colonne");
        TableColumn<Bibliotheque.Livre, Short> rangeeCol = new TableColumn<>("Rangee");
        TableColumn<Bibliotheque.Livre, Void> deleteCol = new TableColumn<>("");

        // Liaison des colonnes aux données
        titreCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTitre()));
        auteurCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(
                data.getValue().getAuteur().getNom() + " " + data.getValue().getAuteur().getPrenom()));
        presentationCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPresentation()));
        parutionCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getParution()));
        colonneCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getColonne()));
        rangeeCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getRangee()));
        Callback<TableColumn<Bibliotheque.Livre, Void>, TableCell<Bibliotheque.Livre, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Bibliotheque.Livre, Void> call(final TableColumn<Bibliotheque.Livre, Void> param) {
                final TableCell<Bibliotheque.Livre, Void> cell = new TableCell<>() {
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        deleteButton.setOnAction(event -> {
                            Bibliotheque.Livre livre = getTableView().getItems().get(getIndex());
                            getTableView().getItems().remove(livre);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
                return cell;
            }
        };

        deleteCol.setCellFactory(cellFactory);

        // Rendre les colonnes éditables
        titreCol.setCellFactory(TextFieldTableCell.forTableColumn());
        titreCol.setOnEditCommit(event -> {
            event.getRowValue().setTitre(event.getNewValue());
        });

        auteurCol.setCellFactory(TextFieldTableCell.forTableColumn());
        auteurCol.setOnEditCommit(event -> {
            // l'auteur est divisé en deux nom et prenom, on doit donc séparer les deux
            String[] auteur = event.getNewValue().split(" ");
            event.getRowValue().getAuteur().setNom(auteur[0]);
            event.getRowValue().getAuteur().setPrenom(auteur[1]);

        });

        presentationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        presentationCol.setOnEditCommit(event -> {
            event.getRowValue().setPresentation(event.getNewValue());
        });

        parutionCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        parutionCol.setOnEditCommit(event -> {
            event.getRowValue().setParution(event.getNewValue());
        });

        colonneCol.setCellFactory(TextFieldTableCell.forTableColumn(new ShortStringConverter()));
        colonneCol.setOnEditCommit(event -> {
            event.getRowValue().setColonne(event.getNewValue());
        });

        rangeeCol.setCellFactory(TextFieldTableCell.forTableColumn(new ShortStringConverter()));
        rangeeCol.setOnEditCommit(event -> {
            event.getRowValue().setRangee(event.getNewValue());
        });

        // Ajout des colonnes au tableau
        getColumns().addAll(titreCol, auteurCol, presentationCol, parutionCol, colonneCol, rangeeCol, deleteCol);
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

    public Bibliotheque.Livre getLivreSelectionne() {
        return getSelectionModel().getSelectedItem();
    }

}