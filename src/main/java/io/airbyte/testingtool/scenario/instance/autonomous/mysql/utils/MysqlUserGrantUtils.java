package io.airbyte.testingtool.scenario.instance.autonomous.mysql.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;

public class MysqlUserGrantUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(MysqlUserGrantUtils.class);

  public static void setUpUserGrants(MySQLContainer instance) {
    try {
      Connection connection = getConnection(instance);
      connection.createStatement().execute("set global local_infile=true");
      connection.createStatement()
          .execute("REVOKE ALL PRIVILEGES, GRANT OPTION FROM " + instance.getUsername() + "@'%';");
      connection.createStatement().execute(
          "GRANT ALTER, CREATE, INSERT, SELECT, DROP ON *.* TO " + instance.getUsername()
              + "@'%';");
      connection.close();
    } catch (SQLException e) {
      LOGGER.error("Can not set up grants for user. Error: {}", e.getErrorCode());
    }
  }

  private static Connection getConnection(MySQLContainer instance) throws SQLException {
    return DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s",
        instance.getHost(),
        instance.getFirstMappedPort(),
        instance.getDatabaseName()), "root", "test");
  }
}
