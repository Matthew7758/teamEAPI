package edu.wpi.cs3733.d20.teamE.views;

public class OnCallBedData {
  // class to hold the data from the database
  String requestID;
  String dateReserved;
  String timeReservedStart;
  String timeReservedEnd;
  String building;
  String reservationType;
  String reservedFor;
  String isReserved;

  // constructor
  public OnCallBedData(
      String requestID,
      String dateReserved,
      String timeReservedStart,
      String timeReservedEnd,
      String building,
      String reservationType,
      String reservedFor,
      String isReserved) {
    this.requestID = requestID;
    this.dateReserved = dateReserved;
    this.timeReservedStart = timeReservedStart;
    this.timeReservedEnd = timeReservedEnd;
    this.building = building;
    this.reservationType = reservationType;
    this.reservedFor = reservedFor;
    this.isReserved = isReserved;
  }
}
