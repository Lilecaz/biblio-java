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
        Alert syncChoiceAlert = new Alert(Alert.AlertType.CONFIRMATION);
        syncChoiceAlert.setTitle("Synchronisation");
        syncChoiceAlert.setHeaderText("Voulez-vous envoyer les données locales au serveur ?");
        syncChoiceAlert.setContentText("Choisissez votre option.");

        ButtonType buttonTypeOne = new ButtonType("Envoyer les données locales");
        ButtonType buttonTypeTwo = new ButtonType("Récupérer les données du serveur");
        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        syncChoiceAlert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = syncChoiceAlert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeOne) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                File loadedFile = XMLFileManager.chargerFichierXML(selectedFile, tableView);
                if (loadedFile != null) {
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirmation des données");
                    confirmAlert.setHeaderText("Voulez-vous envoyer ces données au serveur ?");
                    confirmAlert.setContentText("Confirmez pour envoyer les données au serveur.");

                    ButtonType confirmButton = new ButtonType("Confirmer");
                    ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
                    confirmAlert.getButtonTypes().setAll(confirmButton, cancelButton);

                    Optional<ButtonType> confirmResult = confirmAlert.showAndWait();
                    if (confirmResult.isPresent() && confirmResult.get() == confirmButton) {
                        try {
                            boolean rep = databaseManager.syncData(tableView.getItems(), databaseManager.isAdmin());
                            if (rep) {
                                AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Synchronisation",
                                        "Données envoyées avec succès.");
                                return true;
                            } else {
                                showSyncError(databaseManager);
                                return false;
                            }
                        } catch (SQLException e) {
                            AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                                    "Erreur lors de la synchronisation des données : " + e.getMessage());
                            return false;
                        }
                    }
                } else {
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement du fichier XML.");
                }
            }
        } else if (result.isPresent() && result.get() == buttonTypeTwo) {
            try {
                boolean rep = databaseManager.syncData(tableView.getItems(), databaseManager.isAdmin());
                if (rep) {
                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Synchronisation",
                            "Données récupérées avec succès.");
                    return true;
                } else {
                    AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Erreur lors de la récupération des données.");
                    return false;
                }
            } catch (SQLException e) {
                AlertUtils.showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Erreur lors de la synchronisation des données : " + e.getMessage());
                return false;
            }
        }
        return false;
    }

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
