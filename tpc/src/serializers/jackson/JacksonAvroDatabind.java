package serializers.jackson;

import serializers.JavaBuiltIn;
import serializers.TestGroups;
import serializers.avro.Avro;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.avro.AvroFactory;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;

import data.media.MediaContent;

public class JacksonAvroDatabind
{
    public static void register(TestGroups groups)
    {
        ObjectMapper mapper = new ObjectMapper(new AvroFactory());
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
        JavaType type = mapper.constructType(MediaContent.class);
        AvroSchema schema = new AvroSchema(Avro.Media.sMediaContent);
        ObjectReader reader =  mapper.reader(type).with(schema);
        ObjectWriter writer = mapper.writerWithType(type).withSchema(schema);
        groups.media.add(JavaBuiltIn.mediaTransformer, new StdJacksonDataBind<MediaContent>
            ("avro/jackson", type, mapper, reader, writer));
    }
}
