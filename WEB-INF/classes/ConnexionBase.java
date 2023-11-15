package connexionBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionBase {
    private String utilisateur = "rota2";
    private String password = "sarobidy";
    private Connection connection = null;
    private int validation = 1;

    public ConnexionBase() {
        try {
            this.connexion();
            this.getConnection().setAutoCommit(false);
        } catch (Exception io) {
            io.printStackTrace();
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public int getValidation() {
        return this.validation;
    }

    public void setValidation(int n) {
        this.validation = n;
    }

    private void connexion() throws SQLException, Exception {
        if (this.getConnection() == null) {
            try {
                Class.forName("org.postgresql.Driver");
                Connection conn = DriverManager.getConnection("jdbc:postgresql://192.168.20.140:5432/cluster", this.getUtilisateur(), this.getPassword());
                this.setConnection(conn);
            } catch (SQLException io) {
                System.out.println("Erreur sur la connexion: " + io);
                throw io; // Rejeter l'exception après l'avoir traitée
            }
        }
    }

    private String getUtilisateur() {
        return this.utilisateur;
    }

    private String getPassword() {
        return this.password;
    }

    private void setConnection(Connection t) {
        this.connection = t;
    }

    public static ConnexionBase verifierConnexion(ConnexionBase con) {
        ConnexionBase c = con;
        if (c == null) {
            c = new ConnexionBase();
            c.setValidation(0);
        }
        System.out.println("Connexion: " + c);
        System.out.println("Connexion c ==> " + c.getConnection());
        return c;
    }

    public static ConnexionBase fermetureConnexion(ConnexionBase con) throws SQLException {
        ConnexionBase c = con;
        if (c.getValidation() == 0) {
            try {
                c.getConnection().commit();
                c.getConnection().close();
            } catch (SQLException e) {
                throw e; // Rejeter l'exception après l'avoir traitée
            } finally {
                c = null;
            }
        }
        return c;
    }
}
