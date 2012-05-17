package serializers;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;

import serializers.BenchmarkBase.Params;
import serializers.cks.CksBinary;
import serializers.cks.CksText;
import serializers.jackson.*;
import serializers.json.JsonGsonDatabind;
import serializers.json.JsonArgoTree;
import serializers.json.FastJSONDatabind;
import serializers.json.FlexjsonDatabind;
import serializers.json.JsonGsonManual;
import serializers.json.JsonGsonTree;
import serializers.json.JsonDotOrgManualTree;
import serializers.json.JsonLibJsonDatabind;
import serializers.json.JsonPathDeserializerOnly;
import serializers.json.JsonSimpleManualTree;
import serializers.json.JsonSimpleWithContentHandler;
import serializers.json.JsonSmartManualTree;
import serializers.json.JsonTwoLattes;
import serializers.json.JsonijJpath;
import serializers.json.JsonijManualTree;
import serializers.json.JsonSvensonDatabind;
import serializers.protobuf.ActiveMQProtobuf;
import serializers.protobuf.Protobuf;
import serializers.protostuff.Protostuff;
import serializers.protostuff.ProtostuffJson;
import serializers.protostuff.ProtostuffSmile;
import serializers.xml.XmlJavolution;
import serializers.xml.XmlStax;
import serializers.xml.XmlXStream;

/**
 * Full test of various codecs, using a single <code>MediaItem</code>
 * as test data.
 */
public class BenchmarkRunner extends MediaItemBenchmark
{
    public static void main(String[] args) {
        new BenchmarkRunner().runBenchmark(args);
    }

    @Override
    protected void addTests(TestGroups groups)
    {
        // Binary Formats; language-specific ones
        JavaBuiltIn.register(groups);
        JavaManual.register(groups);
        Scala.register(groups);
        // hessian, kryo and wobly are Java object serializations
        Hessian.register(groups);
        Kryo.register(groups);
        Wobly.register(groups);
        
        // Binary formats, generic: protobuf, thrift, avro, CKS, msgpack
        Protobuf.register(groups);
        // 16-May-2012, Nate: As discussed on mailing list, removed ActiveMQProtobuf as 
        // its lazy deserialization isn't comparable to other serializers.
        // ActiveMQProtobuf.register(groups);
        Protostuff.register(groups);
        Thrift.register(groups);
        AvroSpecific.register(groups);
        AvroGeneric.register(groups);
        CksBinary.register(groups);
        MsgPack.register(groups);

        // JSON
        JacksonJsonManual.register(groups);
        JacksonJsonDatabind.register(groups);
        JacksonJsonAfterburner.register(groups); // databind with bytecode generation (faster)
        JacksonJsonTree.register(groups);
// 01-May-2012, tatu: not all that useful (IMO) for general comparisons
//        JacksonJsonDatabindWithStrings.register(groups);
//        JacksonJsonTreeWithStrings.register(groups);
        JsonTwoLattes.register(groups);
        ProtostuffJson.register(groups);
// too slow, why bother:
//        ProtobufJson.register(groups);
        JsonGsonManual.register(groups);
        JsonGsonTree.register(groups);
        JsonGsonDatabind.register(groups);
        JsonSvensonDatabind.register(groups);
        FlexjsonDatabind.register(groups);
        JsonLibJsonDatabind.register(groups);
        FastJSONDatabind.register(groups);
        JsonSimpleWithContentHandler.register(groups);
        JsonSimpleManualTree.register(groups);
        JsonSmartManualTree.register(groups);
        JsonDotOrgManualTree.register(groups);
        JsonijJpath.register(groups);
        JsonijManualTree.register(groups);
        JsonArgoTree.register(groups);
        JsonPathDeserializerOnly.register(groups);

        // Then JSON-like
        // CKS text is textual JSON-like format
        CksText.register(groups);
        // then binary variants
        // Smile is 1-to-1 binary JSON serialization
        JacksonSmileManual.register(groups);
        JacksonSmileDatabind.register(groups);
        JacksonSmileAfterburner.register(groups); // databind with bytecode generation (faster)
        ProtostuffSmile.register(groups);
        // BSON is JSON-like format with extended datatypes
        JacksonBsonDatabind.register(groups);
        MongoDB.register(groups);

        // XML-based formats.
        XmlStax.register(groups, true, true, true); // woodstox/aalto/fast-infoset
        XmlXStream.register(groups);
        JacksonXmlDatabind.register(groups);
        XmlJavolution.register(groups);
    }
}
