package Database;
import java.sql.*;
/**
 * Created by Eric on 08/01/2018.
 */
public class DBConnection {

    public Connection getConnection() {

        try {
            /*String connectionUrl = "jdbc:sqlserver://localhost:1433;" + "databaseName=asset_manager;integratedSecurity=true;";
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");*/
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/asset_manager", "postgres", "Baraza2011");
            return connection;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
