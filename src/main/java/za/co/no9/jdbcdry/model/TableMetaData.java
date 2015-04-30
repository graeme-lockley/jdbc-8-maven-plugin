package za.co.no9.jdbcdry.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableMetaData {
    private final TableName tableName;
    private final List<FieldMetaData> fieldsMetaData;
    private final Optional<Collection<ForeignKey>> constraints;

    public TableMetaData(TableName tableName, Stream<FieldMetaData> fieldsMetaData, Stream<ForeignKey> constraints) {
        this.tableName = tableName;
        this.fieldsMetaData = fieldsMetaData.collect(Collectors.toList());
        this.constraints = Optional.of(constraints.collect(Collectors.toList()));
    }

    public TableMetaData(TableName tableName, Stream<FieldMetaData> fieldsMetaData) {
        this.tableName = tableName;
        this.fieldsMetaData = fieldsMetaData.collect(Collectors.toList());
        this.constraints = Optional.empty();
    }

    public TableMetaData constraints(Stream<ForeignKey> constraints) {
        return new TableMetaData(tableName, fieldsMetaData.stream(), constraints);
    }

    public TableName tableName() {
        return tableName;
    }

    public Stream<FieldMetaData> primaryKeyFieldNames() {
        return fieldsMetaData.stream().filter(FieldMetaData::isPrimaryKey);
    }

    public Stream<FieldMetaData> autoIncrementFieldNames() {
        return fieldsMetaData.stream().filter(FieldMetaData::isAutoIncrement);
    }

    public Stream<FieldMetaData> fields() {
        return fieldsMetaData.stream();
    }

    public Stream<ForeignKey> foreignKeys() {
        return constraints.get().stream();
    }

    public Optional<FieldMetaData> field(String fieldName) {
        for (FieldMetaData field : fieldsMetaData) {
            if (field.name().equals(fieldName)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    public String toString() {
        return "{name: " + tableName + ", fields: " + fields() + ", foreign_keys: " + foreignKeys() + "}";
    }
}
