package za.co.no9.jdbcdry.drivers;

import za.co.no9.jdbcdry.port.jsqldslmojo.Configuration;
import za.co.no9.jdbcdry.tools.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class DBDriverParent implements DBDriver {
    private Configuration configuration;

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public DatabaseMetaData databaseMetaData(Connection connection) {
        return DatabaseMetaData.from(this, connection);
    }

    @Override
    public TableMetaData tableMetaData(Connection connection, TableName tableName) throws SQLException {
        Set<String> primaryKey = primaryKey(connection, tableName);

        return new TableMetaData(tableName, fields(connection, tableName, primaryKey));
    }

    @Override
    public TableMetaData resolveForeignConstraints(Connection connection, Map<TableName, TableMetaData> tables, TableMetaData tableMetaData) throws SQLException {
        java.sql.DatabaseMetaData dbm = connection.getMetaData();

        List<ForeignKey> result = new ArrayList<>();
        try (ResultSet importedKeys = dbm.getImportedKeys(tableMetaData.tableName().catalog().orElse(null), tableMetaData.tableName().schema().orElse(null), tableMetaData.tableName().name())) {
            while (importedKeys.next()) {
                String PKTABLE_CAT = importedKeys.getString(1);
                String PKTABLE_SCHEM = importedKeys.getString(2);
                String PKTABLE_NAME = importedKeys.getString(3);
                String PKCOLUMN_NAME = importedKeys.getString(4);
                String FKTABLE_CAT = importedKeys.getString(5);
                String FKTABLE_SCHEM = importedKeys.getString(6);
                String FKTABLE_NAME = importedKeys.getString(7);
                String FKCOLUMN_NAME = importedKeys.getString(8);
                short KEY_SEQ = importedKeys.getShort(9);
                short UPDATE_RULE = importedKeys.getShort(10);
                short DELETE_RULE = importedKeys.getShort(11);
                String FK_NAME = importedKeys.getString(12);
                String PK_NAME = importedKeys.getString(13);

                TableName pkTableName = TableName.from(PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME);
                TableName fkTableName = TableName.from(FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME);
                FieldMetaData pkColumn = resolveField(tables, pkTableName, PKCOLUMN_NAME);
                FieldMetaData fkColumn = resolveField(tables, fkTableName, FKCOLUMN_NAME);
                if (KEY_SEQ == 1) {
                    result.add(ForeignKey.from(
                            ForeignKeyEdge.from(Optional.ofNullable(PK_NAME), pkTableName, Collections.singletonList(pkColumn)),
                            ForeignKeyEdge.from(Optional.ofNullable(FK_NAME), fkTableName, Collections.singletonList(fkColumn))));
                } else {
                    result.set(result.size() - 1, result.get(result.size() - 1).addField(pkColumn, fkColumn));
                }
            }
        }

        return tableMetaData.constraints(result);
    }

    private FieldMetaData resolveField(Map<TableName, TableMetaData> tables, TableName tableName, String fieldName) {
        return tables.get(tableName).field(fieldName).get();
    }

    protected Set<String> primaryKey(Connection connection, TableName tableName) throws SQLException {
        java.sql.DatabaseMetaData dbm = connection.getMetaData();

        Set<String> primaryKey = new HashSet<>();
        try (ResultSet resultSet = dbm.getPrimaryKeys("", "", tableName.dbName())) {
            while (resultSet.next()) {
                primaryKey.add(resultSet.getString(4));
            }
        }

        return primaryKey;
    }

    private FieldMetaData[] fields(Connection connection, TableName tableName, Set<String> primaryKeys) throws SQLException {
        java.sql.DatabaseMetaData dbm = connection.getMetaData();

        List<FieldMetaData> fields = new ArrayList<>();
        try (ResultSet resultSet = dbm.getColumns(tableName.catalog().orElse(""), tableName.schema().orElse(""), tableName.dbName(), null)) {
            while (resultSet.next()) {
                fields.add(fromColumnsResultSet(primaryKeys, resultSet));
            }
        }

        return fields.toArray(new FieldMetaData[1]);
    }

    protected abstract FieldMetaData fromColumnsResultSet(Set<String> primaryKeys, ResultSet resultSet) throws SQLException;

    @Override
    public Optional<String> getDBCatalogue() {
        return configuration.getDBCatalogue();
    }

    @Override
    public Optional<String> getDBSchemaPattern() {
        return configuration.getDBSchemaPattern();
    }

    @Override
    public Optional<String> getDBTablePattern() {
        return configuration.getDBTablePattern();
    }
}
