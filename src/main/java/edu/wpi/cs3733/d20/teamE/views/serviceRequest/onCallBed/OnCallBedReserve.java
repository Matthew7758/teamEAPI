package edu.wpi.cs3733.d20.teamE.views.serviceRequest.onCallBed;

import edu.wpi.cs3733.d20.teamE.DB;

import java.sql.Connection;

// class to submit the request
public class OnCallBedReserve {
  String dateReserved;
  String timeReservedStart;
  String timeReservedEnd;
  String building;
  String reservationType;
  String reservedFor;
  String isReserved;
  DB db = new DB("admin", "password");
  Connection conn = db.connectDB("admin", "password");

  // constructor
  public OnCallBedReserve(
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
        db.addReservation(
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
