package za.co.no9.jdbcdry.port.jsqldslmojo;

import org.xml.sax.SAXException;
import za.co.no9.jdbcdry.port.jsqldslmojo.configuration.DBSearchType;
import za.co.no9.jdbcdry.port.jsqldslmojo.configuration.ForeignKeyType;
import za.co.no9.jdbcdry.port.jsqldslmojo.configuration.JdbcType;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public class Configuration {
    private static final String SCHEMA_RESOURCE_NAME = "/xsd/jdbc-8-maven-plugin-configuration.xsd";

    private File configurationFile;
    private za.co.no9.jdbcdry.port.jsqldslmojo.configuration.Configuration xmlConfiguration;

    private Configuration(File configurationFile, za.co.no9.jdbcdry.port.jsqldslmojo.configuration.Configuration xmlConfiguration) {
        this.configurationFile = configurationFile;
        this.xmlConfiguration = xmlConfiguration;
    }

    protected Configuration(File configurationFile) throws ConfigurationException, JAXBException {
        this(configurationFile, (za.co.no9.jdbcdry.port.jsqldslmojo.configuration.Configuration) getUnmarshaller().unmarshal(configurationFile));
    }

    public static Configuration from(File configurationFile) throws ConfigurationException {
        try {
            return new Configuration(configurationFile);
        } catch (JAXBException e) {
            throw new ConfigurationException("Unable to parse the configuration file: " + e.getMessage(), e);
        }
    }

    private static Unmarshaller getUnmarshaller() throws ConfigurationException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(za.co.no9.jdbcdry.port.jsqldslmojo.configuration.Configuration.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            jaxbUnmarshaller.setSchema(getSchema());
            return jaxbUnmarshaller;
        } catch (JAXBException ex) {
            throw new ConfigurationException("Unable to create an unmarshaller to parse the configuration file.", ex);
        }
    }

    private static Schema getSchema() throws ConfigurationException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            return schemaFactory.newSchema(loadSchema());
        } catch (SAXException ex) {
            throw new ConfigurationException("Unable to parse the schema " + SCHEMA_RESOURCE_NAME + ".", ex);
        }
    }

    private static URL loadSchema() {
        return JSQLDSLMojo.class.getResource(SCHEMA_RESOURCE_NAME);
    }

    public File getParentFile() {
        return configurationFile.getParentFile();
    }

    public Iterator<Target> getTargets() {
        return xmlConfiguration.getTargets().getTarget().stream().map(x -> Target.from(this, x)).iterator();
    }

    public Connection getJDBCConnection() throws SQLException {
        JdbcType jdbcType = xmlConfiguration.getSource().getJdbc();
        try {
            Class.forName(jdbcType.getDriver());
            return DriverManager.getConnection(
                    jdbcType.getUrl(),
                    jdbcType.getUsername(),
                    jdbcType.getPassword());
        } catch (ClassNotFoundException e) {
            throw new SQLException("Loading of the driver class " + jdbcType.getDriver() + " failed:" + e.getMessage(), e);
        } catch (SQLException e) {
            throw new SQLException("Unable to connect: " + e.getMessage(), e);
        }
    }

    public TableFilter getTableFilter() {
        return new TableFilter(xmlConfiguration.getSource().getTables().getInclude(), xmlConfiguration.getSource().getTables().getExclude());
    }

    public Optional<String> getDBCatalogue() {
        return Optional.ofNullable(getDbSearch() == null ? null : getDbSearch().getCatalog());
    }

    public Optional<String> getDBSchemaPattern() {
        return Optional.ofNullable(getDbSearch() == null ? null : getDbSearch().getSchemaPattern());
    }

    public Optional<String> getDBTablePattern() {
        return Optional.ofNullable(getDbSearch() == null ? null : getDbSearch().getTablePattern());
    }

    private DBSearchType getDbSearch() {
        return xmlConfiguration.getSource().getTables().getDbSearch();
    }

    public Stream<ForeignKeyType> getManualForeignKeys() {
        return xmlConfiguration.getSource().getForeignKeys().getForeignKey().stream();
    }
}
