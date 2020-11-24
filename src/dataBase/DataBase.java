package dataBase;

import javax.swing.*;
import java.sql.*;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataBase {
	double f = 100.0;
	
    public DataBase() {
        createTable();

    }
    public void createTable() {
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                try {
                    conn = DriverManager.getConnection(
                            "jdbc:h2:./data/SimulationLogs","sa", "");
                    Statement statement = conn.createStatement();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void saveData(Vector<Integer> bodyMassVector, Vector<Integer> bodyIDListVector, Vector<Double> xPosVector,
                         Vector<Double> yPosVector, Vector<Double> vXVector, Vector<Double> vYVector) {
        String tableName = JOptionPane.showInputDialog(null,
                "Podaj nazwe podjaka chcesz zapisac dane z symulacji.\n" +
                        "Podaj jedynie jeden ciag znakow,\n" +
                        "bez polskich znakow i znakow specjalnych", null);

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                try {
                    conn = DriverManager.getConnection(
                            "jdbc:h2:./data/SimulationLogs","sa", "");

                    PreparedStatement deleteOldTable = conn.prepareStatement(
                        String.format("DROP TABLE IF EXISTS %s", tableName)
                    );
                    deleteOldTable.executeUpdate();

                    PreparedStatement createNewTable = conn.prepareStatement(
                            String.format(
                                    "CREATE TABLE %s (ID int(10) unsigned NOT NULL auto_increment," +
                                            "bodyID int(10) default NULL," +
                                            "mass int(6) default NULL," +
                                            "xPos double default NULL," +
                                            "yPos double default NULL," +
                                            "vX double default NULL," +
                                            "vY double default NULL," +
                                            "PRIMARY KEY (ID));", tableName
                            )
                    );
                    createNewTable.executeUpdate();

                    for (int i = 0; i < bodyIDListVector.size(); i++) {
                        PreparedStatement insertIntoStatement = conn.prepareStatement(
                                String.format("INSERT into %s(bodyID, mass, xPos, yPos, vX, vY) values " +
                                        "(?, ?, ?, ?, ?, ?);", tableName));
                        insertIntoStatement.setInt(1, bodyIDListVector.get(i));
                        insertIntoStatement.setInt(2, bodyMassVector.get(i));
                        insertIntoStatement.setDouble(3, Math.round(xPosVector.get(i)*f)/f );
                        insertIntoStatement.setDouble(4, Math.round(yPosVector.get(i)*f)/f );
                        insertIntoStatement.setDouble(5, Math.round(vXVector.get(i)*f)/f );
                        insertIntoStatement.setDouble(6, Math.round(vXVector.get(i)*f)/f );
                        insertIntoStatement.executeUpdate();
                    }

                } catch (SQLException ex) {
                    ex.getSQLState();
                }
            }
        });
        service.shutdown();
    }

    public void loadData() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                try {
                    conn = DriverManager.getConnection(
                            "jdbc:h2:./data/SimulationLogs","sa", ""
                    );

                    String tableName = getTabletoLoad(conn);

                    PreparedStatement statement = conn.prepareStatement(String.format(
                            "SELECT * FROM %s", tableName
                    ));
                    statement.execute();

                    ResultSet resultSet = statement.getResultSet();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    
                    for (int ii = 1; ii <= metaData.getColumnCount(); ii++){
                    	
                        System.out.print(metaData.getColumnName(ii)+ " | ");
                    }
                    System.out.println();

                    while (resultSet.next()) {
                        for (int ii = 1; ii <= metaData.getColumnCount(); ii++){
                            System.out.print( resultSet.getObject(ii) + " | ");
                        }
                        System.out.println();
                    }

                }catch (SQLException ex) {
                    ex.getSQLState();
                }
            }
        });
    }

    private String getTabletoLoad(Connection conn) {

        Vector<String> tableNames = new Vector<>();

        try {

            PreparedStatement showTables = conn.prepareStatement("SHOW TABLES");
            showTables.execute();

            ResultSet resultSet = showTables.getResultSet();

            while (resultSet.next()) {
                    tableNames.add(resultSet.getObject(1).toString());
            }

        }catch (SQLException ex) {
            ex.getSQLState();
        }

        Object[] stringTableNames = tableNames.toArray();

        String s = (String)JOptionPane.showInputDialog(
                null,
                "Wybierz tabele, z ktorej chcesz wczytac dane:",
                "Wybor tabeli",
                JOptionPane.PLAIN_MESSAGE,
                null,
                stringTableNames,
                stringTableNames[0]);

        return s;

    }
}
