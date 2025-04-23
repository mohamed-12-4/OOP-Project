import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.sql.*;

public class User extends Account {
    private UUID id;
    private String name, email, address, password;
    private int phoneNum;



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
                this.phoneNum = rs.getInt("phone");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
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


    public int getPhoneNum() {
        return phoneNum;
    }


    public void setPhoneNum(int phoneNum) {
        this.phoneNum = phoneNum;
    }



    public void setBudgetStatus(String approved) {
    }

    //get uuid
    public UUID getUuid() {
        return id;
    }
}