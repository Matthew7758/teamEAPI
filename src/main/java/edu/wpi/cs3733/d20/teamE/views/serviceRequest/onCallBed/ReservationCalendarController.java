package edu.wpi.cs3733.d20.teamE.views.serviceRequest.onCallBed;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarEvent;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.Messages;
import com.calendarfx.view.TimeField;
import com.calendarfx.view.page.DayPage;
import com.calendarfx.view.popover.EntryHeaderView;
import com.calendarfx.view.popover.PopOverContentPane;
import com.jfoenix.controls.JFXButton;
import edu.wpi.cs3733.d20.teamE.App;
import edu.wpi.cs3733.d20.teamE.DB;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Set;

public class ReservationCalendarController {
  public AnchorPane anchorPane;
  public JFXButton backBtn;
  public VBox vbox;
  Calendar bed1 = new Calendar("Bed 1");
  Calendar bed2 = new Calendar("Bed 2");
  Calendar bed3 = new Calendar("Bed 3");
  Calendar bed4 = new Calendar("Bed 4");
  Calendar bed5 = new Calendar("Bed 5");
  Calendar bed6 = new Calendar("Bed 6");
  Calendar bed7 = new Calendar("Bed 7");
  Calendar refRoom1 = new Calendar("Reflection Room 1");
  Calendar refRoom2 = new Calendar("Reflection Room 2");
  Calendar refRoom3 = new Calendar("Reflection Room 3");
  Calendar compRoom1 = new Calendar("Computer Room 1");
  Calendar compRoom2 = new Calendar("Computer Room 2");
  Calendar compRoom3 = new Calendar("Computer Room 3");
  Calendar compRoom4 = new Calendar("Computer Room 4");
  Calendar compRoom5 = new Calendar("Computer Room 5");
  Calendar compRoom6 = new Calendar("Computer Room 6");
  Calendar confRoom1 = new Calendar("Abrams Conference Room");
  Calendar confRoom2 = new Calendar("Anesthesia Conference Room");
  Calendar confRoom3 = new Calendar("Duncan Reid Conference Room");
  Calendar confRoom4 = new Calendar("Medical Records Conference Room");

  CalendarSource bedCalendar = new CalendarSource("On Call Beds");
  CalendarSource refRoomCalendar = new CalendarSource("Reflection Rooms");
  CalendarSource compRoomCalendar = new CalendarSource("Computer Rooms");
  CalendarSource confRoomsCalendar = new CalendarSource("Conference Rooms");
  CalendarView cal = new CalendarView();
  Entry<String> reservation;
  // DeveloperConsole console = new DeveloperConsole();
  String delim = "#";
  DB database = new DB("admin", "password");
  ObservableList<OnCallBedData> reserves = getReserve();
  @FXML HBox hbox;

  PopOver p1 = new PopOver();
  public void initialize() {
    p1.setTitle("Reservation Info");
    p1.setAutoHide(true);
    p1.setHeaderAlwaysVisible(true);
    bedCalendar.getCalendars().addAll(bed1, bed2, bed3, bed4, bed5, bed6, bed7);
    refRoomCalendar.getCalendars().addAll(refRoom1, refRoom2, refRoom3);
    compRoomCalendar
            .getCalendars()
            .addAll(compRoom1, compRoom2, compRoom3, compRoom4, compRoom5, compRoom6);
    confRoomsCalendar.getCalendars().addAll(confRoom1, confRoom2, confRoom3, confRoom4);
    cal.getCalendarSources()
            .setAll(bedCalendar, refRoomCalendar, compRoomCalendar, confRoomsCalendar);
    setStyles();
    setUpCalendar();
    addReservations();
    vbox.getChildren().add(cal);
  }
  // got this code from Matthew to be able to look at the database in this scene
  private OnCallBedData buildReservation(String[] params) {
    String requestID = params[0];
    String dateReserved = params[1];
    String timeReservedStart = params[2];
    String timeReservedEnd = params[3];
    String building = params[4];
    String reservationType = params[5];
    String reservedFor = params[6];
    String isReserved = params[7];
    return new OnCallBedData(
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
  public ObservableList<OnCallBedData> getReserve() {
    ObservableList<OnCallBedData> theReqs = FXCollections.observableArrayList();
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
                + delim
                + dateReserved
                + delim
                + timeReservedStart
                + delim
                + timeReservedEnd
                + delim
                + building
                + delim
                + reservationType
                + delim
                + reservedFor
                + delim
                + isReserved;
        String[] lineInfo = finalString.split(delim);
        OnCallBedData foundReq = buildReservation(lineInfo);
        theReqs.add(foundReq);
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return theReqs;
  }

  private void addReservations() {
    Connection connection = database.connectDB("admin", "password");
    for (OnCallBedData b : reserves) {
      if (b.reservedFor.contains(":")) {
        reservation = new Entry<>(b.reservedFor);
      } else {
        reservation = new Entry<>(b.reservationType + ": " + b.reservedFor);
      }
      for (Calendar c : cal.getCalendars()) {
        if (c.getName().equalsIgnoreCase(b.reservationType)) {
          reservation.setCalendar(c);
        }
        reservation.setInterval(
            LocalDate.parse(b.dateReserved),
            LocalTime.parse(b.timeReservedStart),
            LocalDate.parse(b.dateReserved),
            LocalTime.parse(b.timeReservedEnd));
        reservation.setLocation(b.building);
        if (!b.requestID.equals(reservation.getId())) {
          database.updateDB(
              connection, "onCallBeds", b.requestID, "REQUESTID", reservation.getId());
        }
      }
    }
    eventHand();
  }

  private void eventHand() {
    Connection connection = database.connectDB("admin", "password");
    for (Calendar c : cal.getCalendars()) {
      c.addEventHandler(
          event -> {
            if (event.isEntryAdded()) {
              Calendar cal = event.getCalendar();
              Entry entry = event.getEntry();
              entry.setMinimumDuration(Duration.ofMinutes(30));
              System.out.println(entry.getDuration());
              if (cal.getName().contains("Bed")) {
                entry.setLocation("Faulkner");
              } else {
                entry.setLocation("Main Campus");
              }
              if (cal.getName().contains("Computer") || cal.getName().contains("Conference")) {
                entry.setLocation(entry.getLocation() + " Flexible Workshop");
              }
              entry.setTitle(cal.getName() + ": " + entry.getTitle());
              database.addReservation(
                  connection,
                  entry.getId(),
                  entry.getStartDate().toString(),
                  entry.getStartTime().toString(),
                  entry.getEndTime().toString(),
                  entry.getLocation(),
                  cal.getName(),
                  entry.getTitle(),
                  "Y");
            } else if (event.getEventType().equals(CalendarEvent.ENTRY_INTERVAL_CHANGED)) {
              Calendar cal = event.getCalendar();
              Entry entry = event.getEntry();
              database.updateDB(
                  connection,
                  "onCallBeds",
                  entry.getId(),
                  "timeReservedStart",
                  entry.getStartTime().toString());
              database.updateDB(
                  connection,
                  "onCallBeds",
                  entry.getId(),
                  "timeReservedEnd",
                  entry.getEndTime().toString());
            } else if (event.getEventType().equals(CalendarEvent.CALENDAR_CHANGED)) {
              Calendar cal = event.getCalendar();
              Entry entry = event.getEntry();
              database.updateDB(
                  connection, "onCallBeds", entry.getId(), "reservationType", cal.getName());
            } else if (event.getEventType().equals(CalendarEvent.ENTRY_TITLE_CHANGED)) {
              Calendar cal = event.getCalendar();
              Entry entry = event.getEntry();
              database.updateDB(
                  connection, "onCallBeds", entry.getId(), "reservedFor", entry.getTitle());
            } else if (event.isEntryRemoved()) {
              Calendar cal = event.getCalendar();
              Entry entry = event.getEntry();
              database.removeReservation(connection, entry.getId());
            }
          });
    }
  }

  public void goBack(ActionEvent event) {
    try {
      Parent root =
          FXMLLoader.load(
              getClass()
                  .getResource(
                      "/edu/wpi/cs3733/d20/teamE/views/onCallBed.fxml"));
      App.getPrimaryStage().getScene().setRoot(root);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void setStyles() {
    for (int i = 0; i < bedCalendar.getCalendars().size(); i++) {
      bedCalendar.getCalendars().get(i).setStyle(Calendar.Style.values()[i]);
    }
    for (int i = 0; i < refRoomCalendar.getCalendars().size(); i++) {
      refRoomCalendar.getCalendars().get(i).setStyle(Calendar.Style.values()[i]);
    }
    for (int i = 0; i < compRoomCalendar.getCalendars().size(); i++) {
      compRoomCalendar.getCalendars().get(i).setStyle(Calendar.Style.values()[i]);
    }
    for (int i = 0; i < confRoomsCalendar.getCalendars().size(); i++) {
      confRoomsCalendar.getCalendars().get(i).setStyle(Calendar.Style.values()[i]);
    }
  }

  private PopOver customPopUp(Entry entry) {
    PopOverContentPane popOverContentPane = new PopOverContentPane();
    EntryHeaderView entryHeaderView = new EntryHeaderView(entry, cal.getCalendars());
    // EntryDetailsView entryDetailsView = new EntryDetailsView(entry);
    cal.getStyleClass().add("entry-details-view");

    Label startDateLabel = new Label(Messages.getString("EntryDetailsView.FROM")); // $NON-NLS-1$
    Label endDateLabel = new Label(Messages.getString("EntryDetailsView.TO")); // $NON-NLS-1$

    TimeField startTimeField = new TimeField();
    startTimeField.setValue(entry.getStartTime());
    startTimeField.disableProperty().bind(entry.getCalendar().readOnlyProperty());

    TimeField endTimeField = new TimeField();
    endTimeField.setValue(entry.getEndTime());
    endTimeField.disableProperty().bind(entry.getCalendar().readOnlyProperty());

    DatePicker startDatePicker = new DatePicker();
    startDatePicker.setValue(entry.getStartDate());
    startDatePicker.disableProperty().bind(entry.getCalendar().readOnlyProperty());

    DatePicker endDatePicker = new DatePicker();
    endDatePicker.setValue(entry.getEndDate());
    endDatePicker.disableProperty().bind(entry.getCalendar().readOnlyProperty());

    entry
            .intervalProperty()
            .addListener(
                    it -> {
                      startTimeField.setValue(entry.getStartTime());
                      endTimeField.setValue(entry.getEndTime());
                      startDatePicker.setValue(entry.getStartDate());
                      endDatePicker.setValue(entry.getEndDate());
                    });

    HBox startDateBox = new HBox(10);
    HBox endDateBox = new HBox(10);

    startDateBox.setAlignment(Pos.CENTER_LEFT);
    endDateBox.setAlignment(Pos.CENTER_LEFT);

    startDateBox.getChildren().addAll(startDateLabel, startDatePicker, startTimeField);
    endDateBox.getChildren().addAll(endDateLabel, endDatePicker, endTimeField);

    startDatePicker.setValue(entry.getStartDate());
    endDatePicker.setValue(entry.getEndDate());

    Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
    ObservableList<ZoneId> zoneIds = FXCollections.observableArrayList();
    for (String id : availableZoneIds) {
      ZoneId zoneId = ZoneId.of(id);
      if (!zoneIds.contains(zoneId)) {
        zoneIds.add(zoneId);
      }
    }

    zoneIds.sort(Comparator.comparing(ZoneId::getId));

    Label zoneLabel = new Label(Messages.getString("EntryDetailsView.TIMEZONE")); // $NON-NLS-1$

    ComboBox<ZoneId> zoneBox = new ComboBox<>(zoneIds);
    zoneBox.disableProperty().bind(entry.getCalendar().readOnlyProperty());
    zoneBox.setConverter(
            new StringConverter<ZoneId>() {

              @Override
              public String toString(ZoneId object) {
                return object.getId();
              }

              @Override
              public ZoneId fromString(String string) {
                return null;
              }
            });
    zoneBox.setValue(entry.getZoneId());

    GridPane box = new GridPane();
    box.getStyleClass().add("content"); // $NON-NLS-1$
    box.add(startDateLabel, 0, 0);
    box.add(startDateBox, 1, 0);
    box.add(endDateLabel, 0, 1);
    box.add(endDateBox, 1, 1);
    box.add(zoneLabel, 0, 2);
    box.add(zoneBox, 1, 2);

    GridPane.setFillWidth(zoneBox, true);
    GridPane.setHgrow(zoneBox, Priority.ALWAYS);

    ColumnConstraints col1 = new ColumnConstraints();
    ColumnConstraints col2 = new ColumnConstraints();

    col1.setHalignment(HPos.RIGHT);
    col2.setHalignment(HPos.LEFT);

    box.getColumnConstraints().addAll(col1, col2);

    startTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));
    endTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));

    // start date and time
    startDatePicker
            .valueProperty()
            .addListener(evt -> entry.changeStartDate(startDatePicker.getValue(), true));
    startTimeField
            .valueProperty()
            .addListener(evt -> entry.changeStartTime(startTimeField.getValue(), true));

    // end date and time
    endDatePicker
            .valueProperty()
            .addListener(evt -> entry.changeEndDate(endDatePicker.getValue(), false));
    endTimeField
            .valueProperty()
            .addListener(evt -> entry.changeEndTime(endTimeField.getValue(), false));

    // zone Id
    zoneBox.setOnAction(evt -> entry.setZoneId(zoneBox.getValue()));

    popOverContentPane.setHeader(entryHeaderView);
    entryHeaderView.getChildren().remove(1);
    entryHeaderView.getChildren().get(1).disableProperty().unbind();
    entryHeaderView.getChildren().get(1).setDisable(true);
    popOverContentPane.setFooter(box);
    PopOver popOver = new PopOver();
    popOver.setContentNode(popOverContentPane);
    return popOver;
  }
  private void setUpCalendar() {
    cal.prefWidthProperty().bind(vbox.widthProperty());
    cal.prefHeightProperty().bind(vbox.heightProperty());
    cal.setShowAddCalendarButton(false);
    cal.showDayPage();
    cal.getDayPage().setDayPageLayout(DayPage.DayPageLayout.DAY_ONLY);
    cal.setTransitionsEnabled(true);
    cal.setTraysAnimated(true);
    cal.getDayPage().setShowDayPageLayoutControls(false);
    cal.setShowPrintButton(false);
    cal.setShowPageToolBarControls(false);
    cal.setLayout(DateControl.Layout.SWIMLANE);
    cal.setEntryEditPolicy(
            e -> {
              if (e.getEditOperation().equals(DateControl.EditOperation.MOVE)) {
                return false;
              }
              return true;
            });
    // cal.setEntryDetailsPopOverContentCallback(e -> customPopUp(e.getEntry()).getContentNode());
    cal.setEntryDetailsCallback(
            e -> {
              return false;
            });
    cal.setEntryContextMenuCallback(
            e -> {
              ContextMenu contextMenu = new ContextMenu();
              MenuItem informationItem =
                      new MenuItem(Messages.getString("DateControl.MENU_ITEM_INFORMATION")); // $NON-NLS-1$
              informationItem.setOnAction(
                      evt -> {
                        p1.setContentNode(customPopUp(e.getEntry()).getContentNode());
                        p1.show(
                                e.getDateControl(),
                                e.getContextMenuEvent().getSceneX(),
                                e.getContextMenuEvent().getSceneY());
                      });
              contextMenu.getItems().add(informationItem);
              if (cal.getEntryEditPolicy()
                      .call(
                              new DateControl.EntryEditParameter(e.getDateControl(), e.getEntry(), DateControl.EditOperation.DELETE))) {
                MenuItem delete =
                        new MenuItem(Messages.getString("DateControl.MENU_ITEM_DELETE")); // $NON-NLS-1$
                contextMenu.getItems().add(delete);
                delete.setDisable(e.getCalendar().isReadOnly());
                delete.setOnAction(
                        evt -> {
                          Calendar calendar = e.getEntry().getCalendar();
                          if (!calendar.isReadOnly()) {
                            e.getEntry().removeFromCalendar();
                          }
                        });
              }
              return contextMenu;
            });
  }
}
