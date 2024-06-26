package org.example.biblio_projet_java.view;

import org.example.biblio_projet_java.Bibliotheque;
import org.example.biblio_projet_java.Bibliotheque.Livre;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.ShortStringConverter;

/**
 * Cette classe représente une vue de tableau pour les livres.
 * Elle étend la classe TableView et affiche les informations des livres dans
 * des colonnes.
 * Les colonnes incluent le titre, l'auteur, la présentation, la parution, la
 * colonne, la rangée, l'emprunt, le résumé et le lien.
 * Elle permet également de supprimer des livres de la table.
 */
public class LivreTableView extends TableView<Bibliotheque.Livre> {

    /**
     * Constructeur de la classe LivreTableView.
     */
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
        TableColumn<Bibliotheque.Livre, Boolean> empruntCol = new TableColumn<>("Emprunt");
        TableColumn<Bibliotheque.Livre, String> resumeCol = new TableColumn<>("Résumé");
        TableColumn<Bibliotheque.Livre, String> lienCol = new TableColumn<>("Lien");

        TableColumn<Bibliotheque.Livre, Void> deleteCol = new TableColumn<>("");

        // Liaison des colonnes aux données
        titreCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getTitre()));
        auteurCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(
                data.getValue().getAuteur().getNom() + " " + data.getValue().getAuteur().getPrenom()));
        presentationCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getPresentation()));
        parutionCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getParution()));
        colonneCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getColonne()));
        rangeeCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getRangee()));
        empruntCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().isEmprunt()));
        resumeCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getResume()));
        lienCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getLien()));

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

        empruntCol.setCellFactory(CheckBoxTableCell.forTableColumn(empruntCol));
        empruntCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isEmprunt()));
        resumeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        resumeCol.setOnEditCommit(event -> {
            event.getRowValue().setResume(event.getNewValue());
        });

        lienCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lienCol.setOnEditCommit(event -> {
            event.getRowValue().setLien(event.getNewValue());
        });

        // Ajout des colonnes au tableau
        getColumns().addAll(titreCol, auteurCol, presentationCol, parutionCol, colonneCol, rangeeCol, empruntCol,
                resumeCol, lienCol, deleteCol);
    }

    /**
     * Ajoute un livre à la liste des livres affichés dans la table.
     *
     * @param nouveauLivre Le livre à ajouter.
     */
    public void ajouterLivre(Livre nouveauLivre) {
        getItems().add(nouveauLivre);
    }

    /**
     * Supprime un livre de la table de vue.
     * 
     * @param livre Le livre à supprimer.
     */
    public void supprimerLivre(Bibliotheque.Livre livre) {
        getItems().remove(livre);
    }

    /**
     * Modifie un livre dans la table de vue.
     *
     * @param livre Le livre à modifier.
     */
    public void modifierLivre(Bibliotheque.Livre livre) {
        getItems().set(getItems().indexOf(livre), livre);
    }

    /**
     * Affiche le livre spécifié dans la table.
     * 
     * @param livre Le livre à afficher.
     */
    public void afficherLivre(Bibliotheque.Livre livre) {
        getSelectionModel().select(livre);
    }

    /**
     * Renvoie la vue de la table pour l'objet Livre.
     *
     * @return La vue de la table pour l'objet Livre.
     */
    public Node getTableView() {
        return this;
    }

    /**
     * Renvoie le livre sélectionné dans la table.
     *
     * @return le livre sélectionné dans la table.
     */
    public Bibliotheque.Livre getLivreSelectionne() {
        return getSelectionModel().getSelectedItem();
    }

}