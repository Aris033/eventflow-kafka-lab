import java.sql.*;
class TestPgContainer {
  public static void main(String[] args) throws Exception {
    try (var c = DriverManager.getConnection("jdbc:postgresql://172.18.0.4:5432/eventflow", "eventflow", "eventflow")) {
      try (var rs = c.createStatement().executeQuery("select current_user, current_database()")) {
        rs.next();
        System.out.println(rs.getString(1) + " / " + rs.getString(2));
      }
    }
  }
}
