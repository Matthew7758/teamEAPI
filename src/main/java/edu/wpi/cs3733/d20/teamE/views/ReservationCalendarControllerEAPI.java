package edu.wpi.cs3733.d20.teamE.views;

import com.calendarfx.model.*;
import com.calendarfx.model.Calendar;
import com.calendarfx.view.*;
import com.calendarfx.view.page.DayPage;
import com.calendarfx.view.popover.EntryHeaderView;
import com.calendarfx.view.popover.PopOverContentPane;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import edu.wpi.cs3733.d20.teamE.DBEAPI;
import edu.wpi.cs3733.d20.teamE.onCallBeds;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.*;
import java.util.*;
import java.util.List;

public class ReservationCalendarControllerEAPI {
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
  DBEAPI database = new DBEAPI("admin", "password");
  ObservableList<OnCallBedDataEAPI> reserves = getReserve();
  @FXML
  HBox hbox;
  @FXML
  TabPane calendarTabs=new TabPane();
  @FXML
  Tab bedTab=new Tab("On Call Beds");
  @FXML
  Tab refTab=new Tab("Reflection Rooms");
  @FXML
  Tab compTab=new Tab("Computer Rooms");
  @FXML
  Tab confTab=new Tab("Conference Rooms");
  @FXML
  public Tab allCallTab;

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
    cal.getCalendarSources().setAll(bedCalendar,refRoomCalendar,compRoomCalendar,confRoomsCalendar);
    setStyles();
    setUpCalendar();
    addReservations();
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
        OnCallBedDataEAPI foundReq = buildReservation(lineInfo);
        theReqs.add(foundReq);
      }
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return theReqs;
  }

  private void addReservations() {
    Connection connection = database.connectDB("admin", "password");
    for (OnCallBedDataEAPI b : reserves) {
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
              if (cal.getName().contains("Bed")) {
                entry.setLocation("Faulkner");
              } else {
                entry.setLocation("Main Campus");
              }
              if (cal.getName().contains("Computer") || cal.getName().contains("Conference")) {
                entry.setLocation(entry.getLocation() + " Flexible Workshop");
              }
              entry.setTitle(cal.getName() + ": " + entry.getTitle());
              entry.setCalendar(cal);
              reserves.add(new OnCallBedDataEAPI(entry.getId(),
                      entry.getStartDate().toString(),
                      entry.getStartTime().toString(),
                      entry.getEndTime().toString(),
                      entry.getLocation(),
                      cal.getName(),
                      entry.getTitle(),
                      "Y"));
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
                      "/edu/wpi/cs3733/d20/teamE/views/OnCallBedEAPI.fxml"));
      onCallBeds.getAppEAPI().getScene().setRoot(root);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void setStyles() {
    for (int i = 0; i < bedCalendar.getCalendars().size(); i++) {
      bedCalendar.getCalendars().get(i).setStyle(Calendar.Style.values()[i]);
      bedCalendar.getCalendars().get(i).setShortName("Bed "+(i+1));
    }
    for (int i = 0; i < refRoomCalendar.getCalendars().size(); i++) {
      refRoomCalendar.getCalendars().get(i).setStyle(Calendar.Style.values()[i]);
      refRoomCalendar.getCalendars().get(i).setShortName("Room "+(i+1));

    }
    for (int i = 0; i < compRoomCalendar.getCalendars().size(); i++) {
      compRoomCalendar.getCalendars().get(i).setStyle(Calendar.Style.values()[i]);
      compRoomCalendar.getCalendars().get(i).setShortName("Room "+(i+1));
    }
    for (int i = 0; i < confRoomsCalendar.getCalendars().size(); i++) {
      confRoomsCalendar.getCalendars().get(i).setStyle(Calendar.Style.values()[i]);
    }
    confRoom1.setShortName("Abrams");
    confRoom2.setShortName("Anesthesia");
    confRoom3.setShortName("Duncan Reid");
    confRoom4.setShortName("Medical Records");
  }

  private PopOver customPopUp(Entry entry) {
    PopOverContentPane popOverContentPane = new PopOverContentPane();
    EntryHeaderView entryHeaderView = new EntryHeaderView(entry, cal.getCalendars());

    cal.getStyleClass().add("entry-details-view");

    Label dateLabel = new Label("Date:");
    Label startTimeLabel = new Label(Messages.getString("EntryDetailsView.FROM")); // $NON-NLS-1$
    Label endTimeLabel = new Label(Messages.getString("EntryDetailsView.TO")); // $NON-NLS-1$

    JFXTimePicker startTimeField = new JFXTimePicker();
    startTimeField.setValue(entry.getStartTime());
    startTimeField.disableProperty().bind(entry.getCalendar().readOnlyProperty());

    JFXTimePicker endTimeField = new JFXTimePicker();
    endTimeField.setValue(entry.getEndTime());
    endTimeField.disableProperty().bind(entry.getCalendar().readOnlyProperty());

    JFXDatePicker startDatePicker = new JFXDatePicker();
    startDatePicker.setValue(entry.getStartDate());
    startDatePicker.disableProperty().bind(entry.getCalendar().readOnlyProperty());

    entry.intervalProperty().addListener(
                    it -> {
                      startTimeField.setValue(entry.getStartTime());
                      endTimeField.setValue(entry.getEndTime());
                      startDatePicker.setValue(entry.getStartDate());
                    });
    startDatePicker.setValue(entry.getStartDate());

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

    box.add(dateLabel, 0, 0);
    box.add(startDatePicker, 1, 0);
    box.add(startTimeLabel, 0, 2);
    box.add(startTimeField, 1, 2);
    box.add(endTimeLabel, 0, 3);
    box.add(endTimeField, 1, 3);
    box.add(zoneLabel, 0, 4);
    box.add(zoneBox, 1, 4);

    box.setHgap(5);
    GridPane.setFillWidth(zoneBox, true);
    GridPane.setHgrow(zoneBox, Priority.ALWAYS);

    ColumnConstraints col1 = new ColumnConstraints();
    ColumnConstraints col2 = new ColumnConstraints();

    col1.setHalignment(HPos.RIGHT);
    col2.setHalignment(HPos.LEFT);

    box.getColumnConstraints().addAll(col1, col2);

    startTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));
    endTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));

    // date and start time
    startDatePicker
            .valueProperty()
            .addListener(evt -> entry.changeStartDate(startDatePicker.getValue(), true));
    startTimeField.valueProperty().addListener(evt -> {
      List list=entry.getCalendar().findEntries(entry.getCalendar().getName());
      list.remove(entry);
      Iterator i=list.iterator();
      while (i.hasNext()){
        Entry ent=(Entry) i.next();
        LocalTime start = ent.getStartTime();
        LocalTime end = ent.getEndTime();
        LocalTime compareStart = startTimeField.getValue();
        if(!(start.equals(compareStart)
                || compareStart.isAfter(start) && compareStart.isBefore(end)||start.isAfter(compareStart))){
          entry.changeStartTime(startTimeField.getValue(), false);
        }
      }
    });

    // end time
    endTimeField.valueProperty().addListener(evt -> {
      List list=entry.getCalendar().findEntries(entry.getCalendar().getName());
      list.remove(entry);
      Iterator i=list.iterator();
      while (i.hasNext()){
        Entry ent=(Entry) i.next();
        LocalTime start = ent.getStartTime();
        LocalTime end = ent.getEndTime();
        LocalTime compareEnd = endTimeField.getValue();
        if(!(compareEnd.isBefore(entry.getStartTime())||compareEnd.isAfter(start)&&compareEnd.isBefore(end)||end.equals(compareEnd)||end.isBefore(compareEnd))){
          entry.changeEndTime(endTimeField.getValue(), false);
        }
      }
    });

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
    cal.prefWidthProperty().bind(calendarTabs.widthProperty());
    cal.prefHeightProperty().bind(calendarTabs.heightProperty());
    cal.setShowAddCalendarButton(false);
    cal.getDayPage().setDayPageLayout(DayPage.DayPageLayout.DAY_ONLY);
    cal.setTransitionsEnabled(true);
    cal.setTraysAnimated(true);
    cal.getDayPage().setShowDayPageLayoutControls(false);
    cal.setShowPrintButton(false);
    cal.setShowPageToolBarControls(false);
    cal.setLayout(DateControl.Layout.SWIMLANE);
    cal.addEventHandler(
            ActionEvent.ANY,
            event -> {
              if (cal.getSelectedPage().equals(cal.getWeekPage())) {
                cal.setLayout(DateControl.Layout.STANDARD);
              } else if (cal.getSelectedPage().equals(cal.getDayPage())) {
                cal.setLayout(DateControl.Layout.SWIMLANE);
              }
            });
    cal.setEntryEditPolicy(
            e -> {
              if (e.getEditOperation().equals(DateControl.EditOperation.MOVE)) {
                return false;
              }
              if(e.getEditOperation().equals(DateControl.EditOperation.CHANGE_START)||e.getEditOperation().equals(DateControl.EditOperation.CHANGE_END)){
                return false;
              }
              return true;
            });
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
              contextMenu.getItems().get(0).setStyle("-fx-text-fill: black");
              contextMenu.getItems().get(1).setStyle("-fx-text-fill: black");
              return contextMenu;
            });
    cal.setContextMenuCallback(
            e -> {
              ContextMenu cM;
              ContextMenuProvider c = new ContextMenuProvider();
              cM =
                      c.call(
                              new DateControl.ContextMenuParameter(
                                      e.getContextMenuEvent(),
                                      e.getDateControl(),
                                      e.getCalendar(),
                                      e.getZonedDateTime()));
              cM.getItems().remove(1);
              cM.getItems().remove(1);
              cM.getItems().remove(1);
              cM.getItems().get(0).setStyle("-fx-text-fill: black");
              cM.getItems()
                      .get(0)
                      .setOnAction(
                              evt -> {
                                if (e.getDateControl().getLayout().equals(DateControl.Layout.SWIMLANE)) {
                                  Calendar calendar =
                                          e.getDateControl()
                                                  .getCalendarAt(e.getContextMenuEvent().getX(), e.getContextMenuEvent().getY()).orElse(null);
                                  e.getDateControl().createEntryAt(e.getZonedDateTime(), calendar);
                                } else {
                                  e.getDateControl().createEntryAt(e.getZonedDateTime());
                                }
                              });
              return cM;
            });
    cal.setEntryFactory(e->{
      int entryCounter=1;
      DateControl control = e.getDateControl();

      VirtualGrid grid = control.getVirtualGrid();
      ZonedDateTime time = e.getZonedDateTime();
      DayOfWeek firstDayOfWeek = control.getFirstDayOfWeek();
      ZonedDateTime lowerTime = grid.adjustTime(time, false, firstDayOfWeek);
      ZonedDateTime upperTime = grid.adjustTime(time, true, firstDayOfWeek);

      if (Duration.between(time, lowerTime).abs().minus(Duration.between(time, upperTime).abs()).isNegative()) {
        time = lowerTime;
      } else {
        time = upperTime;
      }

      Entry<Object> entry=null;
        for(Calendar c:e.getDateControl().getCalendars()) {
          List list = c.findEntries(e.getDefaultCalendar().getName());
          Iterator i = list.iterator();
          while (i.hasNext()){
            Entry ent= (Entry) i.next();
            LocalTime start = ent.getStartTime();
            LocalTime end = ent.getEndTime();
            LocalTime compareStart = time.toLocalTime();
            LocalTime compareEnd = time.toLocalTime().plusHours(1);
            if (ent.getCalendar().getName().equals(e.getDefaultCalendar().getName())) {
              if (ent.getStartDate().equals(time.toLocalDate())) {
                if (start.equals(compareStart)
                        || compareStart.isAfter(start) && compareStart.isBefore(end)
                        || compareEnd.isAfter(start) && compareEnd.isBefore(end)) {
                  return null;
                }
              }
            }
          }
        }
        if(time.toLocalTime().isBefore(LocalTime.now())){
          return null;
        }
        entry = new Entry<>(MessageFormat.format(Messages.getString("DateControl.DEFAULT_ENTRY_TITLE"), entryCounter++)); //$NON-NLS-1$
        entry.setCalendar(e.getDefaultCalendar());
        Interval interval = new Interval(time.toLocalDateTime(), time.toLocalDateTime().plusHours(1));
        entry.setInterval(interval);

      return entry;
    });
  }

  public void tabHandler(Event event) {
    bedTab.setContent(null);
    refTab.setContent(null);
    compTab.setContent(null);
    confTab.setContent(null);
    allCallTab.setContent(null);
    if(event.getSource().equals(allCallTab)){
      cal.getCalendarSources().setAll(bedCalendar,refRoomCalendar,compRoomCalendar,confRoomsCalendar);
      allCallTab.setContent(cal);
    }
    if(event.getSource().equals(bedTab)) {
      cal.getCalendarSources().setAll(bedCalendar);
      bedTab.setContent(cal);
    }
    if(event.getSource().equals(refTab)){
      cal.getCalendarSources().setAll(refRoomCalendar);
      refTab.setContent(cal);
    }
    if(event.getSource().equals(compTab)) {
      cal.getCalendarSources().setAll(compRoomCalendar);
      compTab.setContent(cal);
    }
    if(event.getSource().equals(confTab)){
      cal.getCalendarSources().setAll(confRoomsCalendar);
      confTab.setContent(cal);
    }
  }
}
