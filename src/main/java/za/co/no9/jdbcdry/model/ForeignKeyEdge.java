package za.co.no9.jdbcdry.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ForeignKeyEdge {
    private final Optional<String> name;
    private final TableName tableName;
    private final Collection<FieldMetaData> columns;

    private ForeignKeyEdge(Optional<String> name, TableName tableName, Collection<FieldMetaData> columns) {
        this.name = name;
        this.tableName = tableName;
        this.columns = columns;
    }

    public static ForeignKeyEdge from(Optional<String> name, TableName tableName, Stream<FieldMetaData> columnNames) {
        return new ForeignKeyEdge(name, tableName, columnNames.collect(Collectors.toList()));
    }

    public ForeignKeyEdge addColumn(FieldMetaData column) {
        List<FieldMetaData> newColumns = new ArrayList<>(columns);
        newColumns.add(column);

        return new ForeignKeyEdge(name, tableName, newColumns);
    }

    public Stream<FieldMetaData> columns() {
        return columns.stream();
    }

    public Optional<String> name() {
        return name;
    }

    public TableName tableName() {
        return tableName;
    }

    public String columnNames(String separator) {
        return columns().map(FieldMetaData::name).collect(Collectors.joining(separator));
    }
}
