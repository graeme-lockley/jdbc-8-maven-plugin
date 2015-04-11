package za.co.no9.jdbcdry.drivers;

import org.apache.commons.lang3.StringUtils;
import za.co.no9.jdbcdry.tools.FieldMetaData;
import za.co.no9.jdbcdry.util.IntegerUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

public class H2 extends DBDriverParent {
    @Override
    protected FieldMetaData fromColumnsResultSet(Set<String> primaryKeys, ResultSet resultSet) throws SQLException {
        String fieldName = resultSet.getString(4);
        String fieldType = resultSet.getString(6);
        Optional<Integer> columnSize = IntegerUtils.parseInt(resultSet.getString(7));
        Optional<Integer> subWidth = IntegerUtils.parseInt(resultSet.getString(9));
        boolean nullable = Integer.parseInt(resultSet.getString(11)) == 1;
        boolean isAutoIncrement = StringUtils.equalsIgnoreCase("YES", resultSet.getString(23));

        return new FieldMetaData(fieldName, fieldType, columnSize, subWidth, nullable, primaryKeys.contains(fieldName), isAutoIncrement);
    }
}
