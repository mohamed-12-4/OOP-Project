public abstract class Account {
    protected String name, password;
    private int id;
    public Account(){
    }


    public Account(String name, String password) {
        setName(name);
        setPassword(password);
    }

    public String getName() {
        return name;
    }
    public int getId(){
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 9) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }


        boolean isThereDigit = false;
        for(int i = 0; i<password.length();i++){
            if(Character.isDigit(password.indexOf(i))) isThereDigit = true;
        }
        if (!isThereDigit) {
            throw new IllegalArgumentException("Password must contain at least one digit.");
        }


        boolean isThereAlphabetic = false;
        for(int i = 0; i<password.length();i++){
            if(Character.isAlphabetic(password.indexOf(i))) isThereDigit = true;
        }
        if (!isThereAlphabetic) {
            throw new IllegalArgumentException("Password must contain at least one alphabet.");
        }
        this.password = password;
    }

    @Override
    public String toString() {
        return "Account [Name=" + name + ", ID=" + id + "]";
    }
}

class AdminClass extends Account {
    public AdminClass(){

    }
}

//



