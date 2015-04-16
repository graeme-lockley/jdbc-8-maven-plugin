package za.co.no9.jdbcdry.drivers;

import za.co.no9.jdbcdry.port.jsqldslmojo.Configuration;
import za.co.no9.jdbcdry.tools.DatabaseMetaData;
import za.co.no9.jdbcdry.tools.TableMetaData;
import za.co.no9.jdbcdry.tools.TableName;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public interface DBDriver {
    void setConfiguration(Configuration configuration);

    DatabaseMetaData databaseMetaData(Connection connection);

    TableMetaData tableMetaData(Connection connection, TableName tableName) throws SQLException;

    TableMetaData resolveForeignConstraints(Connection connection, Map<TableName, TableMetaData> tables, TableMetaData tableMetaData) throws SQLException;

    Optional<String> getDBCatalogue();

    Optional<String> getDBSchemaPattern();

    Optional<String> getDBTablePattern();
}
