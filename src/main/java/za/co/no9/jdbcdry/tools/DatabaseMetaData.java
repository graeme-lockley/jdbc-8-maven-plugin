package za.co.no9.jdbcdry.tools;

import za.co.no9.jdbcdry.drivers.DBDriver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseMetaData {
    private DBDriver dbDriver;

    public DatabaseMetaData(DBDriver dbDriver) {
        this.dbDriver = dbDriver;
    }

    public static DatabaseMetaData from(DBDriver dbDriver) {
        return new DatabaseMetaData(dbDriver);
    }

    public Collection<TableMetaData> allTables() throws SQLException {
        return tables(dbDriver.getDBCatalogue(), dbDriver.getDBSchemaPattern(), dbDriver.getDBTablePattern());
    }

    private Collection<TableMetaData> tables(Optional<String> catalogue, Optional<String> schemaNamePattern, Optional<String> tableNamePattern) throws SQLException {
        Map<TableName, TableMetaData> tables = allTablesDictionary(catalogue, schemaNamePattern, tableNamePattern);
        return resolveForeignKeyConstraints(tables);
    }

    private Map<TableName, TableMetaData> allTablesDictionary(Optional<String> catalogue, Optional<String> schemaNamePattern, Optional<String> tableNamePattern) throws SQLException {
        Map<TableName, TableMetaData> tables = new HashMap<>();

        java.sql.DatabaseMetaData metaData = getConnection().getMetaData();
        try (ResultSet rs = metaData.getTables(catalogue.orElse(null), schemaNamePattern.orElse(null), tableNamePattern.orElse(null), null)) {
            while (rs.next()) {
                TableMetaData tableMetaData = dbDriver.tableMetaData(TableName.from(rs.getString(1), rs.getString(2), rs.getString(3)));
                tables.put(tableMetaData.tableName(), tableMetaData);
            }
        }
        return tables;
    }

    private List<TableMetaData> resolveForeignKeyConstraints(Map<TableName, TableMetaData> tables) throws SQLException {
        List<TableMetaData> result = new ArrayList<>();
        for (TableMetaData tableMetaData : tables.values()) {
            result.add(dbDriver.resolveForeignConstraints(tables, tableMetaData));
        }
        return result;
    }

    private Connection getConnection() { return dbDriver.getConnection(); }
}
