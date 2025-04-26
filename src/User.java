import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class User extends Account {
    private UUID id;
    private String name, email, address, password;
    private String phoneNum;



    public User(){
    }
    public User(UUID id) {
        this.id = id;
        try {
            Connection conn = NeonDBConnection.getConnection();
            String query = "SELECT * FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                this.id = UUID.fromString(rs.getString("id"));
                this.name = rs.getString("name");
                this.email = rs.getString("email");
                this.address = rs.getString("address");
                this.password = rs.getString("password");
                this.phoneNum = rs.getString("phone");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteById(UUID id) {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = NeonDBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     public void progressReport() {
        String fileName = this.id + "Report.txt"; 
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Progress Report for User: " + getEmail());
            writer.println("User ID: " + this.id);
            writer.println("Total Transcation: " + Transaction.getAllTransactions().size());
            writer.println("\nUser Transaction:");
            StringBuilder income = new StringBuilder("Income: \n");
            StringBuilder expense = new StringBuilder("Income: \n");

            
            for(Transaction trans: Transaction.getAllTransactions()){
                if (trans.getType().toLowerCase().equals("income")) {
                    income.append(trans + "\n");
                } else{
                    expense.append(trans + "\n");

                }
                
            }
    
            writer.println(income);
            writer.println(expense);
            writer.println("\nThank you for using the KU Budget!");
            System.out.println("Progress report saved as: " + fileName); 
        } catch (IOException e) {
            System.err.println("Error generating progress report: " + e.getMessage());
        }
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        for(int i=0;i<name.length();i++){
            if((Character.isDigit(name.indexOf(i)))) {
                throw new IllegalArgumentException("name cannot contain a digit.");
            }
        }
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        if(password.length()<10) throw new IllegalArgumentException("Password is too short, needs to be at least 10 charcters ");

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit.");
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one letter.");

        }
        this.password = password;
    }


    public String getPhoneNum() {
        return phoneNum;
    }

    public static ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        try {
            Connection conn = NeonDBConnection.getConnection();
            String query = "SELECT * FROM users ";
            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User(UUID.fromString(rs.getString("id")));
                users.add(user);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static int countUsers() {
        int count = 0;
        try {
            Connection conn = NeonDBConnection.getConnection();
            String query = "SELECT COUNT(*) FROM users ";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                count = rs.getInt(1);

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return count;

    }




    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }



    public void setBudgetStatus(String approved) {
    }

    //get uuid
    public UUID getUuid() {
        return id;
    }

    public void updateInDB() {
        String query = "UPDATE users SET name = ?, email = ?, address = ?, phone = ? WHERE id = ?";
        try (Connection conn = NeonDBConnection.getConnection()){;
             PreparedStatement ps = conn.prepareStatement(query);
             ps.setString(1, name);
             ps.setString(2, email);
             ps.setString(3, address);
             ps.setString(4, phoneNum);
             ps.setObject(5, id);

             ps.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}