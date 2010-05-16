package serializers;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;

import serializers.cks.media.MediaContent;

import cakoose.util.Either;

public class CksText
{
	public static void register(TestGroups groups)
	{
		groups.media.add(Cks.MediaTransformer, MediaSerializer, "cks");
	}

	// ------------------------------------------------------------
	// Serializers

	public static final Serializer<MediaContent> MediaSerializer = new Serializer<MediaContent>()
	{
		public MediaContent deserialize(byte[] array) throws Exception
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(array);

			Either<cks.value.text.model.Value,java.util.List<cakoose.util.parser.Problem>> raw = cks.value.text.parser.TextParser.parse(new cks.common.parser.StreamTokenizer(new InputStreamReader(bais), "input"));
			if (raw.isRight()) {
				throw new Exception(raw.getRight().get(0).toString());
			}

			return MediaContent._TextMarshaller.marshal(raw.getLeft());
		}

		public byte[] serialize(MediaContent content) throws Exception
		{
			ByteArrayOutputStream baos = outputStream(content);
			OutputStreamWriter out = new OutputStreamWriter(baos);
			MediaContent._TextWriter.write(out, content);
			out.flush();
			return baos.toByteArray();
		}

		public String getName ()
		{
			return "cks-text";
		}
	};
}

