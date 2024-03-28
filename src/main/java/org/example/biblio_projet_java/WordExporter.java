package org.example.biblio_projet_java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import java.net.URLEncoder;

public class WordExporter {
    public static void exportToWord(List<Livre> livres, String titreDocument, Stage primaryStage)
            throws UnsupportedEncodingException {
        // Créer un document Word
        XWPFDocument document = new XWPFDocument();

        // Créer un en-tête avec le titre du document et la date d'exportation
        XWPFHeader header = document.createHeader(HeaderFooterType.DEFAULT);
        XWPFParagraph headerParagraph = header.createParagraph();
        XWPFRun headerRun = headerParagraph.createRun();
        headerRun.setText(titreDocument);
        headerRun.addBreak();
        headerRun.setText("Exporté le : " + LocalDate.now());

        // Créer le sommaire
        XWPFParagraph summaryParagraph = document.createParagraph();
        XWPFRun summaryRun = summaryParagraph.createRun();
        summaryRun.setText("Sommaire");
        summaryRun.addBreak();

        // Ajouter les titres des livres au sommaire
        for (Livre livre : livres) {
            // Créer un lien hypertexte pour chaque titre
            String anchor = URLEncoder.encode(livre.getTitre().replace(" ", "_"), "UTF-8");
            XWPFHyperlinkRun hyperlinkRun = summaryParagraph.createHyperlinkRun("#" + anchor);
            hyperlinkRun.setText("Titre: " + livre.getTitre());
            hyperlinkRun.addBreak();
        }

        // Ajouter les détails de chaque livre sur une nouvelle page
        for (Livre livre : livres) {
            // Créer une ancre pour la page du livre
            String anchor = URLEncoder.encode(livre.getTitre().replace(" ", "_"), "UTF-8");
            XWPFParagraph anchorParagraph = document.createParagraph();
            anchorParagraph.createRun().setText(anchor);
            anchorParagraph.setPageBreak(true); // Assurez-vous que chaque ancre est sur une nouvelle page

            // Créer une nouvelle page pour chaque livre avec sa description
            XWPFParagraph contentParagraph = document.createParagraph();
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText("Titre: " + livre.getTitre());
            contentRun.addBreak();
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
                alert.setContentText(
                        "Une erreur s'est produite lors de l'exportation des données dans le document Word.");
                alert.showAndWait();
            }
        }
    }
}