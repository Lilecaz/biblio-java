package org.example.biblio_projet_java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.example.biblio_projet_java.Bibliotheque.Livre;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFHeader;


public class WordExporter {
    public static void exportToWord(List<Livre> livres, String titreDocument, Stage primaryStage) {
        // Créer un document Word
        XWPFDocument document = new XWPFDocument();

        // Créer un en-tête avec le titre du document et la date d'exportation
        XWPFHeader header = document.createHeader(HeaderFooterType.DEFAULT);
        XWPFParagraph headerParagraph = header.createParagraph();
        XWPFRun headerRun = headerParagraph.createRun();
        headerRun.setText(titreDocument);
        headerRun.addBreak();
        headerRun.setText("Exporté le : " + LocalDate.now());

        // Ajouter un sommaire
        XWPFParagraph summaryParagraph = document.createParagraph();
        XWPFRun summaryRun = summaryParagraph.createRun();
        summaryRun.setText("Sommaire");
        summaryRun.addBreak();

        // Ajouter les données de la bibliothèque dans le document
        for (Livre livre : livres) {
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText("Titre: " + livre.getTitre());
            contentRun.addBreak();
            contentRun.setText("Auteur: " + livre.getAuteur().getNom() + " " + livre.getAuteur().getPrenom());
            contentRun.addBreak();
            // Ajoutez d'autres détails du livre selon vos besoins
        }

        // Exporter le document Word
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter le document Word");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier Word", "*.docx"));
        File file = fileChooser.showSaveDialog(primaryStage); // Assurez-vous d'avoir une référence à primaryStage
        if (file != null) {
            try (FileOutputStream out = new FileOutputStream(file)) {
                document.write(out);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportation réussie");
                alert.setHeaderText(null);
                alert.setContentText("Les données ont été exportées avec succès dans le document Word.");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'exportation");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors de l'exportation des données dans le document Word.");
                alert.showAndWait();
            }
        }
    }
}

