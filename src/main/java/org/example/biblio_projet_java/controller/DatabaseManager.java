package org.example.biblio_projet_java.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.example.biblio_projet_java.Bibliotheque.Livre;
import org.example.biblio_projet_java.utils.AlertUtils;

import javafx.collections.ObservableList;

/**
 * Cette classe gère la connexion à la base de données et les opérations liées à
 * celle-ci.
 */
public class DatabaseManager {

    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try (InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                AlertUtils.showError("Erreur", "Fichier de configuration introuvable.");
            } else {
                // load a properties file from class path
                prop.load(input);

                // get the property value
                URL = prop.getProperty("database.url");
                USER = prop.getProperty("database.user");
                PASSWORD = prop.getProperty("database.password");
            }

        } catch (IOException ex) {
        }
    }
    private Connection connection;
    private static String usertype;
    private String username;
    private boolean isUserLoggedIn = false;

    public DatabaseManager() throws SQLException {
        connect();
    }

    /**
     * Connecte à la base de données si la connexion est fermée ou nulle.
     * Affiche l'URL de la base de données.
     *
     * @throws SQLException si une erreur d'accès à la base de données se produit
     */
    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {

                connection = DriverManager.getConnection(URL, USER, PASSWORD);

            } catch (Exception e) {
            }
        }
    }

    /**
     * Ferme la connexion à la base de données.
     *
     * @throws SQLException si une erreur survient lors de la fermeture de la
     *                      connexion.
     */
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Methode pour hasher le mot de passe
     *
     * @param password le mot de passe à hasher
     * @return le mot de passe hashé
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Enregistre un utilisateur dans la base de données.
     *
     * @param username Le nom d'utilisateur de l'utilisateur à enregistrer.
     * @param password Le mot de passe de l'utilisateur à enregistrer.
     * @return true si l'utilisateur a été enregistré avec succès, sinon false.
     * @throws SQLException Si une erreur se produit lors de l'exécution de la
     *                      requête SQL.
     */
    public boolean registerUser(String username, String password) throws SQLException {
        String hashedPassword = hashPassword(password);
        String query = "INSERT INTO users (username, usertype, password, register_date) VALUES (?, 'user', ?, CURRENT_DATE())";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean resetPassword(String user2, String password2) {
        String hashedPassword = hashPassword(password2);
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, user2);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Authentifie un utilisateur en vérifiant les informations de connexion
     * fournies.
     *
     * @param username le nom d'utilisateur de l'utilisateur
     * @param password le mot de passe de l'utilisateur
     * @return true si l'utilisateur est authentifié avec succès, sinon false
     * @throws SQLException si une erreur se produit lors de l'exécution de la
     *                      requête SQL
     */
    public boolean loginUser(String username, String password) throws SQLException {
        String hashedPassword = hashPassword(password);
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            try (ResultSet rs = stmt.executeQuery()) {
                setUsername(username);
                setUserType(getUserType(username));
                setUserLoggedIn(true);
                return rs.next();
            }
        }
    }

    /**
     * Déconnecte l'utilisateur en réinitialisant le nom d'utilisateur et le type
     * d'utilisateur.
     */
    public void logout() {
        setUsername(null);
        setUserType(null);
    }

    /**
     * Récupère le type d'utilisateur pour un nom d'utilisateur donné.
     *
     * @param username le nom d'utilisateur pour lequel récupérer le type
     *                 d'utilisateur
     * @return le type d'utilisateur correspondant au nom d'utilisateur donné, ou
     *         null si aucun résultat n'est trouvé
     * @throws SQLException si une erreur se produit lors de l'exécution de la
     *                      requête SQL
     */
    public String getUserType(String username) throws SQLException {
        String query = "SELECT usertype FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("usertype");
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Récupère l'identifiant d'un auteur à partir de son nom et prénom.
     * 
     * @param nom    le nom de l'auteur
     * @param prenom le prénom de l'auteur
     * @return l'identifiant de l'auteur, ou -1 si l'auteur n'existe pas
     * @throws SQLException si une erreur SQL se produit lors de l'exécution de la
     *                      requête
     */
    public int getAuteurId(String nom, String prenom) throws SQLException {
        String query = "SELECT id FROM auteurs WHERE nom = ? AND prenom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1; // Indique que l'auteur n'existe pas
    }

    /**
     * Ajoute un auteur à la base de données.
     * 
     * @param nom    le nom de l'auteur
     * @param prenom le prénom de l'auteur
     * @return l'ID généré de l'auteur ajouté, ou -1 en cas d'échec de l'insertion
     * @throws SQLException si une erreur SQL se produit lors de l'exécution de la
     *                      requête
     */
    public int ajouterAuteur(String nom, String prenom) throws SQLException {
        String query = "INSERT INTO auteurs (nom, prenom) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Récupère l'ID généré
                }
            }
        }
        return -1; // Échec de l'insertion
    }

    /**
     * Ajoute un livre à la base de données.
     * 
     * @param livre Le livre à ajouter.
     * @return true si le livre a été ajouté avec succès, sinon false.
     * @throws SQLException Si une erreur se produit lors de l'ajout du livre.
     */
    public boolean ajouterLivre(Livre livre) throws SQLException {
        // Vérifiez si l'auteur existe déjà
        String nom = livre.getAuteur().getNom();
        String prenom = livre.getAuteur().getPrenom();
        int auteurId = getAuteurId(nom, prenom);

        // Si l'auteur n'existe pas, ajoutez-le
        if (auteurId == -1) {
            auteurId = ajouterAuteur(nom, prenom);
            if (auteurId == -1) {
                throw new SQLException("Erreur lors de l'ajout de l'auteur.");
            }
        }

        String query = "INSERT INTO livres (titre, auteur_id, presentation, parution, colonne, rangee, emprunt, resume, lien) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, livre.getTitre());
            stmt.setInt(2, auteurId);
            stmt.setString(3, livre.getPresentation());
            stmt.setInt(4, livre.getParution());
            stmt.setShort(5, livre.getColonne());
            stmt.setShort(6, livre.getRangee());
            stmt.setBoolean(7, livre.isEmprunt());
            stmt.setString(8, livre.getResume());
            stmt.setString(9, livre.getLien());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Récupère la liste des livres de la base de données.
     * 
     * @return Une liste d'objets Livre contenant les informations des livres.
     * @throws SQLException Si une erreur se produit lors de l'exécution de la
     *                      requête SQL.
     */
    public List<Livre> getLivres() throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String query = "SELECT l.titre, l.presentation, l.parution, l.colonne, l.rangee, l.emprunt, l.resume, l.lien, a.nom, a.prenom "
                +
                "FROM livres l JOIN auteurs a ON l.auteur_id = a.id";

        try (PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Livre livre = new Livre();
                livre.setTitre(rs.getString("titre"));
                livre.setPresentation(rs.getString("presentation"));
                livre.setParution(rs.getInt("parution"));
                livre.setColonne(rs.getShort("colonne"));
                livre.setRangee(rs.getShort("rangee"));
                livre.setEmprunt(rs.getBoolean("emprunt"));
                livre.setResume(rs.getString("resume"));
                livre.setLien(rs.getString("lien"));

                Livre.Auteur auteur = new Livre.Auteur();
                auteur.setNom(rs.getString("nom"));
                auteur.setPrenom(rs.getString("prenom"));
                livre.setAuteur(auteur);

                livres.add(livre);
            }
        }

        return livres;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserType(String usertype) {
        this.usertype = usertype;
    }

    public String getUserType() {
        return usertype;
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        isUserLoggedIn = userLoggedIn;
    }

    public boolean isUserConnected() {
        return username != null;
    }

    public static boolean isAdmin() {
        return "admin".equals(usertype);
    }

    /**
     * Synchronise les données des livres entre une liste observable d'items et la
     * base de données.
     * Cette méthode vérifie si l'utilisateur est administrateur avant de procéder à
     * la synchronisation.
     * Si l'utilisateur est administrateur, elle compare les livres dans la liste
     * fournie avec ceux
     * déjà présents dans la base de données. Les nouveaux livres trouvés sont
     * ajoutés à la base de données.
     * 
     * @param items       La liste observable d'objets de type {@link Livre}
     *                    contenant les livres à synchroniser.
     * @param isUserAdmin Un booléen indiquant si l'utilisateur est administrateur
     *                    ou non.
     * @return Retourne true si la synchronisation a réussi (utilisateur
     *         administrateur), sinon false.
     * @throws SQLException Lancée en cas d'erreur lors de l'accès à la base de
     *                      données.
     */
    public boolean syncData(ObservableList<Livre> items, boolean isUserAdmin) throws SQLException {
        if (!isUserAdmin) {
            return false;
        }

        try {
            List<Livre> livresInDatabase = getLivres();
            List<Livre> newLivres = filterNewLivres(items, livresInDatabase);

            for (Livre livre : newLivres) {
                ajouterLivre(livre);
                System.out.println("Nouveau livre: " + livre.getTitre());
            }
        } catch (SQLException e) {
            AlertUtils.showError("Erreur", "Impossible de récupérer les livres de la base de données.");
        }

        return true;
    }

    private List<Livre> filterNewLivres(ObservableList<Livre> items, List<Livre> livresInDatabase) {
        return items.stream()
                .filter(item -> livresInDatabase.stream().noneMatch(dbItem -> isSameLivre(item, dbItem)))
                .collect(Collectors.toList());
    }

    private boolean isSameLivre(Livre livre1, Livre livre2) {
        return livre1.getTitre().equals(livre2.getTitre())
                && livre1.getAuteur().getNom().equals(livre2.getAuteur().getNom())
                && livre1.getAuteur().getPrenom().equals(livre2.getAuteur().getPrenom());
    }

}
