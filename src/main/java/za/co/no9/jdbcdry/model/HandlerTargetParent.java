package za.co.no9.jdbcdry.model;

import za.co.no9.jdbcdry.drivers.DBDriver;
import za.co.no9.jdbcdry.port.jsqldslmojo.ConfigurationException;
import za.co.no9.jdbcdry.port.jsqldslmojo.Target;

import java.io.File;
import java.sql.Connection;

public abstract class HandlerTargetParent {
    protected final Target target;

    protected HandlerTargetParent(Target target) {
        this.target = target;
    }

    public File generatorTargetRoot() {
        return new File(getConfigurationParentFile(), target.getDestination());
    }

    private File getConfigurationParentFile() {
        return target.getConfigurationParentFile();
    }

    public DBDriver getDBDriver(Connection connection) throws ConfigurationException {
        try {
            DBDriver dbDriver = (DBDriver) Class.forName(getDriverClassName()).newInstance();
            dbDriver.setConfiguration(target.getConfiguration(), connection);
            return dbDriver;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            throw new ConfigurationException("Unable to instantiate the DB handler " + getDriverClassName() + ".", ex);
        } catch (IllegalArgumentException ex) {
            throw new ConfigurationException("Unable to instantiate the DB handler as no driver property.", ex);
        }
    }

    private String getDriverClassName() {
        return target.getProperty("driver").orElseThrow(() -> new IllegalArgumentException("No property 'driver'."));
    }
}
