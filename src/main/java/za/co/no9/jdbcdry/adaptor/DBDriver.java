package za.co.no9.jdbcdry.adaptor;

import za.co.no9.jdbcdry.model.DatabaseMetaData;
import za.co.no9.jdbcdry.port.jsqldslmojo.Configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DBDriver {
    void setConfiguration(Configuration configuration, Connection connection);

    DatabaseMetaData databaseMetaData() throws SQLException;

    List<List<Object>> query(String query) throws SQLException;
}
