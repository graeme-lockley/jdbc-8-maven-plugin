package za.co.no9.jdbcdry.adaptor;

import za.co.no9.jdbcdry.model.DatabaseMetaData;
import za.co.no9.jdbcdry.model.TableMetaData;
import za.co.no9.jdbcdry.model.TableName;
import za.co.no9.jdbcdry.port.jsqldslmojo.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBDriver {
    void setConfiguration(Configuration configuration, Connection connection);

    Connection getConnection();

    DatabaseMetaData databaseMetaData() throws SQLException;

    TableMetaData tableMetaData(TableName tableName) throws SQLException;

    TableMetaData resolveForeignConstraints(DatabaseMetaData databaseMetaData, TableMetaData tableMetaData) throws SQLException;
}
