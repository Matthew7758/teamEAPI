package edu.wpi.cs3733.d20.teamE;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class onCallBeds {
  @Getter
  @Setter
  private static Scene scene;
  @Getter
  @Setter
  private static Scene calendarScene;
  @Getter
  @Setter
  private static Parent onCall;
  @Getter
  @Setter
  private static Parent calendar;
  @Getter
  @Setter
  private static Stage app;

  public static void run(int xcoord, int ycoord, int windowWidth, int windowLength, @Nullable String cssPath, @Nullable String destNodeID, @Nullable String originNodeID) {
    app = new Stage();
    //loadPartial
    try {
      onCall = FXMLLoader.load(onCallBeds.class.getResource("views/OnCallBed.fxml"));
      calendar = FXMLLoader.load(onCallBeds.class.getResource("views/ReservationCalendar.fxml"));
    } catch (NullPointerException | IOException e) {
      System.out.println("Error in loadPartial()");
      e.printStackTrace();
    }
    //loadScenes
    try {
      scene = new Scene(FXMLLoader.load(onCallBeds.class.getResource("/edu/wpi/cs3733/d20/teamE/views/OnCallBed.fxml")));
      calendarScene = new Scene(FXMLLoader.load(onCallBeds.class.getResource("/edu/wpi/cs3733/d20/teamE/views/ReservationCalendar.fxml")));
    } catch (NullPointerException | IOException e) {
      System.out.println("Error in loadScenes()");
      e.printStackTrace();
    }

    app.setTitle("On Call Beds API");

    app.setScene(scene);
    scene.setRoot(onCall);
    Scene[] sceneList = new Scene[]{scene, calendarScene};
    Scene[] sceneListCopy = sceneList;
    int sceneListLength = sceneList.length;
    //Add stylesheet
    for (int i = 0; i < sceneListLength; i++) {
      Scene s = sceneListCopy[i];
      try {
        if (cssPath != null) {
          s.getStylesheets().add(cssPath);
        } else {
          s.getStylesheets().add(String.valueOf(onCallBeds.class.getResource("stylesheets/default.css")));
        }
      } catch (Exception e) {
        System.out.println("Error adding CSS");
        e.printStackTrace();
      }

    }

    app.setX((double) xcoord);
    app.setY((double) ycoord);
    app.setWidth((double) windowWidth);
    app.setHeight((double) windowLength);
    app.setMaximized(true);
    app.show();
  }
}
