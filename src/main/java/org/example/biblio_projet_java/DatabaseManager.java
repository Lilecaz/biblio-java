package org.example.biblio_projet_java;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;

import org.example.biblio_projet_java.Bibliotheque.Livre;

public class DatabaseManager {

    private static final Dotenv dotenv = Dotenv.load();
    private Connection connection;
    private String usertype;
    private String username;
    private boolean isUserLoggedIn = false;

    public DatabaseManager() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                String url = dotenv.get("DB_URL");
                String user = dotenv.get("DB_USER");
                String password = dotenv.get("DB_PASSWORD");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to " + url);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                throw e;
            }
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("db connection closed");
        }
    }

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
            throw new RuntimeException(e);
        }
    }

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

    public boolean loginUser(String username, String password) throws SQLException {
        String hashedPassword = hashPassword(password);
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("User type: " + getUserType(username));
                setUsername(username);
                setUserType(getUserType(username));
                setUserLoggedIn(true);
                return rs.next();
            }
        }
    }

    public void logout() {
        setUsername(null);
        setUserType(null);
    }

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

    public int ajouterAuteur(String nom, String prenom) throws SQLException {
        String query = "INSERT INTO auteurs (nom, prenom) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
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

}
