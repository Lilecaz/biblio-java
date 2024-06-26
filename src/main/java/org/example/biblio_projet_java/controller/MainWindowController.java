package org.example.biblio_projet_java.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;

import org.example.biblio_projet_java.utils.AlertUtils;
import org.example.biblio_projet_java.utils.WordExporter;
import org.example.biblio_projet_java.utils.XMLFileManager;
import org.example.biblio_projet_java.view.LivreTableView;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Cette classe représente le contrôleur de la fenêtre principale.
 * Elle contient des méthodes pour synchroniser les données, exporter un
 * document Word et gérer la connexion de l'utilisateur.
 */
public class MainWindowController {
    public MainWindowController() {
        // Constructeur par défaut
    }

    /**
     * Cette méthode permet de synchroniser les données locales avec le serveur ou
     * du serveur vers un XML.
     * 
     * @param primaryStage
     * @param databaseManager
     * @param tableView
     * @return
     */
    public boolean syncData(Stage primaryStage, DatabaseManager databaseManager, LivreTableView tableView) {
        Optional<ButtonType> result = showSyncChoiceAlert();
        if (!result.isPresent())
            return false;

        if (result.get().getText().equals("Envoyer les données locales")) {
            return handleLocalDataSend(primaryStage, databaseManager, tableView);
        } else if (result.get().getText().equals("Récupérer les données du serveur")) {
            return handleServerDataRetrieval(primaryStage, tableView);
        }

        return false;
    }

    private Optional<ButtonType> showSyncChoiceAlert() {
        Alert syncChoiceAlert = new Alert(Alert.AlertType.CONFIRMATION, "Choisissez votre option.",
                new ButtonType("Envoyer les données locales"),
                new ButtonType("Récupérer les données du serveur"),
                new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE));
        syncChoiceAlert.setTitle("Synchronisation");
        syncChoiceAlert.setHeaderText(
                "Voulez-vous envoyer les données locales au serveur ou récupérer les données du serveur ?");
        return syncChoiceAlert.showAndWait();
    }

    private boolean handleLocalDataSend(Stage primaryStage, DatabaseManager databaseManager, LivreTableView tableView) {
        File selectedFile = showFileChooser(primaryStage, "Envoyer les données locales", "*.xml", "XML files (*.xml)");
        if (selectedFile == null)
            return false;

        File loadedFile = XMLFileManager.chargerFichierXML(selectedFile, tableView);
        if (loadedFile == null) {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement du fichier XML.");
            return false;
        }

        return confirmAndSyncData(databaseManager, tableView);
    }

    private boolean handleServerDataRetrieval(Stage primaryStage, LivreTableView tableView) {
        File selectedFile = showFileChooser(primaryStage, "Sélectionner un fichier XML", "*.xml", "Fichiers XML");
        if (selectedFile == null)
            return false;

        boolean success = XMLFileManager.sauvegarderFichierXML(selectedFile, tableView.getItems());
        if (!success) {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de la sauvegarde des données dans le fichier XML.");
            return false;
        }

        AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Synchronisation",
                "Données sauvegardées avec succès dans le fichier XML.");
        return true;
    }

    private File showFileChooser(Stage primaryStage, String title, String extension, String description) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));
        return extension.equals("*.xml") ? fileChooser.showOpenDialog(primaryStage)
                : fileChooser.showSaveDialog(primaryStage);
    }

    private boolean confirmAndSyncData(DatabaseManager databaseManager, LivreTableView tableView) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Confirmez pour envoyer les données au serveur.",
                new ButtonType("Confirmer"),
                new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE));
        confirmAlert.setTitle("Confirmation des données");
        confirmAlert.setHeaderText("Voulez-vous envoyer ces données au serveur ?");
        Optional<ButtonType> confirmResult = confirmAlert.showAndWait();

        if (!confirmResult.isPresent() || !confirmResult.get().getText().equals("Confirmer"))
            return false;

        try {
            boolean rep = databaseManager.syncData(tableView.getItems(), databaseManager.isAdmin());
            if (!rep) {
                showSyncError(databaseManager);
                return false;
            }
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Synchronisation", "Données envoyées avec succès.");
            return true;
        } catch (SQLException e) {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de la synchronisation des données : " + e.getMessage());
            return false;
        }
    }

    /**
     * Cette méthode affiche une alerte d'erreur lors de la synchronisation des
     * données.
     * 
     * @param databaseManager
     */
    private void showSyncError(DatabaseManager databaseManager) {
        if (databaseManager.isUserConnected()) {
            if (!databaseManager.isAdmin()) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Vous n'avez pas les autorisations nécessaires pour effectuer cette action.");
            } else {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Erreur lors de l'envoi des données : Vous n'êtes pas connecté.");
            }
        } else {
            AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Vous devez être connecté pour effectuer cette action.");
        }
    }

    /**
     * Cette méthode permet d'exporter les données de la table dans un document
     * Word.
     * 
     * @param primaryStage
     * @param tableView
     */

    public void exportDocumentToWord(Stage primaryStage, LivreTableView tableView) {
        if (!tableView.getItems().isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Exporter le document Word");
            dialog.setHeaderText("Veuillez saisir le nom du document :");
            dialog.setContentText("Nom du document:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                WordExporter wordExporter = new WordExporter();
                wordExporter.export(tableView.getItems(), name, primaryStage);
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Exporter le document Word");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Word", "*.docx"));
                File selectedFile = fileChooser.showSaveDialog(primaryStage);
                if (selectedFile != null) {
                    wordExporter.save(selectedFile);
                }
            });
        } else {
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Information", "Aucun livre à exporter.");
        }
    }

    /**
     * Cette méthode gère la connexion de l'utilisateur.
     * 
     * @param primaryStage
     * @param databaseManager
     * @return
     */
    public boolean handleUserAdminConnection(Stage primaryStage, DatabaseManager databaseManager) {
        if (databaseManager.isUserConnected()) {
            if (databaseManager.isAdmin()) {
                return true;

            } else {
                return false;
            }
        } else {
            AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Information", "Connexion échouée.");
        }
        return false;
    }
}
