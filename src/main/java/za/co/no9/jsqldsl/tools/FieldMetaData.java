package za.co.no9.jsqldsl.tools;

import java.util.Optional;

public class FieldMetaData {
    private final String name;
    private final String fieldType;
    private final Optional<Integer> columnSize;
    private final Optional<Integer> subWidth;
    private final boolean isNullable;
    private final boolean isPrimaryKey;
    private final boolean isAutoIncrement;

    public FieldMetaData(String name, String fieldType, Optional<Integer> columnSize, Optional<Integer> subWidth, boolean isNullable, boolean isPrimaryKey, boolean isAutoIncrement) {
        this.name = name;
        this.fieldType = fieldType;
        this.columnSize = columnSize;
        this.subWidth = subWidth;
        this.isNullable = isNullable;
        this.isPrimaryKey = isPrimaryKey;
        this.isAutoIncrement = isAutoIncrement;
    }

    public String name() {
        return name;
    }

    public String fieldType() {
        return fieldType;
    }

    public Optional<Integer> columnSize() {
        return columnSize;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    @Override
    public String toString() {
        return "{name: " + name + ", fieldType: " + fieldType + ", columnSize: " + columnSize + ", subWidth: " + subWidth.orElse(null) + ", isNullable: " + isNullable + ", isPrimaryKey: " + isPrimaryKey + ", isAutoIncrement: " + isAutoIncrement + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldMetaData that = (FieldMetaData) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
