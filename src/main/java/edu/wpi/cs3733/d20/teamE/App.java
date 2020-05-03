package edu.wpi.cs3733.d20.teamE;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class App extends Application {
  private static Scene scene;
  private static Scene calendarScene;
  private static Parent onCall;
  private static Parent calendar;

  @Getter
  @Setter
  private static Stage primaryStage;

  @Override
  public void init() {//int xcoord, int ycoord, int windowWidth, int windowLength, @Nullable String cssPath, @Nullable String destNodeID, @Nullable String originNodeID
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) {
    Parameters params = getParameters();
    List<String> list = params.getRaw();
    System.out.println(list.size());
    for(String each : list){
      System.out.println(each);
    }
    int xcoord = Integer.parseInt(list.get(0));
    int ycoord = Integer.parseInt(list.get(1));
    int windowWidth = Integer.parseInt(list.get(2));
    int windowLength = Integer.parseInt(list.get(3));
    String cssPath;
    String destNodeID;
    String originNodeID;
    try{
      cssPath = list.get(4);
    }
    catch(IndexOutOfBoundsException e) {
      cssPath="edu/wpi/cs3733/d20/teamE/stylesheets/Light.css";
    }
    try{
      destNodeID = list.get(5);
    }
    catch(IndexOutOfBoundsException e) {
      destNodeID="NONE";
    }
    try{
      originNodeID = list.get(6);
    }
    catch(IndexOutOfBoundsException e) {
      originNodeID="NONE";
    }

    App.primaryStage = primaryStage;
    //loadPartial
    try {//getClass().getResource("/edu/wpi/cs3733/d20/teamE/views/OnCallBed.fxml")
      onCall = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamE/views/OnCallBed.fxml"));
      calendar = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamE/views/ReservationCalendar.fxml"));
    } catch (NullPointerException | IOException e) {
      System.out.println("Error in loadPartial()");
      e.printStackTrace();
    }
    //loadScenes
    try {
      scene = new Scene(FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamE/views/OnCallBed.fxml")));
      calendarScene = new Scene(FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/d20/teamE/views/ReservationCalendar.fxml")));
    } catch (NullPointerException | IOException e) {
      System.out.println("Error in loadScenes()");
      e.printStackTrace();
    }

    primaryStage.setTitle("On Call Beds API");

    primaryStage.setScene(scene);
    scene.setRoot(onCall);
    Scene[] sceneList = new Scene[]{scene, calendarScene};
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
    primaryStage.setX((double) xcoord);
    primaryStage.setY((double) ycoord);
    primaryStage.setWidth((double) windowWidth);
    primaryStage.setHeight((double) windowLength);
    primaryStage.setMaximized(true);
    primaryStage.show();
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}
