import org.example.biblio_projet_java.DatabaseManager;
import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.example.biblio_projet_java.Bibliotheque.Livre.Auteur;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseManagerTest {

    private DatabaseManager dbManager;

    @BeforeAll
    public void setup() throws SQLException {
        dbManager = new DatabaseManager();
    }

    @AfterAll
    public void tearDown() throws SQLException {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    @Test
    public void testRegisterUser() throws SQLException {
        System.out.println(dbManager);
        boolean isRegistered = dbManager.registerUser("testUser", "testPassword");
        assertTrue(isRegistered, "User should be registered successfully.");
    }

    @Test
    public void testLoginUser() throws SQLException {
        boolean isLoggedIn = this.dbManager.loginUser("test1", "test");
        assertTrue(isLoggedIn, "User should be able to log in successfully.");
        dbManager.logout();
    }

    @Test
    public void testAddAuthorAndRetrieveId() throws SQLException {
        int authorId = this.dbManager.ajouterAuteur("john", "alex");
        assertTrue(authorId > 0, "Author should be added successfully and a valid ID should be returned.");
    }

    @Test
    public void testAddAndRetrieveBook() throws SQLException {
        Auteur auteur = new Auteur();
        auteur.setNom("TestNom");
        auteur.setPrenom("TestPrenom");

        Livre livre = new Livre();
        livre.setTitre("TestTitre");
        livre.setAuteur(auteur);
        livre.setPresentation("TestPresentation");
        livre.setParution(2023);
        livre.setColonne((short) 1);
        livre.setRangee((short) 1);
        livre.setEmprunt(false);
        livre.setResume("TestResume");
        livre.setLien("TestLien");

        boolean isLivreAdded = this.dbManager.ajouterLivre(livre);
        assertTrue(isLivreAdded, "Book should be added successfully.");

        List<Livre> livres = dbManager.getLivres();
        assertFalse(livres.isEmpty(), "The list of books should not be empty.");
        Livre retrievedLivre = livres.stream()
                .filter(l -> "TestTitre".equals(l.getTitre()))
                .findFirst()
                .orElse(null);
        assertNotNull(retrievedLivre, "The book should be retrieved successfully.");
        assertEquals("TestTitre", retrievedLivre.getTitre(), "The title of the retrieved book should match.");
        assertEquals("TestNom", retrievedLivre.getAuteur().getNom(),
                "The author name of the retrieved book should match.");
        assertEquals("TestPrenom", retrievedLivre.getAuteur().getPrenom(),
                "The author surname of the retrieved book should match.");
    }
}
