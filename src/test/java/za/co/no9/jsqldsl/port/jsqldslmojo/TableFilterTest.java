package za.co.no9.jsqldsl.port.jsqldslmojo;

import org.junit.Test;
import za.co.no9.jsqldsl.port.jsqldslmojo.configuration.TablePatternType;
import za.co.no9.jsqldsl.tools.TableName;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TableFilterTest {
    @Test
    public void should_include_based_on_a_constant_table_name_regex() throws Exception {
        TableFilter tableFilter = new TableFilter(Collections.singletonList(pattern(null, "HELLO")), Collections.EMPTY_LIST);

        assertTrue(tableFilter.filter(TableName.from(null, null, "HELLO")));
        assertTrue(tableFilter.filter(TableName.from(null, "SCHEMA", "HELLO")));
    }

    @Test
    public void should_include_based_on_a_table_name_regex() throws Exception {
        TableFilter tableFilter = new TableFilter(Collections.singletonList(pattern(null, "HE.*$")), Collections.EMPTY_LIST);

        assertTrue(tableFilter.filter(TableName.from(null, null, "HELLO")));
        assertTrue(tableFilter.filter(TableName.from(null, "SCHEMA", "HELLO")));
    }

    @Test
    public void should_include_based_on_a_constant_schema_regex() throws Exception {
        TableFilter tableFilter = new TableFilter(Collections.singletonList(pattern("SCHEMA", null)), Collections.EMPTY_LIST);

        assertTrue(tableFilter.filter(TableName.from(null, "SCHEMA", "WORLD")));
    }

    @Test
    public void should_exclude_based_on_a_constant_table_name_regex() throws Exception {
        TableFilter tableFilter = new TableFilter(Collections.singletonList(pattern(null, "HELLO")), Collections.singletonList(pattern(null, "HELLO")));

        assertFalse(tableFilter.filter(TableName.from(null, null, "HELLO")));
        assertFalse(tableFilter.filter(TableName.from(null, "SCHEMA", "HELLO")));
    }

    @Test
    public void should_exclude_based_on_a_constant_schema_regex() throws Exception {
        TableFilter tableFilter = new TableFilter(Collections.singletonList(pattern("SCHEMA", null)), Collections.singletonList(pattern(null, "HELLO")));

        assertFalse(tableFilter.filter(TableName.from(null, "SCHEMA", "HELLO")));
    }

    private TablePatternType pattern(String schemaPattern, String tablePattern) {
        TablePatternType tablePatternType = new TablePatternType();
        tablePatternType.setSchema(schemaPattern);
        tablePatternType.setTable(tablePattern);
        return tablePatternType;
    }
}