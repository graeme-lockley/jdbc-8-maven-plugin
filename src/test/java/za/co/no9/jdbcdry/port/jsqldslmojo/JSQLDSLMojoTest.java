package za.co.no9.jdbcdry.port.jsqldslmojo;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import za.co.no9.jdbcdry.drivers.DBDriver;
import za.co.no9.jdbcdry.drivers.H2;
import za.co.no9.jdbcdry.tools.DatabaseMetaData;
import za.co.no9.jdbcdry.tools.ForeignKey;
import za.co.no9.jdbcdry.tools.HandlerTargetParent;
import za.co.no9.jdbcdry.tools.TableMetaData;
import za.co.no9.jfixture.FixtureException;
import za.co.no9.jfixture.Fixtures;
import za.co.no9.jfixture.FixturesInput;
import za.co.no9.jfixture.JDBCHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class JSQLDSLMojoTest {
    @Test
    public void should_generate_all_tables_off_of_the_shop_schema() throws Exception {
        Connection connection = loadFixtures("shop.yaml");

        new JSQLDSLMojo().processConfiguration(new za.co.no9.jdbcdry.port.jsqldslmojo.Configuration(getResource("/valid-jsqldsl.xml")) {
            @Override
            public Connection getJDBCConnection() throws SQLException {
                return connection;
            }
        });
    }

    @Test
    public void should_be_able_to_populate_manual_associations() throws Exception {
        Connection connection = loadFixtures("tables-without-fk.yaml");
        DBDriver dbDriver = dbDriver(connection, "/valid-jsqldsl-manual-relationships.xml");

        DatabaseMetaData databaseMetaData = dbDriver.databaseMetaData(connection);
        TableMetaData book = databaseMetaData.allTables().stream().filter(x -> x.tableName().name().equals("BOOKS")).findFirst().get();

        assertEquals(1, book.foreignKeys().length);
        ForeignKey foreignKey = book.foreignKeys()[0];

        assertEquals("BOOKS", foreignKey.pkTableName().dbName());
        assertEquals("AUTHOR_ID", foreignKey.pkColumnNames(","));
        assertEquals("AUTHORS", foreignKey.fkTableName().dbName());
        assertEquals("ID", foreignKey.fkColumnNames(","));
    }

    private DBDriver dbDriver(final Connection connection, String configurationFileName) throws Exception {
        Configuration configuration = new Configuration(getResource(configurationFileName)) {
            @Override
            public Connection getJDBCConnection() throws SQLException {
                return connection;
            }
        };

        Target target = Target.from(configuration, null);
        HandlerTargetParent handlerTarget = new HandlerTargetParent(target) {
            @Override
            public DBDriver getDBDriver() throws ConfigurationException {
                H2 driver = new H2();
                driver.setConfiguration(configuration);
                return driver;
            }
        };

        return handlerTarget.getDBDriver();
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