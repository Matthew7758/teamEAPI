package edu.wpi.cs3733.d20.teamE;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;

public class onCallBeds {

  public static void run(int xcoord, int ycoord, int windowWidth, int windowLength, @Nullable String cssPath, @Nullable String destNodeID, @Nullable String originNodeID) {
    @Nullable String[] args = new String[7];
    args[0]=Integer.toString(xcoord);
    args[1]=Integer.toString(ycoord);
    args[2]=Integer.toString(windowWidth);
    args[3]=Integer.toString(windowLength);
    args[4]=cssPath;
    args[5]=destNodeID;
    args[6]=originNodeID;
    App.launch(App.class,args);
  }
}
