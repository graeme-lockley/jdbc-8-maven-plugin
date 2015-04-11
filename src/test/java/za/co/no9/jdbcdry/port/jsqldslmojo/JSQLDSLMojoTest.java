package za.co.no9.jdbcdry.port.jsqldslmojo;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
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
        Fixtures fixtures = Fixtures.load(FixturesInput.fromResources("shop.yaml"));
        fixtures.processFixtures();
        Connection connection = fixtures.findHandler(JDBCHandler.class).get().connection();

        new JSQLDSLMojo().processConfiguration(new za.co.no9.jdbcdry.port.jsqldslmojo.Configuration(getResource("/valid-jsqldsl.xml")) {
            @Override
            public Connection getJDBCConnection() throws SQLException {
                return connection;
            }
        });
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