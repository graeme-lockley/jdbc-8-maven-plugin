package za.co.no9.jdbcdry.port.jsqldslmojo;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class ConfigurationTest {
    @Test(expected = IOException.class)
    public void should_return_an_IOException_if_configuration_file_does_not_exist() throws Exception {
        Configuration.from(getResource("/unknown-jsqldsl.xml"));
    }

    @Test(expected = ConfigurationException.class)
    public void should_fail_with_an_UnmarshalException_if_the_XML_is_not_compliant_with_the_XSD() throws Exception {
        Configuration.from(getResource("/invalid-jsqldsl.xml"));
    }

    @Test
    public void should_return_an_empty_stream_if_there_are_no_manual_foreign_keys() throws Exception {
        Configuration configuration = Configuration.from(getResource("/valid-jsqldsl.xml"));

        assertNotNull(configuration.getManualForeignKeys());
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