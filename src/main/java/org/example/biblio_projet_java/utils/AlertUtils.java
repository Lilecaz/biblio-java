package org.example.biblio_projet_java.utils;

import javafx.scene.control.Alert;

/**
 * Cette classe fournit des méthodes utilitaires pour afficher des alertes dans
 * une application JavaFX.
 */
public class AlertUtils {

    /**
     * Constructeur privé pour empêcher l'instanciation de la classe.
     */
    private AlertUtils() {
    }

    /**
     * Affiche une alerte d'information avec le titre et le message spécifiés.
     *
     * @param title   le titre de l'alerte
     * @param message le message de l'alerte
     */
    public static void showInformation(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    /**
     * Affiche une alerte d'erreur avec le titre et le message spécifiés.
     *
     * @param title   le titre de l'alerte
     * @param message le message de l'alerte
     */
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    /**
     * Affiche une alerte avec le type, le titre et le message spécifiés.
     *
     * @param alertType le type de l'alerte
     * @param title     le titre de l'alerte
     * @param message   le message de l'alerte
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
