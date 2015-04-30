package za.co.no9.jdbcdry.model;

import za.co.no9.jdbcdry.drivers.DBDriver;
import za.co.no9.jdbcdry.drivers.Oracle;
import za.co.no9.jdbcdry.port.jsqldslmojo.Configuration;

import java.io.File;

public class DatabaseMetaDataTest {
    //    @Test
    public void should_access_all_tables_within_oracle() throws Exception {
        Configuration configuration = Configuration.from(new File("src/test/resources/oracle-jsqldsl.xml"));

        DBDriver dbDriver = new Oracle();
        dbDriver.setConfiguration(configuration, configuration.getJDBCConnection());

        DatabaseMetaData dbMetaData = DatabaseMetaData.from(dbDriver);

        dbMetaData.allTables().forEach(table -> {
            System.out.println(table.tableName());
            System.out.println(table.fields());
            System.out.println(table.foreignKeys());
        });
    }
}