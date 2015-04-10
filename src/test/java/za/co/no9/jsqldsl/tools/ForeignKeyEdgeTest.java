package za.co.no9.jsqldsl.tools;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class ForeignKeyEdgeTest {
    @Test
    public void should_format_the_column_names() {
        ForeignKeyEdge foreignKeyEdge = ForeignKeyEdge.from(Optional.of("BOOKS_FK2_INDEX_4"), TableName.from("UNNAMED", "PUBLIC", "AUTHORS"), Arrays.asList(fieldMetaData("ID"), fieldMetaData("SURNAME")));

        assertEquals("ID, SURNAME", foreignKeyEdge.columnNames(", "));
    }

    private FieldMetaData fieldMetaData(String name) {
        return new FieldMetaData(name, "", Optional.<Integer>empty(), Optional.<Integer>empty(), false, false, false);
    }
}