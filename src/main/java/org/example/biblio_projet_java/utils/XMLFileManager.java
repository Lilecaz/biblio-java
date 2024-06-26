package org.example.biblio_projet_java.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.example.biblio_projet_java.Bibliotheque;
import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.example.biblio_projet_java.view.LivreTableView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Cette classe permet de gérer la lecture et l'écriture de fichiers XML
 * contenant les données d'une bibliothèque.
 */
public class XMLFileManager {

    /**
     * Charge un fichier XML contenant les données d'une bibliothèque.
     * 
     * @param file      le fichier XML à charger
     * @param tableView la table view dans laquelle afficher les livres chargés
     * @return le fichier XML chargé, ou null en cas d'erreur
     */
    public static File chargerFichierXML(File file, LivreTableView tableView) {
        try {

            JAXBContext context = JAXBContext.newInstance(Bibliotheque.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Bibliotheque bibliotheque = (Bibliotheque) unmarshaller.unmarshal(file);

            tableView.getItems().clear();

            bibliotheque.getLivre().forEach(tableView::ajouterLivre);
            return file;
        } catch (JAXBException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement du fichier XML.");
            alert.showAndWait();
            return null;
        }
    }

    /**
     * Sauvegarde les livres dans un fichier XML.
     *
     * @param file   Le fichier dans lequel les livres doivent être sauvegardés.
     * @param livres La liste des livres à sauvegarder.
     * @return Le fichier dans lequel les livres ont été sauvegardés, ou null si
     *         l'opération a été annulée ou a échoué.
     */
    public static boolean sauvegarderFichierXML(File file, List<Livre> livres) {
        try {
            JAXBContext context = JAXBContext.newInstance(Bibliotheque.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Bibliotheque bibliotheque = new Bibliotheque();
            bibliotheque.getLivre().addAll(livres);

            if (file.exists()) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmation");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Le fichier sélectionné existe déjà. Voulez-vous le remplacer ?");
                ButtonType yesButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
                confirmAlert.getButtonTypes().setAll(yesButton, noButton);

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == yesButton) {
                    marshaller.marshal(bibliotheque, file);
                    return true;
                } else {
                    return false;
                }
            } else {
                file.createNewFile();
                marshaller.marshal(bibliotheque, file);
                return true;
            }
        } catch (IOException | JAXBException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la sauvegarde du fichier XML.");
            alert.showAndWait();
            return false;
        }
    }

}