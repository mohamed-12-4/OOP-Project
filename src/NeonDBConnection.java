import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class NeonDBConnection {
    // Replace the placeholders with your actual Neon DB credentials.
    // The connection string includes host, port, database name, username, and password.
    private static final String CONNECTION_URL =
            "jdbc:postgresql://ep-billowing-shadow-a2ua19r1-pooler.eu-central-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_QwgRrG5zhq3a&sslmode=require";

    public static Connection getConnection() throws SQLException {
        try {
            // Ensure the PostgreSQL JDBC Driver is available.
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found.");
            e.printStackTrace();
        }
        return DriverManager.getConnection(CONNECTION_URL);
    }
}