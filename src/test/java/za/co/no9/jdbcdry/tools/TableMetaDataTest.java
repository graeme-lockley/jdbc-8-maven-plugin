package za.co.no9.jdbcdry.tools;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import za.co.no9.jdbcdry.drivers.H2;
import za.co.no9.jdbcdry.port.jsqldslmojo.TableFilter;
import za.co.no9.jdbcdry.port.jsqldslmojo.configuration.TablePatternType;
import za.co.no9.jdbcdry.util.ListUtils;
import za.co.no9.jfixture.Fixtures;
import za.co.no9.jfixture.FixturesInput;
import za.co.no9.jfixture.JDBCHandler;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TableMetaDataTest {
    private static Connection connection;

    private final ForeignKey BOOKS_FK1 = ForeignKey.from(
            ForeignKeyEdge.from(Optional.of("BOOKS_FK1_INDEX_4"), TableName.from("UNNAMED", "PUBLIC", "AUTHORS"), Arrays.asList(fieldMetaData("ID"), fieldMetaData("FIRST_NAME"))),
            ForeignKeyEdge.from(Optional.of("BOOKS_FK1"), TableName.from("UNNAMED", "PUBLIC", "BOOKS"), Arrays.asList(fieldMetaData("AUTHOR_ID"), fieldMetaData("NAME"))));
    private final ForeignKey BOOKS_FK2 = ForeignKey.from(
            ForeignKeyEdge.from(Optional.of("BOOKS_FK2_INDEX_4"), TableName.from("UNNAMED", "PUBLIC", "AUTHORS"), Arrays.asList(fieldMetaData("ID"), fieldMetaData("SURNAME"))),
            ForeignKeyEdge.from(Optional.of("BOOKS_FK2"), TableName.from("UNNAMED", "PUBLIC", "BOOKS"), Arrays.asList(fieldMetaData("AUTHOR_ID"), fieldMetaData("NAME"))));

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

    @After
    public void after() throws Exception {
        connection.rollback();
    }

    @Test
    public void should_list_all_tables() throws Exception {
        TableFilter tableFilter = new TableFilter(Collections.EMPTY_LIST, Collections.EMPTY_LIST);

        List<TableMetaData> tableMetaDatas = DatabaseMetaData.from(getDbDriver()).allTables()
                .filter(tableFilter::filter)
                .collect(Collectors.toList());
        assertEquals(4, tableMetaDatas.size());
    }

    private H2 getDbDriver() {
        return new H2() {
            @Override
            public Optional<String> getDBCatalogue() {
                return Optional.empty();
            }

            @Override
            public Optional<String> getDBSchemaPattern() {
                return Optional.of("PUBLIC");
            }

            @Override
            public Optional<String> getDBTablePattern() {
                return Optional.empty();
            }

            @Override
            protected void resolveManualForeignConstraints(List<ForeignKey> foreignKeys, Map<TableName, TableMetaData> tables, TableMetaData tableMetaData) {
            }

            @Override
            public Connection getConnection() {
                return connection;
            }
        };
    }

    @Test
    public void should_confirm_books_content() throws Exception {
        TablePatternType includeType = new TablePatternType();
        includeType.setSchema("PUBLIC");
        includeType.setTable("BOOKS");
        TableFilter tableFilter = new TableFilter(Collections.singletonList(includeType), Collections.EMPTY_LIST);

        Optional<TableMetaData> optBookMetaData = DatabaseMetaData.from(getDbDriver()).allTables()
                .filter(tableFilter::filter)
                .findFirst();
        assertTrue(optBookMetaData.isPresent());

        TableMetaData bookMetaData = optBookMetaData.get();
        assertEquals("BOOKS", bookMetaData.tableName().name());


        List<ForeignKey> foreignKeys = ListUtils.fromIterable(bookMetaData.foreignKeys());
        assertEquals(2, foreignKeys.size());
        assertConstraint(BOOKS_FK1, foreignKeys.get(0));
        assertConstraint(BOOKS_FK2, foreignKeys.get(1));
    }

    @Test
    public void should_confirm_that_foreign_key_column_names_are_formatted() throws Exception {
        assertEquals("AUTHOR_ID:NAME", BOOKS_FK1.fkColumnNames(":"));
        assertEquals("ID, FIRST_NAME", BOOKS_FK1.pkColumnNames(", "));
    }


    private void assertConstraint(ForeignKey expected, ForeignKey actual) {
        assertEquals(expected.fkName(), actual.fkName());
        assertEquals(expected.pkName(), actual.pkName());
        assertEquals(expected.pkTableName(), actual.pkTableName());
        assertEquals(expected.pkColumns(), actual.pkColumns());
        assertEquals(expected.fkTableName(), actual.fkTableName());
        assertEquals(expected.fkColumns(), actual.fkColumns());
    }

    private FieldMetaData fieldMetaData(String name) {
        return new FieldMetaData(name, "", Optional.<Integer>empty(), Optional.<Integer>empty(), false, false, false);
    }
}