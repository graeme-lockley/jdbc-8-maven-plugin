package za.co.no9.jdbcdry.model;

import za.co.no9.jdbcdry.adaptor.DBDriver;
import za.co.no9.jdbcdry.adaptor.Oracle;
import za.co.no9.jdbcdry.port.jsqldslmojo.Configuration;

import java.io.File;

public class DatabaseMetaDataTest {
    //    @Test
    public void should_access_all_tables_within_oracle() throws Exception {
        Configuration configuration = Configuration.from(new File("src/test/resources/oracle-jsqldsl.xml"));

        DBDriver dbDriver = new Oracle();
        dbDriver.setConfiguration(configuration, configuration.establishJDBCConnection());

        DatabaseMetaData dbMetaData = DatabaseMetaData.from(dbDriver);

        dbMetaData.allTables().forEach(table -> {
            System.out.println(table.tableName());
            System.out.println(table.fields());
            System.out.println(table.foreignKeys());
        });
    }
}