package za.co.no9.jdbcdry.tools;

import java.util.*;

public class TableMetaData {
    private final TableName tableName;
    private final FieldMetaData[] fieldsMetaData;
    private final Optional<Collection<ForeignKey>> constraints;

    public TableMetaData(TableName tableName, FieldMetaData[] fieldsMetaData, Collection<ForeignKey> constraints) {
        this.tableName = tableName;
        this.fieldsMetaData = fieldsMetaData;
        this.constraints = Optional.of(new ArrayList<>(constraints));
    }

    public TableMetaData(TableName tableName, FieldMetaData[] fieldsMetaData) {
        this.tableName = tableName;
        this.fieldsMetaData = fieldsMetaData;
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

    public List<FieldMetaData> fields() {
        return Arrays.asList(fieldsMetaData);
    }

    public ForeignKey[] foreignKeys() {
        return constraints.get().toArray(new ForeignKey[constraints.get().size()]);
    }

    public Optional<FieldMetaData> field(String fieldName) {
        for (FieldMetaData field : fieldsMetaData) {
            if (field.name().equals(fieldName)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }
}
