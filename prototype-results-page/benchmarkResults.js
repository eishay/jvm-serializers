var benchmarkResults = {
  categories : [ {
    name : "Format",
    description : "The format used to serialize.",
    properties : [ {
      name : "netural",
      description : "The serialization format is language-neutral."
    }, {
      name : "binary",
      description : "Some binary format."
    }, {
      name : "text",
      description : "Some human-readable text format."
    }, {
      name : "json",
      description : null
    }, {
      name : "xml",
      description : null
    } ]
  }, {
    name : "Spec",
    description : "How you specify the data structure to the serializer.",
    properties : [ {
      name : "pojo",
      description : "The serializer works with user-defined plain Java classes."
    }, {
      name : "schema",
      description : "You must define a schema for your data types and run the library's compiler to generate Java classes."
    }, {
      name : "manual",
      description : "You must call the serialization routines manually."
    }, {
      name : "manual.more",
      description : "You must call the serialization routines manually, and there is more effort than in the \"manual\" case."
    } ]
  }, {
    name : "POJO Hints",
    description : "For POJO serializers, did we specify any hints to help them out?",
    properties : [ {
      name : "hint.type",
      description : "The serializer was given the list of types."
    }, {
      name : "hint.null",
      description : "The serializer was given the list of non-null fields."
    } ]
  }, {
    name : "Speed",
    description : null,
    properties : [ {
      name : "fast",
      description : "Serializers whose round-trip time is in the top 50th percentile."
    }, {
      name : "slow",
      description : "Serializers whose round-trip time is in the bottom 25th percentile."
    } ]
  } ],
  columns : [ "create", "ser", "deser", "total", "size", "size-gz" ],
  entries : [ {
    name : "avro-generic",
    properties : [ "netural", "binary", "fast" ],
    results : [ 663, 2973, 4894, 7867, 221, 133 ]
  }, {
    name : "avro-specific",
    properties : [ "netural", "binary" ],
    results : [ 110, 2036, 43719, 45755, 221, 133 ]
  }, {
    name : "bson/jackson/databind",
    properties : [ "netural", "binary" ],
    results : [ 77, 6377, 15721, 22098, 506, 286 ]
  }, {
    name : "bson/mongodb/manual",
    properties : [ "netural", "binary" ],
    results : [ 106, 6303, 33090, 39393, 495, 278 ]
  }, {
    name : "cbor/jackson/databind",
    properties : [ "netural", "binary", "fast" ],
    results : [ 73, 1849, 3929, 5778, 397, 246 ]
  }, {
    name : "cbor/jackson/manual",
    properties : [ "netural", "binary", "fast" ],
    results : [ 117, 1886, 3469, 5355, 386, 238 ]
  }, {
    name : "fst-flat-pre",
    properties : [ "binary", "fast" ],
    results : [ 76, 929, 1042, 1972, 251, 165 ]
  }, {
    name : "fst-flat",
    properties : [ "binary", "fast" ],
    results : [ 77, 1199, 1616, 2815, 314, 204 ]
  }, {
    name : "fst",
    properties : [ "binary", "fast" ],
    results : [ 73, 1994, 2197, 4191, 316, 203 ]
  }, {
    name : "hessian",
    properties : [ "netural", "binary" ],
    results : [ 70, 5027, 9888, 14915, 501, 313 ]
  }, {
    name : "java-built-in",
    properties : [ "binary", "slow" ],
    results : [ 99, 10378, 92341, 102718, 889, 514 ]
  }, {
    name : "java-built-in-serializer",
    properties : [ "binary", "slow" ],
    results : [ 106, 11571, 116588, 128159, 889, 514 ]
  }, {
    name : "java-manual",
    properties : [ "binary", "fast" ],
    results : [ 71, 1047, 824, 1871, 255, 147 ]
  }, {
    name : "jboss-marshalling-river-ct-manual",
    properties : [ "binary", "fast" ],
    results : [ 76, 2114, 2074, 4188, 289, 167 ]
  }, {
    name : "jboss-marshalling-river-ct",
    properties : [ "binary", "fast" ],
    results : [ 102, 3856, 4057, 7913, 298, 199 ]
  }, {
    name : "jboss-marshalling-river-manual",
    properties : [ "binary", "fast" ],
    results : [ 71, 2938, 8477, 11415, 483, 240 ]
  }, {
    name : "jboss-marshalling-river",
    properties : [ "binary", "slow" ],
    results : [ 78, 5892, 63561, 69453, 694, 400 ]
  }, {
    name : "jboss-marshalling-serial",
    properties : [ "binary", "slow" ],
    results : [ 93, 14430, 70314, 84744, 856, 498 ]
  }, {
    name : "jboss-serialization",
    properties : [ "binary" ],
    results : [ 71, 8152, 9531, 17683, 932, 582 ]
  }, {
    name : "json/argo/manual-tree",
    properties : [ "netural", "text", "json", "slow" ],
    results : [ 72, 75524, 33220, 108743, 485, 263 ]
  }, {
    name : "json/fastjson/databind",
    properties : [ "netural", "text", "json", "fast" ],
    results : [ 73, 1494, 1916, 3410, 486, 262 ]
  }, {
    name : "json/flexjson/databind",
    properties : [ "netural", "text", "json", "slow" ],
    results : [ 89, 36250, 425907, 462157, 503, 273 ]
  }, {
    name : "json/gson/databind",
    properties : [ "netural", "text", "json" ],
    results : [ 105, 6602, 7573, 14175, 486, 259 ]
  }, {
    name : "json/gson/manual",
    properties : [ "netural", "text", "json" ],
    results : [ 91, 5924, 7154, 13078, 468, 253 ]
  }, {
    name : "json/gson/manual-tree",
    properties : [ "netural", "text", "json" ],
    results : [ 71, 6222, 11478, 17700, 485, 259 ]
  }, {
    name : "json/jackson+afterburner/databind",
    properties : [ "netural", "text", "json", "fast" ],
    results : [ 83, 2188, 4755, 6942, 485, 261 ]
  }, {
    name : "json/jackson/databind",
    properties : [ "netural", "text", "json", "fast" ],
    results : [ 100, 2088, 5660, 7749, 485, 261 ]
  }, {
    name : "json/jackson-jr/databind",
    properties : [ "netural", "text", "json" ],
    results : [ 107, 3446, 9787, 13233, 468, 255 ]
  }, {
    name : "json/jackson/manual",
    properties : [ "netural", "text", "json", "fast" ],
    results : [ 71, 1328, 2790, 4118, 468, 253 ]
  }, {
    name : "json/javax-stream/glassfish",
    properties : [ "netural", "text", "json" ],
    results : [ 82, 9071, 21378, 30449, 468, 253 ]
  }, {
    name : "json/javax-tree/glassfish",
    properties : [ "netural", "text", "json" ],
    results : [ 1475, 10524, 21362, 31886, 485, 263 ]
  }, {
    name : "json/json-lib/databind",
    properties : [ "netural", "text", "json", "slow" ],
    results : [ 77, 29838, 893614, 923453, 485, 263 ]
  }, {
    name : "json/json-smart/manual-tree",
    properties : [ "netural", "text", "json" ],
    results : [ 111, 9991, 7043, 17034, 495, 269 ]
  }, {
    name : "json/json.simple/manual",
    properties : [ "netural", "text", "json" ],
    results : [ 75, 10594, 12273, 22867, 495, 269 ]
  }, {
    name : "json/jsonij/manual-jpath",
    properties : [ "netural", "text", "json", "slow" ],
    results : [ 73, 77589, 25965, 103554, 478, 257 ]
  }, {
    name : "json/org.json/manual-tree",
    properties : [ "netural", "text", "json" ],
    results : [ 74, 8310, 11461, 19770, 485, 259 ]
  }, {
    name : "json/protobuf",
    properties : [ "netural", "text", "json", "slow" ],
    results : [ 129, 10846, 126229, 137075, 488, 253 ]
  }, {
    name : "json/protostuff-manual",
    properties : [ "netural", "text", "json", "fast" ],
    results : [ 71, 1762, 5551, 7313, 449, 233 ]
  }, {
    name : "json/protostuff-runtime",
    properties : [ "netural", "text", "json", "fast" ],
    results : [ 99, 2824, 7385, 10209, 469, 243 ]
  }, {
    name : "json/svenson/databind",
    properties : [ "netural", "text", "json" ],
    results : [ 80, 7431, 43037, 50468, 495, 271 ]
  }, {
    name : "kryo-flat-pre",
    properties : [ "binary", "fast" ],
    results : [ 74, 884, 1584, 2468, 212, 132 ]
  }, {
    name : "kryo-flat",
    properties : [ "binary", "fast" ],
    results : [ 76, 1044, 1596, 2640, 268, 177 ]
  }, {
    name : "kryo-manual",
    properties : [ "binary", "fast" ],
    results : [ 70, 680, 912, 1591, 211, 131 ]
  }, {
    name : "kryo-opt",
    properties : [ "binary", "fast" ],
    results : [ 103, 1309, 1742, 3051, 209, 129 ]
  }, {
    name : "kryo-serializer",
    properties : [ "binary", "fast" ],
    results : [ 75, 2089, 2163, 4251, 286, 188 ]
  }, {
    name : "msgpack-databind",
    properties : [ "netural", "binary", "fast" ],
    results : [ 91, 1536, 3825, 5361, 233, 146 ]
  }, {
    name : "msgpack-manual",
    properties : [ "netural", "binary", "fast" ],
    results : [ 83, 1108, 3244, 4352, 233, 146 ]
  }, {
    name : "protobuf/protostuff",
    properties : [ "netural", "binary", "fast" ],
    results : [ 108, 654, 1145, 1799, 239, 149 ]
  }, {
    name : "protobuf/protostuff-runtime",
    properties : [ "netural", "binary", "fast" ],
    results : [ 72, 959, 1199, 2158, 241, 150 ]
  }, {
    name : "protobuf",
    properties : [ "netural", "binary", "fast" ],
    results : [ 151, 1576, 1346, 2922, 239, 149 ]
  }, {
    name : "protostuff-graph",
    properties : [ "binary", "fast" ],
    results : [ 112, 1100, 1252, 2351, 239, 150 ]
  }, {
    name : "protostuff-graph-runtime",
    properties : [ "binary", "fast" ],
    results : [ 72, 1123, 1381, 2504, 241, 151 ]
  }, {
    name : "protostuff-manual",
    properties : [ "binary", "fast" ],
    results : [ 71, 652, 1051, 1703, 239, 150 ]
  }, {
    name : "protostuff",
    properties : [ "binary", "fast" ],
    results : [ 99, 611, 1159, 1770, 239, 150 ]
  }, {
    name : "protostuff-runtime",
    properties : [ "binary", "fast" ],
    results : [ 77, 797, 1241, 2037, 241, 151 ]
  }, {
    name : "scala/java-built-in",
    properties : [ "slow" ],
    results : [ 143, 11258, 148031, 159289, 1312, 700 ]
  }, {
    name : "scala/sbinary",
    properties : [ "fast" ],
    results : [ 198, 2088, 1808, 3895, 255, 147 ]
  }, {
    name : "smile/jackson+afterburner/afterburner",
    properties : [ "binary", "fast" ],
    results : [ 88, 2240, 3730, 5970, 352, 252 ]
  }, {
    name : "smile/jackson/databind",
    properties : [ "binary", "fast" ],
    results : [ 75, 1985, 4059, 6044, 338, 241 ]
  }, {
    name : "smile/jackson/manual",
    properties : [ "binary", "fast" ],
    results : [ 73, 1130, 2369, 3498, 341, 244 ]
  }, {
    name : "stephenerialization",
    properties : [ "binary", "slow" ],
    results : [ 67, 8036, 83465, 91501, 1093, 517 ]
  }, {
    name : "thrift-compact",
    properties : [ "netural", "binary", "fast" ],
    results : [ 127, 2425, 1484, 3909, 240, 148 ]
  }, {
    name : "thrift",
    properties : [ "netural", "binary", "fast" ],
    results : [ 202, 2928, 1916, 4843, 349, 197 ]
  }, {
    name : "wobly-compact",
    properties : [ "binary", "fast" ],
    results : [ 43, 1155, 1057, 2212, 225, 139 ]
  }, {
    name : "wobly",
    properties : [ "binary", "fast" ],
    results : [ 52, 1058, 857, 1915, 251, 151 ]
  }, {
    name : "xml/aalto-manual",
    properties : [ "netural", "text", "xml", "slow" ],
    results : [ 105, 3949, 64599, 68549, 653, 304 ]
  }, {
    name : "xml/exi-manual",
    properties : [ "netural", "text", "xml", "slow" ],
    results : [ 113, 33689, 86487, 120176, 337, 327 ]
  }, {
    name : "xml/fastinfo-manual",
    properties : [ "netural", "text", "xml" ],
    results : [ 77, 8692, 16530, 25222, 377, 284 ]
  }, {
    name : "xml/jackson+aalto/databind",
    properties : [ "netural", "text", "xml" ],
    results : [ 99, 3448, 15951, 19399, 683, 286 ]
  }, {
    name : "xml/javolution/manual",
    properties : [ "netural", "text", "xml" ],
    results : [ 87, 9523, 26169, 35692, 504, 263 ]
  }, {
    name : "xml/woodstox-manual",
    properties : [ "netural", "text", "xml" ],
    results : [ 72, 4249, 14764, 19013, 653, 304 ]
  }, {
    name : "xml/xstream+c-aalto",
    properties : [ "netural", "text", "xml", "slow" ],
    results : [ 99, 7326, 72326, 79651, 525, 273 ]
  }, {
    name : "xml/xstream+c-fastinfo",
    properties : [ "netural", "text", "xml", "slow" ],
    results : [ 105, 13666, 56694, 70360, 345, 264 ]
  }, {
    name : "xml/xstream+c",
    properties : [ "netural", "text", "xml", "slow" ],
    results : [ 82, 7120, 62281, 69400, 487, 244 ]
  }, {
    name : "xml/xstream+c-woodstox",
    properties : [ "netural", "text", "xml", "slow" ],
    results : [ 79, 6333, 73395, 79728, 525, 273 ]
  }, {
    name : "yaml/jackson/databind",
    properties : [ "netural", "text", "json", "slow" ],
    results : [ 70, 24278, 122702, 146980, 505, 260 ]
  } ]
}