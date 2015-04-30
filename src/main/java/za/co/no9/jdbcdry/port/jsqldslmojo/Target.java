package za.co.no9.jdbcdry.port.jsqldslmojo;

import org.apache.maven.plugin.logging.Log;
import za.co.no9.jdbcdry.model.ToolHandler;
import za.co.no9.jdbcdry.port.jsqldslmojo.configuration.PropertyType;
import za.co.no9.jdbcdry.port.jsqldslmojo.configuration.TargetType;

import java.io.File;
import java.util.Optional;

public final class Target {
    private final Configuration configuration;
    private final TargetType targetType;

    private Target(Configuration configuration, TargetType targetType) {
        this.configuration = configuration;
        this.targetType = targetType;
    }

    public static Target from(Configuration configuration, TargetType targetType) {
        return new Target(configuration, targetType);
    }

    public ToolHandler getToolHandler(Log log) throws ConfigurationException {
        try {
            ToolHandler toolHandler = (ToolHandler) Class.forName(targetType.getHandler()).newInstance();
            toolHandler.setup(log, this);
            return toolHandler;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            throw new ConfigurationException("Unable to create the tool handler associated " + targetType.getHandler() + ".", ex);
        }
    }

    public File getConfigurationParentFile() {
        return configuration.getParentFile();
    }

    public String getDestination() {
        return targetType.getDestination();
    }

    public Optional<String> getProperty(String name) {
        for (PropertyType propertyType : targetType.getProperties().getProperty()) {
            if (propertyType.getName().equals(name)) {
                return Optional.of(propertyType.getValue());
            }
        }
        return Optional.empty();
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
