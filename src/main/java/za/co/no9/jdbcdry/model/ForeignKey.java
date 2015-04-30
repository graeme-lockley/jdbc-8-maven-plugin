package za.co.no9.jdbcdry.model;

import java.util.Optional;

public final class ForeignKey {
    private final ForeignKeyEdge primaryEdge;
    private final ForeignKeyEdge foreignEdge;

    private ForeignKey(ForeignKeyEdge primaryEdge, ForeignKeyEdge foreignEdge) {
        this.primaryEdge = primaryEdge;
        this.foreignEdge = foreignEdge;
    }

    public static ForeignKey from(ForeignKeyEdge primaryEdge, ForeignKeyEdge foreignEdge) {
        return new ForeignKey(primaryEdge, foreignEdge);
    }

    public Optional<String> pkName() {
        return primaryEdge.name();
    }

    public Optional<String> fkName() {
        return foreignEdge.name();
    }

    public TableName fkTableName() {
        return foreignEdge.tableName();
    }

    public TableName pkTableName() {
        return primaryEdge.tableName();
    }

    public ForeignKey addField(FieldMetaData pkColumn, FieldMetaData fkColumn) {
        return new ForeignKey(primaryEdge.addColumn(pkColumn), foreignEdge.addColumn(fkColumn));
    }

    public Iterable<FieldMetaData> pkColumns() {
        return primaryEdge.columns();
    }

    public String pkColumnNames(String separator) {
        return primaryEdge.columnNames(separator);
    }

    public Iterable<FieldMetaData> fkColumns() {
        return foreignEdge.columns();
    }

    public String fkColumnNames(String separator) {
        return foreignEdge.columnNames(separator);
    }
}

