package za.co.no9.jdbcdry.drivers;

import za.co.no9.jdbcdry.tools.DatabaseMetaData;
import za.co.no9.jdbcdry.tools.TableMetaData;
import za.co.no9.jdbcdry.tools.TableName;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public interface DBDriver {
    DatabaseMetaData databaseMetaData(Connection connection);

    TableMetaData tableMetaData(Connection connection, TableName tableName) throws SQLException;

    TableMetaData resolveForeignConstraints(Connection connection, Map<TableName, TableMetaData> tables, TableMetaData tableMetaData) throws SQLException;
}
