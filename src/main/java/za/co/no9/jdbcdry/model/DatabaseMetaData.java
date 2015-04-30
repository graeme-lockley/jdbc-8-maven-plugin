package za.co.no9.jdbcdry.model;

import za.co.no9.jdbcdry.drivers.DBDriver;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DatabaseMetaData {
    private Map<TableName, TableMetaData> tables = new HashMap<>();

    public DatabaseMetaData() {
    }

    public static DatabaseMetaData from(DBDriver dbDriver) throws SQLException {
        return dbDriver.databaseMetaData();
    }

    public Stream<TableMetaData> allTables() throws SQLException {
        return tables.values().stream();
    }

    public void addTable(TableMetaData table) {
        tables.put(table.tableName(), table);
    }

    public TableMetaData get(TableName tableName) {
        return tables.get(tableName);
    }
}
