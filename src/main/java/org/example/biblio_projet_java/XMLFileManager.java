package org.example.biblio_projet_java;

import javafx.scene.control.Alert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.example.biblio_projet_java.Bibliotheque.Livre;

import java.io.File;
import java.util.List;

public class XMLFileManager {

    public static File chargerFichierXML(File file, LivreTableView tableView) {
        try {

            JAXBContext context = JAXBContext.newInstance(Bibliotheque.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Bibliotheque bibliotheque = (Bibliotheque) unmarshaller.unmarshal(file);

            tableView.getItems().clear();

            bibliotheque.getLivre().forEach(tableView::ajouterLivre);
            return file;
        } catch (JAXBException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement du fichier XML.");
            alert.showAndWait();
            return null;
        }
    }

    public static File sauvegarderFichierXML(File file, List<Livre> livres) {
        try {
            JAXBContext context = JAXBContext.newInstance(Bibliotheque.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Bibliotheque bibliotheque = (Bibliotheque) unmarshaller.unmarshal(file);

            // Supprimer les anciens livres
            bibliotheque.getLivre().clear();

            // Ajouter les nouveaux livres
            bibliotheque.getLivre().addAll(livres);

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(bibliotheque, file);
            return file; // Renvoyer le fichier sauvegardé
        } catch (JAXBException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la sauvegarde du fichier XML.");
            alert.showAndWait();
            return null; // En cas d'erreur, renvoyer null
        }
    }

}