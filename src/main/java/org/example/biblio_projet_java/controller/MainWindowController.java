package org.example.biblio_projet_java.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.Optional;

import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.example.biblio_projet_java.utils.AlertUtils;
import org.example.biblio_projet_java.utils.WordExporter;
import org.example.biblio_projet_java.view.LivreTableView;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainWindowController {
    public MainWindowController() {
    }

    public boolean syncData(Stage primaryStage, DatabaseManager databaseManager, LivreTableView tableView) {
        // Implement your synchronization logic here
        Alert syncChoiceAlert = new Alert(Alert.AlertType.CONFIRMATION);
        syncChoiceAlert.setTitle("Synchronisation");
        syncChoiceAlert.setHeaderText("Voulez-vous envoyer les données locales au serveur ?");
        syncChoiceAlert.setContentText("Choisissez votre option.");

        ButtonType buttonTypeOne = new ButtonType("Envoyer les données locales");
        ButtonType buttonTypeTwo = new ButtonType("Récupérer les données du serveur");
        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        syncChoiceAlert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = syncChoiceAlert.showAndWait();
        if (result.get() == buttonTypeOne) {
            try {
                boolean rep = databaseManager.syncData(tableView.getItems(), databaseManager.isAdmin());
                if (rep) {
                    AlertUtils.showAlert(Alert.AlertType.INFORMATION, "Synchronisation",
                            "Données envoyées avec succès.");
                    return true;
                } else {
                    if (databaseManager.isUserConnected()) {
                        if (!databaseManager.isAdmin()) {
                            AlertUtils
                                    .showAlert(Alert.AlertType.ERROR, "Erreur",
                                            "Vous n'avez pas les autorisations nécessaires pour effectuer cette action.");
                            return false;
                        } else {
                            AlertUtils
                                    .showAlert(Alert.AlertType.ERROR, "Erreur",
                                            "Erreur lors de l'envoi des données : " + "Vous n'êtes pas connecté.");
                            return false;
                        }
                    } else {
                        AlertUtils
                                .showAlert(Alert.AlertType.ERROR, "Erreur",
                                        "Vous devez être connecté pour effectuer cette action.");
                        return false;
                    }
                }
            } catch (SQLException e) {
                AlertUtils
                        .showAlert(Alert.AlertType.ERROR, "Erreur",
                                "Erreur lors de la synchronisation des données : " + e.getMessage());
                return false;
            }
        } else if (result.get() == buttonTypeTwo) {
            try {
                boolean rep = databaseManager.syncData(tableView.getItems(), databaseManager.isAdmin());
                if (rep) {
                    AlertUtils
                            .showAlert(Alert.AlertType.INFORMATION, "Synchronisation",
                                    "Données récupérées avec succès.");
                } else {
                    AlertUtils
                            .showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des données.");
                    return false;
                }
            } catch (SQLException e) {
                AlertUtils
                        .showAlert(Alert.AlertType.ERROR, "Erreur",
                                "Erreur lors de la synchronisation des données : " + e.getMessage());
                return false;
            }
        }
        return false;
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
