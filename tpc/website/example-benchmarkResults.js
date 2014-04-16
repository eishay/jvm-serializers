// Example results file.  To see it being used:
// 1. Copy to "benchmarkResults.js".
// 2. View "index.html" in a web browser.

var benchmarkResults = {
  columns: ['ser', 'deser', 'size', 'gz-size'],

  categories: [
    // Serialization format.
    {
      name: "Format",
      properties: [
        {
          name: "text",
          description: "the serializer uses a human-readable text format",
        },
        {
          name: "neutral",
          description: "the serializer's format is language-neutral",
        },
      ]
    },
    // Specifying the data structure.
    {
      name: "Spec",
      properties: [
        {
          name: "pojo",
          description: "the serializer automatically uses POJOs",
        },
        {
          name: "schema",
          description: "you must define a schema for your data type and compile that schema",
        },
        {
          name: "manual",
          description: "you must call the serialization routines manually",
        },
        {
          name: "manual.more",
          description: "you must call the serialization routines manually, and there is more effort involved",
        },
      ],
    },
    // For POJO serializers, some additional properties.
    {
      name: "POJO Hints",
      properties: [
        {
          name: "hint.type",
          description: "the serializer was given the list of type names", 
        },
        {
          name: "hint.null",
          description: "the serializer was given the list of non-null fields", 
        },
      ],
    },
  ],

  entries: [
    {
      name: 'protobuf',
      properties: ['schema', 'neutral'],
      results: [10, 12, 12, 11]
    },
    {
      name: 'kryo',
      properties: ['pojo'],
      results: [11, 13, 8, 4]
    },
    {
      name: 'kryo-opt',
      properties: ['pojo', 'hint.type', 'hint.null'],
      results: [10, 10, 8, 4]
    },
    {
      name: 'jackson-databind',
      properties: ['pojo', 'text', 'neutral'],
      results: [40, 20, 18, 16]
    },
    {
      name: 'jackson-manual',
      properties: ['manual.more', 'text', 'neutral'],
      results: [4, 24, 22, 20]
    },
    {
      name: 'jvm-default',
      properties: ['pojo'],
      results: [50, 50, 12, 11]
    },
    {
      name: 'jvm-manual',
      properties: ['manual'],
      results: [15, 15, 12, 11]
    },
  ],
}
