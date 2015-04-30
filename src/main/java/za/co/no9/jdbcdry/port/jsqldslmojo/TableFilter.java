package za.co.no9.jdbcdry.port.jsqldslmojo;

import za.co.no9.jdbcdry.model.TableMetaData;
import za.co.no9.jdbcdry.model.TableName;
import za.co.no9.jdbcdry.port.jsqldslmojo.configuration.TablePatternType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TableFilter {
    private final List<CompiledTablePattern> includes;
    private final List<CompiledTablePattern> excludes;

    public TableFilter(List<TablePatternType> includes, List<TablePatternType> excludes) {
        this.includes = compile(includes.size() == 0 ? allIncludes() : includes);
        this.excludes = compile(excludes);
    }

    private List<TablePatternType> allIncludes() {
        return Collections.singletonList(new TablePatternType());
    }

    private List<CompiledTablePattern> compile(List<TablePatternType> tablePatternTypes) {
        List<CompiledTablePattern> result = new ArrayList<>();
        for (TablePatternType tablePatternType : tablePatternTypes) {
            result.add(new CompiledTablePattern(tablePatternType));
        }
        return result;
    }

    public boolean filter(TableMetaData table) {
        return filter(table.tableName());
    }

    public Collection<TableMetaData> filter(Collection<TableMetaData> tables) {
        return tables.stream().filter(this::filter).collect(Collectors.<TableMetaData>toList());
    }

    public boolean filter(TableName tableName) {
        return filterMatch(includes, tableName) && !filterMatch(excludes, tableName);
    }

    private boolean filterMatch(List<CompiledTablePattern> patterns, TableName tableName) {
        return patterns.stream().anyMatch(x -> x.filter(tableName));
    }

    static class CompiledTablePattern {
        public final Predicate<String> schemaPattern;
        public final Predicate<String> tablePattern;

        public CompiledTablePattern(String schemaPattern, String tablePattern) {
            this.schemaPattern = compile(schemaPattern);
            this.tablePattern = compile(tablePattern);
        }

        public CompiledTablePattern(TablePatternType tablePatternType) {
            this(tablePatternType.getSchema(), tablePatternType.getTable());
        }

        private Predicate<String> compile(String pattern) {
            return pattern == null ? (x -> true) : Pattern.compile(pattern).asPredicate();
        }

        public boolean filter(TableName tableName) {
            return filterSchema(tableName) && filterTable(tableName);
        }

        private boolean filterSchema(TableName tableName) {
            return !tableName.schema().isPresent() || schemaPattern.test(tableName.schema().get());
        }

        private boolean filterTable(TableName tableName) {
            return tablePattern.test(tableName.name());
        }
    }
}
