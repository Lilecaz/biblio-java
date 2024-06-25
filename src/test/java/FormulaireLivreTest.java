import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.example.biblio_projet_java.controller.DatabaseManager;
import org.example.biblio_projet_java.view.FormulaireLivre;
import org.example.biblio_projet_java.view.LivreTableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class FormulaireLivreTest {

    private FormulaireLivre formulaireLivre;
    private DatabaseManager dbManager;
    private LivreTableView tableView;

    @Start
    public void start(Stage stage) throws SQLException {
        tableView = new LivreTableView();
        dbManager = new DatabaseManager();
        formulaireLivre = new FormulaireLivre(tableView, dbManager);
    }

    @BeforeEach
    public void setup() {
        formulaireLivre.titreField.setText("Test Titre");
        formulaireLivre.auteurField.setText("Test Auteur");
        formulaireLivre.presentationField.setText("Test Presentation");
        formulaireLivre.parutionField.setText("2021");
        formulaireLivre.colonneField.setText("1");
        formulaireLivre.rangeeField.setText("1");
        formulaireLivre.resumeArea.setText("Test Resume");
    }

    @Test
    public void testFormulaireLivre() {
        assertNotNull(formulaireLivre.titreField);
        assertNotNull(formulaireLivre.auteurField);
        assertNotNull(formulaireLivre.presentationField);
        assertNotNull(formulaireLivre.parutionField);
        assertNotNull(formulaireLivre.colonneField);
        assertNotNull(formulaireLivre.rangeeField);
        assertNotNull(formulaireLivre.empruntCheckBox);
        assertNotNull(formulaireLivre.resumeArea);
        assertNotNull(formulaireLivre.lienField);
        assertNotNull(formulaireLivre.ajouterButton);
    }

    @Test
    public void testFormulaireLivreParutionField() {
        formulaireLivre.parutionField.setText("2004");
        assertEquals("2004", formulaireLivre.parutionField.getText());
    }

    @Test
    public void testFormulaireLivreColonneField() {
        formulaireLivre.colonneField.setText("1");
        assertEquals("1", formulaireLivre.colonneField.getText());
    }

    @Test
    public void testFormulaireLivreRangeeField() {
        formulaireLivre.rangeeField.setText("6");
        assertEquals("6", formulaireLivre.rangeeField.getText());
    }

    @Test
    public void testFormulaireLivreAjouterButton() {
        Platform.runLater(() -> {
            formulaireLivre.titreField.setText("Test Titre");
            formulaireLivre.auteurField.setText("Test Auteur");
            formulaireLivre.presentationField.setText("Test Presentation");
            formulaireLivre.parutionField.setText("2021");
            formulaireLivre.colonneField.setText("1");
            formulaireLivre.rangeeField.setText("1");
            formulaireLivre.resumeArea.setText("Test Resume");
            formulaireLivre.lienField.setText("Test Lien");

            formulaireLivre.ajouterButton.fire();

            List<Livre> livres = FXCollections.observableArrayList();
            try {
                livres = dbManager.getLivres();
            } catch (SQLException e) {
            }

            // Assurez-vous que le livre a été ajouté
            boolean livreAjoute = livres.stream()
                    .anyMatch(l -> l.getTitre().equals("Test Titre") && l.getAuteur().getNom().equals("Test Auteur"));
            assertTrue(livreAjoute);
        });
    }
}
