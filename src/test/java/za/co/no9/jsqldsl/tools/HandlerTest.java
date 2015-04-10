package za.co.no9.jsqldsl.tools;

import org.apache.maven.plugin.logging.Log;
import za.co.no9.jsqldsl.port.jsqldslmojo.ConfigurationException;
import za.co.no9.jsqldsl.port.jsqldslmojo.TableFilter;
import za.co.no9.jsqldsl.port.jsqldslmojo.Target;

import java.sql.Connection;
import java.sql.SQLException;

public class HandlerTest implements ToolHandler {
    @Override
    public void setup(Log log, Target target) {
    }

    @Override
    public void process(Connection connection, TableFilter tableFilter) throws SQLException, ConfigurationException, GenerationException {
    }
}
