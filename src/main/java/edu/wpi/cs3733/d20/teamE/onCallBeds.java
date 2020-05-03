package edu.wpi.cs3733.d20.teamE;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class onCallBeds {
  private static Stage app;
  private static Scene application;
  private static Scene calendarScene;
  private static Parent calendar;
  private static Parent onCall;

  public static void run(int xcoord, int ycoord, int windowWidth, int windowLength, @Nullable String cssPath, @Nullable String destNodeID, @Nullable String originNodeID) {
    app = new Stage();
    app.setTitle("On Call Beds API");
    loadPartial();
    loadScenes();
    app.setScene(application);
    application.setRoot(onCall);
    Scene[] sceneList = new Scene[]{application, calendarScene};
    Scene[] sceneListCopy = sceneList;
    int sceneListLength = sceneList.length;
    //Add stylesheet
    for(int i = 0; i < sceneListLength; i++) {
      Scene s = sceneListCopy[i];
      try {
        if (cssPath != null) {
          s.getStylesheets().add(cssPath);
        }
      } catch (Exception e) {
        System.out.println("Error adding CSS");
        e.printStackTrace();
      }
    }
  }

  private static void loadScenes() {
    try {
      application = new Scene((Parent)FXMLLoader.load(onCallBeds.class.getResource("views/OnCallBed.fxml")));
      calendarScene = new Scene((Parent)FXMLLoader.load(onCallBeds.class.getResource("views/ReservationCalendar.fxml")));
    } catch (IOException e) {
      System.out.println("Error in loadScenes()");
      e.printStackTrace();
    }
  }

  private static void loadPartial() {
    try {
      calendar = (Parent) FXMLLoader.load(onCallBeds.class.getResource("views/OnCallBed.fxml"));
      onCall = (Parent)FXMLLoader.load(onCallBeds.class.getResource("views/ReservationCalendar.fxml"));
    } catch (IOException e) {
      System.out.println("Error in loadPartial()");
      e.printStackTrace();
    }
  }
}
