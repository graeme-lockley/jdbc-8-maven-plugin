package za.co.no9.jdbcdry.tools;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class TableName {
    private final Optional<String> catalog;
    private final Optional<String> schema;
    private final String name;

    private TableName(Optional<String> catalog, Optional<String> schema, String name) {
        this.catalog = catalog;
        this.schema = schema;
        this.name = name;
    }

    public Optional<String> catalog() {
        return catalog;
    }

    public Optional<String> schema() {
        return schema;
    }

    public String dbName() {
        return StringUtils.upperCase(name);
    }

    public static TableName from(String catalog, String schema, String name) {
        return new TableName(Optional.ofNullable(catalog), Optional.ofNullable(schema), name);
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return catalog.orElse("") + "." + schema.orElse("") + "." + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableName tableName = (TableName) o;

        return !(catalog != null ? !catalog.equals(tableName.catalog) : tableName.catalog != null) && !(schema != null ? !schema.equals(tableName.schema) : tableName.schema != null) && !(name != null ? !name.equals(tableName.name) : tableName.name != null);
    }

    @Override
    public int hashCode() {
        int result = catalog != null ? catalog.hashCode() : 0;
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
