import java.sql.*;
import java.util.UUID;

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
    public static void main(String[] args) {
        double total = 0;
        String query = "SELECT SUM(amount) FROM transactions WHERE type='expense' AND user_id = ?" ;
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {


            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                total += rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(total);
    }
}