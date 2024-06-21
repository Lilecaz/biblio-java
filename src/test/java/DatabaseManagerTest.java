import org.example.biblio_projet_java.DatabaseManager;
import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.example.biblio_projet_java.Bibliotheque.Livre.Auteur;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseManagerTest {

    @Mock
    private DatabaseManager dbManager;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    public void tearDown() throws SQLException {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    @Test
    public void testRegisterUser() throws SQLException {
        when(dbManager.registerUser("testUser", "testPassword")).thenReturn(true);
        boolean isRegistered = dbManager.registerUser("testUser", "testPassword");
        assertTrue(isRegistered, "User should be registered successfully.");
    }

    @Test
    public void testLoginUser() throws SQLException {
        when(dbManager.loginUser("test1", "test")).thenReturn(true);
        boolean isLoggedIn = dbManager.loginUser("test1", "test");
        assertTrue(isLoggedIn, "User should be able to log in successfully.");
        dbManager.logout();
    }

    @Test
    public void testAddAuthorAndRetrieveId() throws SQLException {
        when(dbManager.ajouterAuteur("john", "alex")).thenReturn(1);
        int authorId = dbManager.ajouterAuteur("john", "alex");
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

        when(dbManager.ajouterLivre(livre)).thenReturn(true);

        List<Livre> livres = new ArrayList<>();
        livres.add(livre);
        when(dbManager.getLivres()).thenReturn(livres);

        boolean isLivreAdded = dbManager.ajouterLivre(livre);
        assertTrue(isLivreAdded, "Book should be added successfully.");

        List<Livre> retrievedLivres = dbManager.getLivres();
        assertFalse(retrievedLivres.isEmpty(), "The list of books should not be empty.");
        Livre retrievedLivre = retrievedLivres.stream()
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
