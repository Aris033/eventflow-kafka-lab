import java.sql.*;
class TestPg15432 {
  public static void main(String[] args) throws Exception {
    try (var c = DriverManager.getConnection("jdbc:postgresql://localhost:15432/eventflow", "eventflow", "eventflow")) {
      try (var rs = c.createStatement().executeQuery("select current_user, current_database()")) {
        rs.next();
        System.out.println(rs.getString(1) + " / " + rs.getString(2));
      }
    }
  }
}
