package za.co.no9.jdbcdry.port.jsqldslmojo;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import za.co.no9.jdbcdry.adaptor.DBDriver;
import za.co.no9.jdbcdry.adaptor.H2;
import za.co.no9.jdbcdry.model.DatabaseMetaData;
import za.co.no9.jdbcdry.model.ForeignKey;
import za.co.no9.jdbcdry.model.HandlerTargetParent;
import za.co.no9.jdbcdry.model.TableMetaData;
import za.co.no9.jfixture.FixtureException;
import za.co.no9.jfixture.Fixtures;
import za.co.no9.jfixture.FixturesInput;
import za.co.no9.jfixture.JDBCHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class JSQLDSLMojoTest {
    @Test
    public void should_generate_all_tables_off_of_the_shop_schema() throws Exception {
        Connection connection = loadFixtures("shop.yaml");

        new JSQLDSLMojo().processConfiguration(new za.co.no9.jdbcdry.port.jsqldslmojo.Configuration(getResource("/valid-jsqldsl.xml")) {
            @Override
            public Connection establishJDBCConnection() throws SQLException {
                return connection;
            }
        });
    }

    @Test
    public void should_be_able_to_populate_manual_associations() throws Exception {
        Connection connection = loadFixtures("tables-without-fk.yaml");
        DBDriver dbDriver = dbDriver(connection, "/valid-jsqldsl-manual-relationships.xml");

        DatabaseMetaData databaseMetaData = dbDriver.databaseMetaData();
        TableMetaData book = databaseMetaData.allTables().filter(x -> x.tableName().name().equals("BOOKS")).findFirst().get();

        List<ForeignKey> bookForeignKeys = book.foreignKeys().collect(Collectors.toList());
        assertEquals(1, bookForeignKeys.size());
        ForeignKey foreignKey = bookForeignKeys.get(0);

        assertEquals("BOOKS", foreignKey.fkTableName().dbName());
        assertEquals("AUTHOR_ID", foreignKey.fkColumnNames(","));
        assertEquals("AUTHORS", foreignKey.pkTableName().dbName());
        assertEquals("ID", foreignKey.pkColumnNames(","));
    }

    private DBDriver dbDriver(final Connection connection, String configurationFileName) throws Exception {
        Configuration configuration = new Configuration(getResource(configurationFileName)) {
            @Override
            public Connection establishJDBCConnection() throws SQLException {
                return connection;
            }
        };

        Target target = Target.from(configuration, null);
        HandlerTargetParent handlerTarget = new HandlerTargetParent(target) {
            @Override
            public DBDriver getDBDriver(Connection connection) throws ConfigurationException {
                H2 driver = new H2();
                driver.setConfiguration(configuration, connection);
                return driver;
            }
        };

        return handlerTarget.getDBDriver(connection);
    }

    private Connection loadFixtures(String fixturesFileName) throws IOException, FixtureException {
        Fixtures fixtures = Fixtures.load(FixturesInput.fromResources(fixturesFileName));
        fixtures.processFixtures();
        return fixtures.findHandler(JDBCHandler.class).get().connection();
    }

    private File getResource(String resourceName) throws IOException {
        URL resource = ConfigurationTest.class.getResource(resourceName);

        if (resource == null) {
            throw new IOException("The resource " + resourceName + " could not be located.");
        } else {
            return FileUtils.toFile(resource);
        }
    }
}