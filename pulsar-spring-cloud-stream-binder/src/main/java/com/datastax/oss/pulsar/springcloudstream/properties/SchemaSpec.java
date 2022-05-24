package com.datastax.oss.pulsar.springcloudstream.properties;

import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.common.schema.SchemaType;

public class SchemaSpec {
	private SchemaType type = SchemaType.BYTES;
	private Class<?> valueClass = byte[].class;

	public SchemaType getType() {
		return type;
	}

	public void setType(SchemaType type) {
		this.type = type;
		switch (type) {
		case STRING:
			valueClass = String.class;
			break;
		case BYTES:
			valueClass = byte[].class;
			break;
		}
	}

	public Class<?> getValueClass() {
		return valueClass;
	}

	public void setValueClass(Class<?> valueClass) {
		this.valueClass = valueClass;
	}

	public Schema<?> asPulsarSchema() {
		switch (type) {
		case STRING:
			return Schema.STRING;
		case AVRO:
			return Schema.AVRO(valueClass);
		case JSON:
			return Schema.JSON(valueClass);
		}

		return Schema.BYTES;
	}
}
