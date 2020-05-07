package edu.wpi.cs3733.d20.teamE;

import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

public class OnCallBedsFX extends Application {

  public void start(Stage primaryStage) throws Exception {
    DBEAPI database = new DBEAPI("admin", "password");
    Connection conn = database.connectDB("admin", "password");

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        conn.close();
      } catch (SQLException throwables) {
        throwables.printStackTrace();
      }
    }));
    onCallBeds.run(0, 0, 1280, 720, "edu/wpi/cs3733/d20/teamE/stylesheetsEAPI/defaultEAPI.css", null, null);
  }
}
