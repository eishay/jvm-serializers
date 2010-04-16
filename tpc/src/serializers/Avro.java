package serializers;

import org.apache.avro.Schema;

public class Avro
{
	// -------------------------------------------------------------
	// Schema Builder Helpers

	public static final Record R(String name, String namespace, Field... fields)
	{
		return new Record(name, namespace, fields);
	}

	public static final Named N(String name)
	{
		return new Named(name);
	}

	public static final Union U(Type... types)
	{
		return new Union(types);
	}

	public static final Type O(Type t) // "optional"
	{
		return U(t, NULL);
	}

	public static final Array A(Type elementType)
	{
		return new Array(elementType);
	}

	public static final Field F(String name, Type type)
	{
		return new Field(name, type);
	}

	public static final Named STRING = new Named("string");
	public static final Named INT = new Named("int");
	public static final Named LONG = new Named("long");
	public static final Named NULL = new Named("null");

	public static abstract class Type
	{
		public String toString() { return toSchema(); }

		public final String toSchema()
		{
			StringBuilder b = new StringBuilder();
			toSchema(b);
			return b.toString();
		}

		public abstract void toSchema(StringBuilder b);
	}

	public static final class Record extends Type
	{
		public final String name;
		public final String namespace;
		public final Field[] fields;

		private Record(String name, String namespace, Field[] fields)
		{
			this.name = name;
			this.namespace = namespace;
			this.fields = fields;
		}

		public void toSchema(StringBuilder b)
		{
			b.append("{\"type\": \"record\", \"name\": \"").append(name).append("\"");
			if (namespace != null) {
				b.append(", \"namespace\": \"").append(namespace).append("\"");
			}
			b.append(", \"fields\": [");
			String sep = "";
			for (Field f : fields) {
				b.append(sep); sep = ", ";
				b.append("{\"name\": \"" + f.name + "\", \"type\": ");
				f.type.toSchema(b);
				b.append("}");
			}
			b.append("]}");
		}
	}

	public static final class Union extends Type
	{
		public final Type[] members;

		private Union(Type[] members)
		{
			this.members = members;
		}

		public void toSchema(StringBuilder b)
		{
			b.append("[");
			String sep = "";
			for (Type t : members) {
				b.append(sep); sep = ", ";
				t.toSchema(b);
			}
			b.append("]");
		}
	}

	public static final class Array extends Type
	{
		public final Type elementType;

		private Array(Type elementType)
		{
			this.elementType = elementType;
		}

		public void toSchema(StringBuilder b)
		{
			b.append("{\"type\": \"array\", \"items\": ");
			elementType.toSchema(b);
			b.append("}");
		}
	}

	public static final class Enum extends Type
	{
		public final String name;
		public final String[] options;

		public Enum(String name, String[] options)
		{
			this.name = name;
			this.options = options;
		}

		public void toSchema(StringBuilder b)
		{
			b.append("{\"type\": \"enum\", \"name\": \"").append(name).append("\", \"symbols\": [");
			String sep = "";
			for (String option : options) {
				b.append(sep); sep = ", ";
				b.append('"').append(option).append('"');
			}
			b.append("]}");
		}
	}

	public static final class Named extends Type
	{
		public final String name;

		private Named(String name)
		{
			this.name = name;
		}

		public void toSchema(StringBuilder b)
		{
			b.append('"').append(name).append('"');
		}
	}

	public static final class Field
	{
		public final String name;
		public final Type type;

		private Field(String name, Type type)
		{
			this.name = name;
			this.type = type;
		}
	}

	// -------------------------------------------------------------
	// Media

	public static final class Media
	{
		public static final Type tImage =
			R("Image", null,
				F("uri", STRING),
				F("title", O(STRING)),
				F("width", INT),
				F("height", INT),
				F("size", INT));

		public static final Type tMedia =
			R("Media", null,
				F("uri", STRING),
				F("title", O(STRING)),
				F("width", INT),
				F("height", INT),
				F("format", STRING),
				F("duration", LONG),
				F("size", LONG),
				F("bitrate", O(INT)),
				F("persons", A(STRING)),
				F("player", INT),
				F("copyright", O(STRING)));

		public static final Type tMediaContent =
			R("MediaContent", "serializers.avro.specific",
				F("images", A(tImage)),
				F("media", tMedia));

		public static final Schema sImage = Schema.parse(tImage.toSchema());
		public static final Schema sMedia = Schema.parse(tMedia.toSchema());
		public static final Schema sMediaContent = Schema.parse(tMediaContent.toSchema());

		public static final Schema sImages = sMediaContent.getField("images").schema();
		public static final Schema sPersons = sMedia.getField("persons").schema();
	}

}
