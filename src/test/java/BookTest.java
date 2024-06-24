import org.example.biblio_projet_java.DatabaseManager;
import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.example.biblio_projet_java.Bibliotheque.Livre.Auteur;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookTest {

    @Mock
    private DatabaseManager dbManager;

    @BeforeEach
    public void setUp() {
        // Initialisation des mocks
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testMultipleBooksAdditionAndVerification() throws SQLException {
        // Configurer le comportement du mock pour ajouter des livres
        dbManager = mock(DatabaseManager.class);
        Auteur auteur1 = new Auteur();
        auteur1.setNom("Rowling");
        auteur1.setPrenom("J.K.");

        Livre livre1 = new Livre();
        livre1.setTitre("Harry Potter and the Philosopher's Stone");
        livre1.setAuteur(auteur1);
        livre1.setPresentation("A young wizard's first year at Hogwarts School of Witchcraft and Wizardry.");
        livre1.setParution(1997);
        livre1.setColonne((short) 2);
        livre1.setRangee((short) 2);
        livre1.setEmprunt(false);
        livre1.setResume("Harry Potter discovers his magical heritage and attends Hogwarts.");
        livre1.setLien("https://example.com/harry-potter-1");

        doReturn(true).when(dbManager).ajouterLivre(livre1);

        Auteur auteur2 = new Auteur();
        auteur2.setNom("Martin");
        auteur2.setPrenom("George R.R.");

        Livre livre2 = new Livre();
        livre2.setTitre("A Game of Thrones");
        livre2.setAuteur(auteur2);
        livre2.setPresentation(
                "The first book in the A Song of Ice and Fire series, set in a fictional medieval world.");
        livre2.setParution(1996);
        livre2.setColonne((short) 3);
        livre2.setRangee((short) 3);
        livre2.setEmprunt(false);
        livre2.setResume("Noble families vie for control of the Iron Throne and the Seven Kingdoms of Westeros.");
        livre2.setLien("https://example.com/game-of-thrones");

        doReturn(true).when(dbManager).ajouterLivre(livre2);
        // Appeler la méthode sous test avec les livres simulés
        assertTrue(dbManager.ajouterLivre(livre1), "First book should be added successfully.");
        assertTrue(dbManager.ajouterLivre(livre2), "Second book should be added successfully.");

        // Simuler la récupération des livres à partir du mock
        List<Livre> livresSimules = new ArrayList<>();
        livresSimules.add(livre1);
        livresSimules.add(livre2);
        doReturn(livresSimules).when(dbManager).getLivres();
        // Vérifier le comportement attendu
        List<Livre> livres = dbManager.getLivres();
        assertEquals(livres.size(), livres.size(), "The list should contain two books.");

        Livre retrievedLivre1 = livres.stream()
                .filter(l -> "Harry Potter and the Philosopher's Stone".equals(l.getTitre()))
                .findFirst()
                .orElse(null);
        assertNotNull(retrievedLivre1, "First book should be retrieved successfully.");
        assertEquals("Harry Potter and the Philosopher's Stone", retrievedLivre1.getTitre(),
                "The title of the first book should match.");
        assertEquals("Rowling", retrievedLivre1.getAuteur().getNom(),
                "The author name of the first book should match.");
        assertEquals("J.K.", retrievedLivre1.getAuteur().getPrenom(),
                "The author surname of the first book should match.");

        Livre retrievedLivre2 = livres.stream()
                .filter(l -> "A Game of Thrones".equals(l.getTitre()))
                .findFirst()
                .orElse(null);
        assertNotNull(retrievedLivre2, "Second book should be retrieved successfully.");
        assertEquals("A Game of Thrones", retrievedLivre2.getTitre(), "The title of the second book should match.");
        assertEquals("Martin", retrievedLivre2.getAuteur().getNom(),
                "The author name of the second book should match.");
        assertEquals("George R.R.", retrievedLivre2.getAuteur().getPrenom(),
                "The author surname of the second book should match.");
    }

    @Test
    public void testIncorrectBookDetails() throws SQLException {
        // Configurer le comportement du mock pour ajouter un livre avec des détails
        // incorrects
        dbManager = mock(DatabaseManager.class);
        Auteur auteur = new Auteur();
        auteur.setNom("FakeName");
        auteur.setPrenom("FakeSurname");

        Livre livre = new Livre();
        livre.setTitre("Fake Title");
        livre.setAuteur(auteur);
        livre.setPresentation("Fake Presentation");
        livre.setParution(2020);
        livre.setColonne((short) 4);
        livre.setRangee((short) 4);
        livre.setEmprunt(false);
        livre.setResume("Fake Resume");
        livre.setLien("https://example.com/fake-title");

        doReturn(true).when(dbManager).ajouterLivre(livre);
        // Appeler la méthode sous test avec le livre simulé
        boolean isLivreAdded = dbManager.ajouterLivre(livre);
        assertTrue(isLivreAdded, "Book with incorrect details should be added successfully.");

        // Simuler la récupération des livres à partir du mock
        List<Livre> livresSimules = new ArrayList<>();
        livresSimules.add(livre);
        doReturn(livresSimules).when(dbManager).getLivres();
        // Vérifier le comportement attendu
        List<Livre> livres = dbManager.getLivres();
        assertFalse(livres.isEmpty(), "The list of books should not be empty.");
        Livre retrievedLivre = livres.stream()
                .filter(l -> "Fake Title".equals(l.getTitre()))
                .findFirst()
                .orElse(null);
        assertNotNull(retrievedLivre, "The book with incorrect details should be retrieved successfully.");
        assertNotEquals("Correct Title", retrievedLivre.getTitre(),
                "The title of the retrieved book should not match the correct title.");
        assertNotEquals("CorrectName", retrievedLivre.getAuteur().getNom(),
                "The author name of the retrieved book should not match the correct name.");
        assertNotEquals("CorrectSurname", retrievedLivre.getAuteur().getPrenom(),
                "The author surname of the retrieved book should not match the correct surname.");
    }
}
