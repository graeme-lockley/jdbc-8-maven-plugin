package za.co.no9.jdbcdry.adaptor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import za.co.no9.jfixture.Fixtures;
import za.co.no9.jfixture.FixturesInput;
import za.co.no9.jfixture.JDBCHandler;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class H2Test {
    private static Connection connection;

    @BeforeClass
    public static void beforeClass() throws Exception {
        Fixtures fixtures = Fixtures.load(FixturesInput.fromResources("foreign-key.yaml"));
        fixtures.processFixtures();
        connection = fixtures.findHandler(JDBCHandler.class).get().connection();
        connection.commit();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        connection.close();
    }

    @Test
    public void should_select_rows() throws Exception {
        H2 h2 = new H2();

        h2.setConfiguration(null, connection);

        List<List<Object>> query = h2.query("select id, first_name, surname from authors order by surname");
        assertEquals(2, query.size());

        assertEquals(2L, query.get(0).get(0));
        assertEquals("Cressida", query.get(0).get(1));
        assertEquals("Cowell", query.get(0).get(2));

        assertEquals(1L, query.get(1).get(0));
        assertEquals("John Ronald Reuel", query.get(1).get(1));
        assertEquals("Tolkien", query.get(1).get(2));
    }
}

