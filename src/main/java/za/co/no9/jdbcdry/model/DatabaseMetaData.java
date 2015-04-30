package za.co.no9.jdbcdry.model;

import za.co.no9.jdbcdry.drivers.DBDriver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class DatabaseMetaData {
    private DBDriver dbDriver;
    private Map<TableName, TableMetaData> tables = new HashMap<>();

    public DatabaseMetaData(DBDriver dbDriver) {
        this.dbDriver = dbDriver;
    }

    public static DatabaseMetaData from(DBDriver dbDriver) {
        return new DatabaseMetaData(dbDriver);
    }

    public Stream<TableMetaData> allTables() throws SQLException {
        return tables(dbDriver.getDBCatalogue(), dbDriver.getDBSchemaPattern(), dbDriver.getDBTablePattern());
    }

    private Stream<TableMetaData> tables(Optional<String> catalogue, Optional<String> schemaNamePattern, Optional<String> tableNamePattern) throws SQLException {
        allTablesDictionary(catalogue, schemaNamePattern, tableNamePattern);
        resolveForeignKeyConstraints(tables);
        return tables.values().stream();
    }

    private void allTablesDictionary(Optional<String> catalogue, Optional<String> schemaNamePattern, Optional<String> tableNamePattern) throws SQLException {
        java.sql.DatabaseMetaData metaData = getConnection().getMetaData();
        try (ResultSet rs = metaData.getTables(catalogue.orElse(null), schemaNamePattern.orElse(null), tableNamePattern.orElse(null), null)) {
            while (rs.next()) {
                TableMetaData tableMetaData = dbDriver.tableMetaData(TableName.from(rs.getString(1), rs.getString(2), rs.getString(3)));
                addTable(tableMetaData);
            }
        }
    }

    private void resolveForeignKeyConstraints(Map<TableName, TableMetaData> tables) throws SQLException {
        for (TableMetaData tableMetaData : tables.values()) {
            addTable(dbDriver.resolveForeignConstraints(tables, tableMetaData));
        }
    }

    private Connection getConnection() { return dbDriver.getConnection(); }

    private void addTable(TableMetaData table) {
        tables.put(table.tableName(), table);
    }
}
