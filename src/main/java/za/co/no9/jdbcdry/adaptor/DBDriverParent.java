package za.co.no9.jdbcdry.adaptor;

import za.co.no9.jdbcdry.model.*;
import za.co.no9.jdbcdry.port.jsqldslmojo.Configuration;
import za.co.no9.jdbcdry.port.jsqldslmojo.configuration.ForeignKeyType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DBDriverParent implements DBDriver {
    private Configuration configuration;
    private Connection connection;

    @Override
    public void setConfiguration(Configuration configuration, Connection connection) {
        this.configuration = configuration;
        this.connection = connection;
    }

    private void allTablesDictionary(DatabaseMetaData databaseMetaData) throws SQLException {
        java.sql.DatabaseMetaData metaData = getConnection().getMetaData();
        try (ResultSet rs = metaData.getTables(getDBCatalogue().orElse(null), getDBSchemaPattern().orElse(null), getDBTablePattern().orElse(null), null)) {
            while (rs.next()) {
                TableMetaData tableMetaData = tableMetaData(TableName.from(rs.getString(1), rs.getString(2), rs.getString(3)));
                databaseMetaData.addTable(tableMetaData);
            }
        }
    }

    private void resolveForeignKeyConstraints(DatabaseMetaData databaseMetaData) throws SQLException {
        for (TableMetaData tableMetaData : databaseMetaData.allTables().collect(Collectors.toList())) {
            databaseMetaData.addTable(resolveForeignConstraints(databaseMetaData, tableMetaData));
        }
    }

    @Override
    public DatabaseMetaData databaseMetaData() throws SQLException {
        DatabaseMetaData databaseMetaData = new DatabaseMetaData();

        allTablesDictionary(databaseMetaData);
        resolveForeignKeyConstraints(databaseMetaData);

        return databaseMetaData;
    }

    private TableMetaData tableMetaData(TableName tableName) throws SQLException {
        return new TableMetaData(tableName, fields(tableName, primaryKey(tableName)).stream());
    }

    private TableMetaData resolveForeignConstraints(DatabaseMetaData databaseMetaData, TableMetaData tableMetaData) throws SQLException {
        java.sql.DatabaseMetaData dbm = getConnection().getMetaData();

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
                FieldMetaData pkColumn = resolveField(databaseMetaData, pkTableName, PKCOLUMN_NAME);
                FieldMetaData fkColumn = resolveField(databaseMetaData, fkTableName, FKCOLUMN_NAME);
                if (KEY_SEQ == 1) {
                    result.add(ForeignKey.from(
                            ForeignKeyEdge.from(Optional.ofNullable(PK_NAME), pkTableName, Stream.of(pkColumn)),
                            ForeignKeyEdge.from(Optional.ofNullable(FK_NAME), fkTableName, Stream.of(fkColumn))));
                } else {
                    result.set(result.size() - 1, result.get(result.size() - 1).addField(pkColumn, fkColumn));
                }
            }
        }

        resolveManualForeignConstraints(result, databaseMetaData, tableMetaData);

        return tableMetaData.constraints(result.stream());
    }

    protected void resolveManualForeignConstraints(List<ForeignKey> foreignKeys, DatabaseMetaData databaseMetaData, TableMetaData tableMetaData) {
        foreignKeys.addAll(configuration.getManualForeignKeys()
                .filter(x -> namesEqual(x.getFromTable(), tableMetaData.tableName().toString()))
                .map(foreignKeyType -> resolveForeignKey(databaseMetaData, foreignKeyType))
                .collect(Collectors.toList()));
    }

    private boolean namesEqual(String filterFromName, String tableName) {
        return filterFromName.equals(tableName);
    }

    private ForeignKey resolveForeignKey(DatabaseMetaData databaseMetaData, ForeignKeyType foreignKeyType) {
        String[] fromKeys = foreignKeyType.getFromFields().split(",");
        String[] toKeys = foreignKeyType.getToFields().split(",");

        if (fromKeys.length != toKeys.length) {
            throw new IllegalArgumentException("Manual foreign keys do not have the same number of fields: " + foreignKeyType.toString());
        }

        return ForeignKey.from(
                resolveForeignKeyEdge(
                        foreignKeyType.getToName(),
                        databaseMetaData,
                        foreignKeyType.getToTable(),
                        toKeys),
                resolveForeignKeyEdge(
                        foreignKeyType.getFromName(),
                        databaseMetaData,
                        foreignKeyType.getFromTable(),
                        fromKeys));
    }

    private ForeignKeyEdge resolveForeignKeyEdge(String name, DatabaseMetaData databaseMetaData, String qualifiedTableName, String[] fieldNames) {
        TableName tableName = TableName.from(qualifiedTableName);
        return ForeignKeyEdge.from(
                Optional.ofNullable(name),
                tableName,
                resolveFields(databaseMetaData, tableName, fieldNames).stream());
    }

    private Collection<FieldMetaData> resolveFields(DatabaseMetaData databaseMetaData, TableName tableName, String[] fieldNames) {
        return Stream.of(fieldNames).map(x -> resolveField(databaseMetaData, tableName, x)).collect(Collectors.<FieldMetaData>toList());
    }

    private FieldMetaData resolveField(DatabaseMetaData databaseMetaData, TableName tableName, String fieldName) {
        return databaseMetaData.get(tableName).field(fieldName).get();
    }

    private Set<String> primaryKey(TableName tableName) throws SQLException {
        java.sql.DatabaseMetaData dbm = getConnection().getMetaData();

        Set<String> primaryKey = new HashSet<>();
        try (ResultSet resultSet = dbm.getPrimaryKeys("", "", tableName.dbName())) {
            while (resultSet.next()) {
                primaryKey.add(resultSet.getString(4));
            }
        }

        return primaryKey;
    }

    private Collection<FieldMetaData> fields(TableName tableName, Set<String> primaryKeys) throws SQLException {
        java.sql.DatabaseMetaData dbm = getConnection().getMetaData();

        List<FieldMetaData> fields = new ArrayList<>();
        try (ResultSet resultSet = dbm.getColumns(tableName.catalog().orElse(""), tableName.schema().orElse(""), tableName.dbName(), null)) {
            while (resultSet.next()) {
                fields.add(fromColumnsResultSet(primaryKeys, resultSet));
            }
        }

        return fields;
    }

    protected abstract FieldMetaData fromColumnsResultSet(Set<String> primaryKeys, ResultSet resultSet) throws SQLException;

    protected Connection getConnection() {
        return connection;
    }

    protected Optional<String> getDBCatalogue() {
        return configuration.getDBCatalogue();
    }

    protected Optional<String> getDBSchemaPattern() {
        return configuration.getDBSchemaPattern();
    }

    protected Optional<String> getDBTablePattern() {
        return configuration.getDBTablePattern();
    }

    @Override
    public List<List<Object>> query(String query) throws SQLException {
        List<List<Object>> results = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            int numberOfColumns = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                List<Object> row = new ArrayList<>(numberOfColumns);
                for (int column = 1; column <= numberOfColumns; column += 1) {
                    row.add(resultSet.getObject(column));
                }
                results.add(row);
            }
        }

        return results;
    }
}
