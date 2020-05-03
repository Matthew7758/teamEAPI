package edu.wpi.cs3733.d20.teamE;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class App extends Application {
  @Getter
  @Setter
  private static Stage primaryStage;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) {
    App.primaryStage = primaryStage;
    try {
     Parent root =
          FXMLLoader.load(
              getClass()
                  .getResource(
                      "/edu/wpi/cs3733/d20/teamE/views/OnCallBed.fxml"));
      Scene scene = new Scene(root);
      primaryStage.setScene(scene);
      primaryStage.setMaximized(true);
      primaryStage.show();
    } catch (IOException e) {
      e.printStackTrace();
      Platform.exit();
    }
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}
