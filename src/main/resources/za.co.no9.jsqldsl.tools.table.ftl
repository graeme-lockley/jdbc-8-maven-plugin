package ${packageName};

import za.co.no9.jsqldsl.db.h2.*;

public class ${tableMetaData.tableName().dbName()} extends za.co.no9.jsqldsl.db.TableReference {
<#list tableMetaData.fields() as field>
	public final ${field.fieldType()}ColumnReference ${field.name()};
</#list>

	private ${tableMetaData.tableName().dbName()}(java.util.Optional<String> alias) {
		super(alias);

<#list tableMetaData.fields() as field>
		${field.name()} = ${field.fieldType()}ColumnReference.from(this, "${field.name()}");
</#list>
	}

	public static ${tableMetaData.tableName().dbName()} ref() {
		return new ${tableMetaData.tableName().dbName()}(java.util.Optional.<String>empty());
	}

	public static ${tableMetaData.tableName().dbName()} as(String alias) {
		return new ${tableMetaData.tableName().dbName()}(java.util.Optional.of(alias));
	}

	@Override
	public String name() {
		return "${tableMetaData.tableName().dbName()}";
	}
}
