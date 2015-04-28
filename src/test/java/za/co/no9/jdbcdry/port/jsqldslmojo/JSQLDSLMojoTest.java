package za.co.no9.jdbcdry.port.jsqldslmojo;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import za.co.no9.jfixture.FixtureException;
import za.co.no9.jfixture.Fixtures;
import za.co.no9.jfixture.FixturesInput;
import za.co.no9.jfixture.JDBCHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

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

        Configuration configuration = new Configuration(getResource("/valid-jsqldsl-manual-relationships.xml")) {
            @Override
            public Connection getJDBCConnection() throws SQLException {
                return connection;
            }
        };
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