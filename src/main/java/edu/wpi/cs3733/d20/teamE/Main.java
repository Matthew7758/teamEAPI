package edu.wpi.cs3733.d20.teamE;
import java.sql.Connection;

public class Main {
  public static void main(String[] args) {
    DB database = new DB("admin", "password");
    // BELOW IS AN EXAMPLE OF HOW TO INTERFACE WITH THE BACKEND.
    Connection conn = database.connectDB("admin", "password");
    //App.launch(App.class, args);
    onCallBeds.run(0,0,1280,720,"edu/wpi/cs3733/d20/teamE/stylesheets/Light.css",null,null);
  }
}
