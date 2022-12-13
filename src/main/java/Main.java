import dataaccess.BaseRepository;
import dataaccess.MysqlCourseRepository;
import dataaccess.MysqlDatabaseConnection;
import ui.Cli;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        try {
            Cli myCli = new Cli(new MysqlCourseRepository());
            myCli.start();


        } catch(SQLException e){
            System.out.println("Datenbankfehler: "+ e.getMessage() + " SQL STATE: "+ e.getSQLState());
        } catch (ClassNotFoundException e){
            System.out.println("Datenbankfehler: "+ e.getMessage());
        }

    }
}