package za.co.no9.jdbcdry.model;

import za.co.no9.jdbcdry.util.ListUtils;

import java.util.*;

public class TableMetaData {
    private final TableName tableName;
    private final List<FieldMetaData> fieldsMetaData;
    private final Optional<Collection<ForeignKey>> constraints;

    public TableMetaData(TableName tableName, Iterable<FieldMetaData> fieldsMetaData, Iterable<ForeignKey> constraints) {
        this.tableName = tableName;
        this.fieldsMetaData = ListUtils.fromIterable(fieldsMetaData);
        this.constraints = Optional.of(ListUtils.fromIterable(constraints));
    }

    public TableMetaData(TableName tableName, Iterable<FieldMetaData> fieldsMetaData) {
        this.tableName = tableName;
        this.fieldsMetaData = ListUtils.fromIterable(fieldsMetaData);
        this.constraints = Optional.empty();
    }

    public TableMetaData constraints(Collection<ForeignKey> constraints) {
        return new TableMetaData(tableName, fieldsMetaData, constraints);
    }

    public TableName tableName() {
        return tableName;
    }

    public Set<FieldMetaData> primaryKeyFieldNames() {
        Set<FieldMetaData> result = new HashSet<>();

        for (FieldMetaData fieldMetaData : fieldsMetaData) {
            if (fieldMetaData.isPrimaryKey()) {
                result.add(fieldMetaData);
            }
        }

        return result;
    }

    public Set<FieldMetaData> autoIncrementFieldNames() {
        Set<FieldMetaData> result = new HashSet<>();

        for (FieldMetaData fieldMetaData : fieldsMetaData) {
            if (fieldMetaData.isAutoIncrement()) {
                result.add(fieldMetaData);
            }
        }

        return result;
    }

    public Iterable<FieldMetaData> fields() {
        return fieldsMetaData;
    }

    public Iterable<ForeignKey> foreignKeys() {
        return constraints.get();
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
