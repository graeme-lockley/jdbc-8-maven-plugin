package za.co.no9.jdbcdry.tools;

import za.co.no9.jdbcdry.drivers.DBDriver;
import za.co.no9.jdbcdry.port.jsqldslmojo.ConfigurationException;
import za.co.no9.jdbcdry.port.jsqldslmojo.Target;

import java.io.File;

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

    public DBDriver getDBDriver() throws ConfigurationException {
        try {
            return (DBDriver) Class.forName(getDriverClassName()).newInstance();
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
