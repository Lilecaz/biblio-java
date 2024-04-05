package org.example.biblio_projet_java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import org.example.biblio_projet_java.Bibliotheque.Livre;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.*;

public class WordExporter {
    public static void exportToWord(List<Livre> livres, String titreDocument, Stage primaryStage)
            throws IOException {
        // Créer un document Word
        try (XWPFDocument document = new XWPFDocument()) {
            // Créer un en-tête avec le titre du document et la date d'exportation
            XWPFHeader header = document.createHeader(HeaderFooterType.DEFAULT);
            XWPFParagraph headerParagraph = header.createParagraph();
            XWPFRun headerRun = headerParagraph.createRun();
            headerRun.setText(titreDocument);
            headerRun.addBreak();
            headerRun.setText("Exporté le : " + LocalDate.now());

            // Créer le sommaire avec des liens vers chaque livre
            XWPFParagraph summaryParagraph = document.createParagraph();
            XWPFRun summaryRun = summaryParagraph.createRun();
            summaryRun.setText("Sommaire");
            summaryRun.addBreak();

            for (Livre livre : livres) {
                // Créer une ancre pour chaque titre de livre
                String anchor = "titre_" + livre.getTitre().replace(" ", "_");
                XWPFParagraph anchorParagraph = document.createParagraph();
                anchorParagraph.setPageBreak(true);
                anchorParagraph.createRun().setText(anchor);
                anchorParagraph.setNumID(BigInteger.valueOf(1));

                // Ajouter le titre de livre au sommaire avec un lien hypertexte vers l'ancre
                // correspondante
                XWPFHyperlinkRun hyperlinkRun = summaryParagraph.createHyperlinkRun("#" + anchor);
                hyperlinkRun.setText("Titre: " + livre.getTitre());
                hyperlinkRun.addBreak();

                // Ajouter les détails du livre à côté du titre
                XWPFParagraph contentParagraph = document.createParagraph();
                XWPFRun contentRun = contentParagraph.createRun();
                contentRun.setText("Auteur: " + livre.getAuteur().getNom() + " " + livre.getAuteur().getPrenom());
                contentRun.addBreak();
                contentRun.setText("Parution: " + livre.getParution());
                contentRun.addBreak();
                contentRun.setText("Présentation: " + livre.getPresentation());
                contentRun.addBreak();
                contentRun.setText("Colonne: " + livre.getColonne());
                contentRun.addBreak();
                contentRun.setText("Rangée: " + livre.getRangee());
                contentRun.addBreak();
                contentRun.setText("Emprunt: " + (livre.isEmprunt() ? "Oui" : "Non"));
                contentRun.addBreak();
                contentRun.setText("Résumé: " + livre.getResume());
                contentRun.addBreak();
                contentRun.setText("Lien: " + livre.getLien());
                contentRun.addBreak();
            }

            // Exporter le document Word
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter le document Word");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier Word", "*.docx"));
            File file = fileChooser.showSaveDialog(primaryStage);
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
                    alert.setContentText(
                            "Une erreur s'est produite lors de l'exportation des données dans le document Word.");
                    alert.showAndWait();
                }
            }
        }
    }
}
