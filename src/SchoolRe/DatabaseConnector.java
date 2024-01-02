package SchoolRe;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class DatabaseConnector {
        private static final String JDBC_URL = "dbc:postgresql://localhost:5432/SHDB;";
        private static final String USERNAME = "postgres";
        private static final String PASSWORD = "12345";

        public static Connection connect() throws SQLException {
            return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        }
    }


