package edu.wpi.cs3733.d20.teamE;

import java.sql.*;
import java.util.UUID;

public class DB {
  private String password;
  private String username;
  public DB(String username, String password) {
    this.username=username;
    this.password=password;
    try {
      if (checkDBExists()) {
        System.out.println("Database exists");
        Connection connection = connectDB(this.username, this.password);
        connection.close();
      } else { // Create files
        System.out.println("Running first time setup");
        runSetup();
      }
    }
    catch(Exception e) {
      System.out.println("Error in DB()");
      e.printStackTrace();
    }
  }

  private boolean checkDBExists() {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver"); // Register JDBC Driver
      // System.out.println("Creating a connection...");
      Connection conn =
          DriverManager.getConnection(
              "jdbc:derby:EdbAPI;create=false", "admin", "password"); // Open a connection
      ResultSet resultSet = conn.getMetaData().getCatalogs();
      while (resultSet.next()) {
        String databaseName = resultSet.getString(1);
        // System.out.println(databaseName);
        if (databaseName.equals("EdbAPI") || databaseName.equals("EDBAPI")) {
          return true;
        }
      }
      resultSet.close();
    } catch (Exception e) {
      System.out.println("SQLException caught in checkDBExists()");
      System.out.println("Database does not exist");
      return false;
    }
    // System.out.println("End of checkDBExists()");
    return true;
  }

  public Connection connectDB(String user, String pass) {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    } catch (ClassNotFoundException e) {
      System.out.println("Apache Derby Driver not found. Add the classpath to your module.");
      e.printStackTrace();
      return null;
    }
    System.out.println("Apache Derby driver registered!");
    Connection connection = null;
    try {
      connection =
          DriverManager.getConnection(
              String.format("jdbc:derby:EdbAPI;create=true;user=%s;password=%s", user, pass));
      // Turn on built in users to ensure proper connection.
      turnOnBuiltInUsers(connection, this.username, this.password);
    } catch (SQLException e) {
      System.out.println("Connection failed. Check output console.");
      e.printStackTrace();
      return null;
    }
    // System.out.println("Apache Derby connection established!");
    return connection;
  }

  private static void turnOnBuiltInUsers(Connection connection, String username, String pass) {
    try {
      Statement s = connection.createStatement();
      // Setting and Confirming requireAuthentication
      s.executeUpdate(
          "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.connection.requireAuthentication', 'true')");
      ResultSet rs =
          s.executeQuery(
              "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.connection.requireAuthentication')");
      rs.next();
      // Setting authentication scheme to Derby
      s.executeUpdate(
          "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.authentication.provider', 'BUILTIN')");

      // Creating users
      // s.executeUpdate("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(" + "'derby.user." + username +
      // "', '" + pass + "')");
      String un = "'" + username + "'";
      String pw = "'" + pass + "'";
      s.executeUpdate(
          String.format(
              "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user.%s',%s)", username, pw));
      s.executeUpdate("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.user.guest', 'test')");

      // System.out.println("Successfully created user");

      // Setting default connection mode to no access
      // (user authorization)
      s.executeUpdate(
          "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.defaultConnectionMode', 'noAccess')");
      // Confirming default connection mode
      rs =
          s.executeQuery(
              "VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('derby.database.defaultConnectionMode')");
      rs.next();

      // System.out.println("Value of defaultConnectionMode is " + rs.getString(1));

      // Defining read-only users
      s.executeUpdate(
          "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.readOnlyAccessUsers', 'guest')");
      // Defining full access users
      s.executeUpdate(
          String.format(
              "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.database.fullAccessUsers',%s)",
              un));
      // We would set the following property to TRUE only
      // when we were ready to deploy.
      s.executeUpdate(
          "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY("
              + "'derby.database.propertiesOnly', 'false')");
      s.close();
    }
    catch(SQLException e) {
      System.out.println("Error in connectDB");
      e.printStackTrace();
    }
  }

  private void runSetup() {
    Connection connection = connectDB(this.username, this.password);
    turnOnBuiltInUsers(connection, this.username, this.password);
    try {
      Statement stmt = connection.createStatement();
      String sql = "CREATE TABLE onCallBeds(requestID VARCHAR(255), dateReserved VARCHAR(255), timeReservedStart VARCHAR(255), timeReservedEnd VARCHAR(255), building  VARCHAR(255), reservationType VARCHAR(255), reservedFor VARCHAR(255), isReserved CHAR(1), primary key(requestID))";
      stmt.execute(sql);
      stmt.close();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
  }

  private String generateUniqueID() {
    UUID tempUUID = UUID.randomUUID();
    return tempUUID.toString();
  }


  // OnCallBeds add request
  public int addReservation(
      Connection connection,
      String requestID,
      String dateReserved,
      String timeReservedStart,
      String timeReservedEnd,
      String building,
      String reservationType,
      String reservedFor,
      String isReserved) {
    String uniqueID = requestID;
    if (requestID == null || requestID.isEmpty() || requestID.isBlank()) {
      uniqueID = generateUniqueID();
    }
    try {
      String sql =
          "INSERT INTO ONCALLBEDS(REQUESTID, DATERESERVED, TIMERESERVEDSTART, TIMERESERVEDEND, BUILDING, RESERVATIONTYPE, RESERVEDFOR, ISRESERVED) VALUES(?,?,?,?,?,?,?,?)";
      // After creating sql template string, create prepared statements
      PreparedStatement statement = connection.prepareStatement(sql);
      // Use set string/setInt on statement to prepare string
      statement.setString(1, uniqueID);
      statement.setString(2, dateReserved);
      statement.setString(3, timeReservedStart);
      statement.setString(4, timeReservedEnd);
      statement.setString(5, building);
      statement.setString(6, reservationType);
      statement.setString(7, reservedFor);
      statement.setString(8, isReserved);
      // Execute it
      statement.executeUpdate();
      // DON'T CALL UPDATE HASH MAP ONLY CALL EXPORT DB
      return 1;
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return -1;
  }

  // OnCallBeds remove request
  public int removeReservation(Connection connection, String requestID) {
    try {
      String sql = "DELETE FROM ONCALLBEDS WHERE REQUESTID = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, requestID);
      statement.executeUpdate();
      return 1;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }

  public int updateDB(
      Connection connection,
      String table,
      String uniqueKey,
      String fieldToBeChanged,
      String newValue) {
    try {
      String primaryKeyType = "";
      primaryKeyType = "REQUESTID";
      String query = "";

      if (fieldToBeChanged.toUpperCase().equals("XCOORD")
          || fieldToBeChanged.toUpperCase().equals("YCOORD")
          || fieldToBeChanged.toUpperCase().equals("FLOOR")
          || fieldToBeChanged.toUpperCase().equals("NUMSTAFF")) {
        query =
            String.format(
                "UPDATE %s SET %s = '?' WHERE %s = '?'", table, fieldToBeChanged, primaryKeyType);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, Integer.parseInt(newValue));
        ps.setString(2, uniqueKey);
        ps.executeUpdate();
      } else {
        query =
            String.format(
                "UPDATE %s SET %s = ? WHERE %s = ?", table, fieldToBeChanged, primaryKeyType);
        System.out.println(
            "Updated "
                + fieldToBeChanged
                + " of primary key type "
                + primaryKeyType
                + " for node id "
                + uniqueKey);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, newValue);
        ps.setString(2, uniqueKey);
        ps.executeUpdate();
      }
      return 1;
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    // Update CSV files at the end.
    return -1;
  }

}
