package edu.wpi.cs3733.d20.teamE.views;

import com.jfoenix.controls.*;
import edu.wpi.cs3733.d20.teamE.DBEAPI;
import edu.wpi.cs3733.d20.teamE.onCallBeds;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static java.time.LocalTime.parse;

public class OnCallBedControllerEAPI {
  // FXML buttons and fields
  public JFXDatePicker datePicker;
  public JFXComboBox<String> buildingSelect=new JFXComboBox<>();
  public JFXComboBox<String> resSelect=new JFXComboBox<>();
  public JFXTimePicker timePickerStart;
  public JFXTimePicker timePickerEnd;
  public JFXButton checkBtn;
  public JFXButton homeBtn;
  // Images for the bed icons
  public JFXButton bed1;
  public JFXButton bed2;
  public JFXButton bed3;
  public JFXButton bed4;
  public JFXButton bed5;
  public JFXButton bed6;
  public JFXButton bed7;
  public AnchorPane anchorPane;
  public GridPane roomGridPane;
  public JFXButton refRoom1;
  public JFXButton reflRoom2;
  public JFXButton reflRoom3;
  public GridPane confGridPane;
  public JFXButton confRoom1;
  public JFXButton confRoom2;
  public JFXButton confRoom3;
  public JFXButton confRoom4;
  public GridPane compGridPane;
  public JFXButton compRoom1;
  public JFXButton compRoom4;
  public JFXButton compRoom2;
  public JFXButton compRoom5;
  public JFXButton compRoom3;
  public JFXButton compRoom6;
  public GridPane bedGridPane;

  DBEAPI database = new DBEAPI("admin", "password");
  String delim = ",";
  // list to hold reservation data
  ObservableList<OnCallBedDataEAPI> reservations = FXCollections.observableArrayList();

  // variables to hold data to send request
  String date;
  String timeStart;
  String timeEnd;
  String building;
  String resNum;

  // holds the bed buttons
  ArrayList<JFXButton> beds = new ArrayList<>();
  ArrayList<JFXButton> refRooms = new ArrayList<>();
  ArrayList<JFXButton> compRooms = new ArrayList<>();
  ArrayList<JFXButton> confRooms = new ArrayList<>();

  // bed names to be assigned to reservation type
  ObservableList<String> bedNames =
      FXCollections.observableArrayList(
          "Bed 1", "Bed 2", "Bed 3", "Bed 4", "Bed 5", "Bed 6", "Bed 7");

  // Alert Box
  JFXAlert alert = new JFXAlert();
  // Buttons and layout for confirming a reservation
  JFXButton confirm = new JFXButton("Yes");
  JFXButton deny = new JFXButton("No");
  JFXDialogLayout layout = new JFXDialogLayout();
  JFXTextField name = new JFXTextField();
  JFXDialogLayout enterName = new JFXDialogLayout();
  JFXButton makeRes = new JFXButton("Confirm");
  // populates the buildings drop down
  ObservableList<String> buildings = FXCollections.observableArrayList("Faulkner", "Main Campus");
  ObservableList<String> resTypes =
          FXCollections.observableArrayList(
                  "On Call Beds", "Reflection Rooms", "Computer Rooms", "Conference Rooms");
  int getLastClicked;
  // Sets all of the initial values of the screen
  public void initialize() {
    getLastClicked=0;
    buildingSelect.setItems(buildings);
    resSelect.setItems(resTypes);
    // gets the existing reservations from database
    reservations.addAll(getReserve());
    makeLists();
    // sets dialogue box for the confirmation
    enterName.setHeading(new Label("Please enter your name"));
    makeRes.setStyle("-fx-background-color: WHITE");
    makeRes.setRipplerFill(Color.BLUE);
    VBox conf = new VBox(name, makeRes);
    conf.setAlignment(Pos.CENTER);
    conf.setSpacing(10);
    enterName.setBody(conf);
    alert.setSize(250, 150);
  }

  // got this code from Matthew to be able to look at the database in this scene
  private OnCallBedDataEAPI buildReservation(String[] params) {
    String requestID = params[0];
    String dateReserved = params[1];
    String timeReservedStart = params[2];
    String timeReservedEnd = params[3];
    String building = params[4];
    String reservationType = params[5];
    String reservedFor = params[6];
    String isReserved = params[7];
    return new OnCallBedDataEAPI(
        requestID,
        dateReserved,
        timeReservedStart,
        timeReservedEnd,
        building,
        reservationType,
        reservedFor,
        isReserved);
  }

  // also got this from Matthew to get request database
  public ObservableList<OnCallBedDataEAPI> getReserve() {
    ObservableList<OnCallBedDataEAPI> theReqs = FXCollections.observableArrayList();
    // Query database and build line by line
    try {
      Connection connection = database.connectDB("admin", "password");
      Statement stmt = connection.createStatement();
      String sql = "SELECT * FROM ONCALLBEDS";
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        // requestID,date,typeSpill,location,details,deptAssigned,completed
        String requestID = rs.getString("requestID");
        String dateReserved = rs.getString("dateReserved");
        String timeReservedStart = rs.getString("timeReservedStart");
        String timeReservedEnd = rs.getString("timeReservedEnd");
        String building = rs.getString("building");
        String reservationType = rs.getString("reservationType");
        String reservedFor = rs.getString("reservedFor");
        String isReserved = rs.getString("isReserved");
        String finalString =
            requestID
                + ","
                + dateReserved
                + ","
                + timeReservedStart
                + ","
                + timeReservedEnd
                + ","
                + building
                + ","
                + reservationType
                + ","
                + reservedFor
                + ","
                + isReserved;
        String[] lineInfo = finalString.split(delim);
        OnCallBedDataEAPI foundReq = buildReservation(lineInfo);
        theReqs.add(foundReq);
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return theReqs;
  }

  // gets building selection
  public void selBuilding(ActionEvent event) {
    resetBtns();
    building = buildingSelect.getSelectionModel().getSelectedItem();
    if (building.equals("Faulkner")) {
      //resSelect.setDisable(false);
      resSelect.setItems(FXCollections.observableArrayList("On Call Beds"));
    } else {
      //resSelect.setDisable(false);
      resSelect.setItems(
          FXCollections.observableArrayList(
              "Reflection Rooms", "Computer Rooms", "Conference Rooms"));
    }
    buildingSelect.setUnFocusColor(Color.BLACK);
  }

  // gets date
  public void pickDate(ActionEvent event) {
    resetBtns();
    datePicker.setDefaultColor(Color.BLUE);
    if (LocalDate.now().isAfter(datePicker.getValue())) {
      alert.setContent(
          new Label("You cannot pick a date that has happened!\nPlease pick a different date!"));
      alert.show();
      datePicker.setDefaultColor(Color.RED);
      datePicker.getEditor().clear();
    } else {
      date = datePicker.getValue().toString();
    }
  }

  // checks which beds have already been reserved after the user selects a time and date
  private void checkIsReserved() {
    for (int r = 0; r < reservations.size(); r++) {
      LocalTime start = parse(reservations.get(r).timeReservedStart);
      LocalTime end = parse(reservations.get(r).timeReservedEnd);
      LocalTime compareStart = parse(timeStart);
      LocalTime compareEnd = parse(timeEnd);
      if (reservations.get(r).dateReserved.equals(date)) {
        if (start.equals(compareStart)
            || compareStart.isAfter(start) && compareStart.isBefore(end)
            || compareEnd.isAfter(start) && compareEnd.isBefore(end)) {
          for (JFXButton bed : beds) {
            if (reservations.get(r).reservationType.equalsIgnoreCase(bed.getText())) {
              bed.setDisable(true);
              bed.setText("RESERVED!");
            }else if(!bed.getText().equals("RESERVED!")){
              bed.setDisable(false);
            }
          }
          for (JFXButton refRoom : refRooms) {
            if (reservations.get(r).reservationType.equalsIgnoreCase(refRoom.getText())) {
              refRoom.setDisable(true);
              refRoom.setText("RESERVED!");
            }else if(!refRoom.getText().equals("RESERVED!")){
              refRoom.setDisable(false);
            }
          }
          for (JFXButton compRoom : compRooms) {
            if (reservations.get(r).reservationType.equalsIgnoreCase(compRoom.getText())) {
              compRoom.setDisable(true);
              compRoom.setText("RESERVED!");
            }else if(!compRoom.getText().equals("RESERVED!")){
              compRoom.setDisable(false);
            }
          }
          for (JFXButton confRoom : confRooms) {
            if (reservations.get(r).reservationType.equalsIgnoreCase(confRoom.getText())) {
              confRoom.setDisable(true);
              confRoom.setText("RESERVED!");
            } else if(!confRoom.getText().equals("RESERVED!")){
              confRoom.setDisable(false);
            }
          }
        }
      }else{
        for (JFXButton bed : beds) {
          if(!bed.getText().equals("RESERVED!")) {
            bed.setDisable(false);
          }
        }
        for (JFXButton refRoom : refRooms) {
          if(!refRoom.getText().equals("RESERVED!")) {
            refRoom.setDisable(false);
          }
        }
        for (JFXButton comp : compRooms) {
          if(!comp.getText().equals("RESERVED!")) {
            comp.setDisable(false);
          }
        }
        for (JFXButton conf : confRooms) {
          if(!conf.getText().equals("RESERVED!")) {
            conf.setDisable(false);
          }
        }
      }
    }
  }

  // action for the checkBtn to check what is available
  public void checkAvail(ActionEvent event) {
    if (building != null
        && resSelect.getSelectionModel().getSelectedItem() != null
        && date != null
        && timeStart != null
        && timeEnd != null) {
      LocalTime start = parse(timeStart);
      LocalTime end = parse(timeEnd);
      if (end.isBefore(start)||end.equals(start)) {
        alert.setContent(new Label("End time cannot be before\nor the same as start!"));
        alert.show();
        timePickerEnd.setDefaultColor(Color.RED);
        timePickerEnd.getEditor().clear();
      } else {
        checkIsReserved();
      }
    } else {
      isMissingFields();
    }
  }

  // checks if there are any missing fields and sets color to red to alert user
  private boolean isMissingFields() {
    boolean bool = false;
    if (datePicker.getValue() == null
        || buildingSelect.getSelectionModel().getSelectedItem() == null
        || resSelect.getSelectionModel().getSelectedItem() == null
        || timePickerStart.getValue() == null
        || timePickerEnd.getValue() == null) {
      if (datePicker.getValue() == null || datePicker.getEditor().getText().isBlank()) {
        datePicker.setDefaultColor(Color.RED);
      }
      if (buildingSelect.getSelectionModel().getSelectedItem() == null) {
        buildingSelect.setUnFocusColor(Color.RED);
      }
      if (resSelect.getSelectionModel().getSelectedItem() == null) {
        resSelect.setUnFocusColor(Color.RED);
      }
      if (timePickerStart.getValue() == null || timePickerStart.getEditor().getText().isBlank()) {
        timePickerStart.setDefaultColor(Color.RED);
      }
      if (timePickerEnd.getValue() == null || timePickerEnd.getEditor().getText().isBlank()) {
        timePickerEnd.setDefaultColor(Color.RED);
      }
      alert.setContent(new Label("You need to enter the required fields"));
      alert.show();
      bool = true;
    }
    return bool;
  }

  // action button to go to home screen
  public void goHome(ActionEvent event) {
    Stage stage = (Stage) homeBtn.getScene().getWindow();
    stage.close();
  }

  // gets the time start chosen
  public void pickTimeStart(ActionEvent event) {
    resetBtns();
    timeStart = timePickerStart.getValue().toString();
    timePickerStart.setDefaultColor(Color.BLUE);
  }

  // gets the time end chosen
  public void pickTimeEnd(ActionEvent event) {
    resetBtns();
    timeEnd = timePickerEnd.getValue().toString();
    timePickerEnd.setDefaultColor(Color.BLUE);
  }

  public void resSelection(ActionEvent event) {
    resetBtns();
    bedGridPane.setVisible(false);
    roomGridPane.setVisible(false);
    compGridPane.setVisible(false);
    confGridPane.setVisible(false);
    if(resSelect.getSelectionModel().isEmpty()){
      bedGridPane.setVisible(false);
      roomGridPane.setVisible(false);
      compGridPane.setVisible(false);
      confGridPane.setVisible(false);
    } else if (resSelect.getSelectionModel().getSelectedItem().equals("Reflection Rooms")) {
      roomGridPane.toFront();
      roomGridPane.setVisible(true);
    } else if (resSelect.getSelectionModel().getSelectedItem().equals("On Call Beds")) {
      bedGridPane.toFront();
      bedGridPane.setVisible(true);
    } else if (resSelect.getSelectionModel().getSelectedItem().equals("Computer Rooms")) {
      compGridPane.toFront();
      compGridPane.setVisible(true);
    } else if (resSelect.getSelectionModel().getSelectedItem().equals("Conference Rooms")) {
      confGridPane.toFront();
      confGridPane.setVisible(true);
    }
  }

  public void goToCalendar(ActionEvent event) {
    try {
      Parent root =
          FXMLLoader.load(
              onCallBeds.class.getResource("views/ReservationCalendarEAPI.fxml"));
      onCallBeds.getAppEAPI().getScene().setRoot(root);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  // creates the reservation if everything has been filled int
  public void makeReservationBed(ActionEvent event) {
    if (!isMissingFields()) {
      for (int i = 0; i < beds.size(); i++) {
        if (event.getSource() == beds.get(i)) {
          // sets the popup windows for making the reservation
          resNum = beds.get(i).getText();
          setAlert();
          // display the alert
          alert.show();
        }
      }
    }
  }

  public void makeReservationReflRoom(ActionEvent event) {
    if (!isMissingFields()) {
      for (int i = 0; i < refRooms.size(); i++) {
        if (event.getSource() == refRooms.get(i)) {
          // sets the popup windows for making the reservation
          resNum = refRooms.get(i).getText();
          setAlert();
          // display the alert
          alert.show();
        }
      }
    }
  }

  public void makeReservationConf(ActionEvent event) {
    if (!isMissingFields()) {
      for (int i = 0; i < confRooms.size(); i++) {
        if (event.getSource() == confRooms.get(i)) {
          // sets the popup windows for making the reservation
          resNum = confRooms.get(i).getText();
          setAlert();
          // display the alert
          alert.show();
        }
      }
    }
  }

  public void makeReservationComp(ActionEvent event) {
    if (!isMissingFields()) {
      for (int i = 0; i < compRooms.size(); i++) {
        if (event.getSource() == compRooms.get(i)) {
          // sets the popup windows for making the reservation
          resNum = compRooms.get(i).getText();
          setAlert();
          // display the alert
          alert.show();
        }
      }
    }
  }

  private void makeLists() {
    beds.add(bed1);
    beds.add(bed2);
    beds.add(bed3);
    beds.add(bed4);
    beds.add(bed5);
    beds.add(bed6);
    beds.add(bed7);
    refRooms.add(refRoom1);
    refRooms.add(reflRoom2);
    refRooms.add(reflRoom3);
    compRooms.add(compRoom1);
    compRooms.add(compRoom2);
    compRooms.add(compRoom3);
    compRooms.add(compRoom4);
    compRooms.add(compRoom5);
    compRooms.add(compRoom6);
    confRooms.add(confRoom1);
    confRooms.add(confRoom2);
    confRooms.add(confRoom3);
    confRooms.add(confRoom4);
  }

  private void setAlert() {
    layout.setHeading(new Label("Make Reservation?"));
    // puts yes and no buttons into the dialogue box
    HBox buttons = new HBox(confirm, deny);
    buttons.setSpacing(20);
    buttons.setAlignment(Pos.CENTER);
    layout.setBody(buttons);
    // sets the confirm event to go to the enter name screen
    confirm.setOnAction(
        confirmEvent -> {
          alert.setContent(enterName);
        });
    // closes the alert if the user does not wish to make reservation
    deny.setOnAction(
        denyEvent -> {
          alert.close();
        });
    // final confirmation button to send the reservation
    makeRes.setOnAction(
        resEvent -> {
          if (!name.getText().isEmpty()) {
            OnCallBedReserveEAPI reserve =
                new OnCallBedReserveEAPI(
                    date, building, resNum, timeStart, timeEnd, name.getText(), "Y");
            int verify = reserve.sendReservation();
            // confirm that the reservation is valid
            if (verify == 1) {
              alert.setContent(new Label("Your reservation has been made\nThank you!"));
              alert.setOnCloseRequest(
                  close -> {
                    try {
                      Parent root =
                          FXMLLoader.load(onCallBeds.class.getResource("views/ReservationCalendarEAPI.fxml"));
                      onCallBeds.getAppEAPI().getScene().setRoot(root);
                    } catch (IOException ex) {
                      ex.printStackTrace();
                    }
                  });
            }
          } else {
            // if user tries to confirm before typing name, it alerts
            name.setUnFocusColor(Color.RED);
            name.setPromptText("Please Enter a name!");
          }
        });
    // setting the alert to the dialogue info
    alert.setContent(layout);
  }

  private void resetBtns() {
    for(int i=0;i<refRooms.size();i++){
      refRooms.get(i).setDisable(true);
      refRooms.get(i).setText("Reflection Room "+(i+1));
    }
    for(int i=0;i<beds.size();i++){
      beds.get(i).setDisable(true);
      beds.get(i).setText("Bed "+(i+1));
    }for(int i=0;i<compRooms.size();i++){
      compRooms.get(i).setDisable(true);
      compRooms.get(i).setText("Computer Room "+(i+1));
    }for(int i=0;i<confRooms.size();i++){
      confRooms.get(i).setDisable(true);
    }
    confRoom1.setText("Abrams Conference Room");
    confRoom2.setText("Anesthesia Conference Room");
    confRoom3.setText("Duncan Reid Conference Room");
    confRoom4.setText("Medical Records Conference Room");
  }

}
