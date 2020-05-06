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
  private static Scene sceneEAPI;
  @Getter
  @Setter
  private static Scene calendarSceneEAPI;
  @Getter
  @Setter
  private static Parent onCallEAPI;
  @Getter
  @Setter
  private static Parent calendarEAPI;
  @Getter
  @Setter
  private static Stage appEAPI;

  public static void run(int xcoord, int ycoord, int windowWidth, int windowLength, @Nullable String cssPath, @Nullable String destNodeID, @Nullable String originNodeID) {
    appEAPI = new Stage();
    //loadPartial
    try {
      onCallEAPI = FXMLLoader.load(onCallBeds.class.getResource("views/OnCallBedEAPI.fxml"));
      calendarEAPI = FXMLLoader.load(onCallBeds.class.getResource("views/ReservationCalendarEAPI.fxml"));
    } catch (NullPointerException | IOException e) {
      System.out.println("Error in loadPartial()");
      e.printStackTrace();
    }
    //loadScenes
    try {
      sceneEAPI = new Scene(FXMLLoader.load(onCallBeds.class.getResource("/edu/wpi/cs3733/d20/teamE/views/OnCallBedEAPI.fxml")));
      calendarSceneEAPI = new Scene(FXMLLoader.load(onCallBeds.class.getResource("/edu/wpi/cs3733/d20/teamE/views/ReservationCalendarEAPI.fxml")));
    } catch (NullPointerException | IOException e) {
      System.out.println("Error in loadScenes()");
      e.printStackTrace();
    }

    appEAPI.setTitle("On Call Beds API");

    appEAPI.setScene(sceneEAPI);
    sceneEAPI.setRoot(onCallEAPI);
    Scene[] sceneList = new Scene[]{sceneEAPI, calendarSceneEAPI};
    Scene[] sceneListCopy = sceneList;
    int sceneListLength = sceneList.length;
    //Add stylesheet
    for (int i = 0; i < sceneListLength; i++) {
      Scene s = sceneListCopy[i];
      try {
        if (cssPath != null) {
          s.getStylesheets().add(cssPath);
        } else {
          s.getStylesheets().add("/edu/wpi/cs3733/d20/teamE/stylesheetsEAPI/defaultEAPI.css");
        }
      } catch (Exception e) {
        System.out.println("Error adding CSS");
        e.printStackTrace();
      }

    }

    appEAPI.setX((double) xcoord);
    appEAPI.setY((double) ycoord);
    appEAPI.setWidth((double) windowWidth);
    appEAPI.setHeight((double) windowLength);
    appEAPI.setMaximized(true);
    appEAPI.show();
  }
}
