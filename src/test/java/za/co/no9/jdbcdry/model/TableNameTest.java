package za.co.no9.jdbcdry.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TableNameTest {
    @Test
    public void should_accept_single_element_in_qualified_name() throws Exception {
        TableName tableName = TableName.from("BOB");

        assertFalse(tableName.catalog().isPresent());
        assertFalse(tableName.schema().isPresent());
        assertEquals("BOB", tableName.name());
        assertEquals("BOB", tableName.toString());
    }

    @Test
    public void should_accept_two_elements_in_qualified_name() throws Exception {
        TableName tableName = TableName.from("SCHEMA.BOB");

        assertFalse(tableName.catalog().isPresent());
        assertEquals("SCHEMA", tableName.schema().get());
        assertEquals("BOB", tableName.name());
        assertEquals("SCHEMA.BOB", tableName.toString());
    }

    @Test
    public void should_accept_three_elements_in_qualified_name() throws Exception {
        TableName tableName = TableName.from("CATALOG.SCHEMA.BOB");

        assertEquals("CATALOG", tableName.catalog().get());
        assertEquals("SCHEMA", tableName.schema().get());
        assertEquals("BOB", tableName.name());
        assertEquals("CATALOG.SCHEMA.BOB", tableName.toString());
    }
}