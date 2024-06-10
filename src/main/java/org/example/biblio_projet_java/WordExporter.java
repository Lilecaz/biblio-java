package org.example.biblio_projet_java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.poi.xwpf.usermodel.*;

public class WordExporter {

    private XWPFDocument document;

    public WordExporter() {
        this.document = new XWPFDocument();
    }

    public void export(List<Livre> livres, String titreDocument, Stage primaryStage) {
        try {
            // Ajouter la page de garde
            addTitlePage(titreDocument);

            // Ajouter le sommaire
            addTableOfContent();

            // Ajouter les livres
            addLivres(livres);

            // Choisir l'emplacement pour enregistrer le fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter le document Word");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier Word", "*.docx"));
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                save(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'exportation");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'exportation des données dans le document Word.");
            alert.showAndWait();
        }
    }

    private void addTitlePage(String titreDocument) {
        XWPFParagraph titlePageParagraph = document.createParagraph();
        titlePageParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titlePageRun = titlePageParagraph.createRun();
        titlePageRun.setText(titreDocument);
        titlePageRun.setBold(true);
        titlePageRun.setFontSize(20);
        titlePageRun.addBreak();
        titlePageRun.addBreak();
        titlePageRun.setText("Exporté le : " + LocalDate.now());
        titlePageRun.addBreak();
        titlePageRun.addBreak();
        titlePageRun.setText("Liste des livres dans la bibliothèque");

        titlePageParagraph.setPageBreak(true); // Sauter à la page suivante
    }

    private void addTableOfContent() {
        // Créer le sommaire
        XWPFParagraph tocParagraph = document.createParagraph();
        CTSimpleField toc = tocParagraph.getCTP().addNewFldSimple();
        toc.setInstr("TOC \\o \"1-3\" \\h \\z \\u");
        toc.setDirty(STOnOff1.ON);

        // Ajouter un run au paragraphe pour le texte du sommaire
        XWPFRun tocRun = tocParagraph.createRun();
        tocRun.setText("Table des matières");
        tocRun.setBold(true);

        // Ajouter un saut de page après le sommaire
        tocParagraph.setPageBreak(true);

        // Ajouter les styles personnalisés
        addCustomHeadingStyle(document, "heading 1", 1);
        addCustomHeadingStyle(document, "heading 2", 2);
    }

    private void addLivres(List<Livre> livres) {
        for (Livre livre : livres) {
            // Ajouter les détails du livre avec les styles personnalisés
            addLivreDetails(document, livre);

            // Ajouter un saut de page après chaque livre
            XWPFParagraph pageBreakParagraph = document.createParagraph();
            pageBreakParagraph.setPageBreak(true);
        }
    }

    private static void addCustomHeadingStyle(XWPFDocument document, String strStyleId, int headingLevel) {
        CTStyle ctStyle = CTStyle.Factory.newInstance();
        ctStyle.setStyleId(strStyleId);

        CTString styleName = CTString.Factory.newInstance();
        styleName.setVal(strStyleId);
        ctStyle.setName(styleName);

        CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
        indentNumber.setVal(BigInteger.valueOf(headingLevel));

        ctStyle.setUiPriority(indentNumber);

        CTOnOff onoffnull = CTOnOff.Factory.newInstance();
        ctStyle.setUnhideWhenUsed(onoffnull);

        ctStyle.setQFormat(onoffnull);

        CTPPrGeneral ppr = CTPPrGeneral.Factory.newInstance();
        ppr.setOutlineLvl(indentNumber);
        ctStyle.setPPr(ppr);

        XWPFStyle style = new XWPFStyle(ctStyle);

        XWPFStyles styles = document.createStyles();

        style.setType(STStyleType.PARAGRAPH);
        styles.addStyle(style);
    }

    private static void addLivreDetails(XWPFDocument document, Livre livre) {
        // Titre du livre
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setStyle("heading 1");
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("Titre: " + livre.getTitre());
        titleRun.setBold(true);

        // Détails du livre
        XWPFParagraph contentParagraph = document.createParagraph();
        contentParagraph.setStyle("heading 2");
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

    public void save(File file) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            document.write(out);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exportation réussie");
            alert.setHeaderText(null);
            alert.setContentText("Le document a été exporté avec succès.");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'exportation");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur s'est produite lors de l'exportation du document.");
            alert.showAndWait();
        }
    }
}
