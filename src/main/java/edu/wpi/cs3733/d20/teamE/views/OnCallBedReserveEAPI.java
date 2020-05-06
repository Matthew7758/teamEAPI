package edu.wpi.cs3733.d20.teamE.views;

import edu.wpi.cs3733.d20.teamE.DBEAPI;

import java.sql.Connection;

// class to submit the request
public class OnCallBedReserveEAPI {
  String dateReserved;
  String timeReservedStart;
  String timeReservedEnd;
  String building;
  String reservationType;
  String reservedFor;
  String isReserved;
  DBEAPI DBEAPI = new DBEAPI("admin", "password");
  Connection conn = DBEAPI.connectDB("admin", "password");

  // constructor
  public OnCallBedReserveEAPI(
      String dateReserved,
      String building,
      String reservationType,
      String timeReservedStart,
      String timeReservedEnd,
      String reservedFor,
      String isReserved) {
    this.dateReserved = dateReserved;
    this.timeReservedStart = timeReservedStart;
    this.timeReservedEnd = timeReservedEnd;
    this.building = building;
    this.reservationType = reservationType;
    this.reservedFor = reservedFor;
    this.isReserved = isReserved;
  }

  // sends reservation to DB
  // has return value to be able to verify the request sent
  public int sendReservation() {
    int verify =
        DBEAPI.addReservation(
            conn,
            null,
            dateReserved,
            timeReservedStart,
            timeReservedEnd,
            building,
            reservationType,
            reservedFor,
            isReserved);
    return verify;
  }
}
