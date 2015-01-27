package serializers.dslplatform.media;

public final class MediaContent implements java.io.Serializable, com.dslplatform.client.json.JsonObject {
    public MediaContent(
            final serializers.dslplatform.media.Media media,
            final java.util.List<serializers.dslplatform.media.Image> images) {
        setMedia(media);
        setImages(images);
    }

    public MediaContent() {
        this.media = new serializers.dslplatform.media.Media();
        this.images = new java.util.ArrayList<serializers.dslplatform.media.Image>();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + 1135396885;
        result = prime * result + (this.media.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;

        if (!(obj instanceof MediaContent)) return false;
        final MediaContent other = (MediaContent) obj;

        if (!(this.media.equals(other.media))) return false;
        if (!((this.images == other.images || this.images != null && this.images.equals(other.images)))) return false;

        return true;
    }

    @Override
    public String toString() {
        return "MediaContent(" + media + ',' + images + ')';
    }

    private static final long serialVersionUID = 0x0097000a;

    private serializers.dslplatform.media.Media media;

    public serializers.dslplatform.media.Media getMedia() {
        return media;
    }

    public MediaContent setMedia(final serializers.dslplatform.media.Media value) {
        if (value == null) throw new IllegalArgumentException("Property \"media\" cannot be null!");
        this.media = value;

        return this;
    }

    private java.util.List<serializers.dslplatform.media.Image> images;

    public java.util.List<serializers.dslplatform.media.Image> getImages() {
        return images;
    }

    public MediaContent setImages(final java.util.List<serializers.dslplatform.media.Image> value) {
        if (value == null) throw new IllegalArgumentException("Property \"images\" cannot be null!");
        serializers.dslplatform.Guards.checkNulls(value);
        this.images = value;

        return this;
    }

    public void serialize(final com.dslplatform.client.json.JsonWriter sw, final boolean minimal) {
        sw.writeByte(com.dslplatform.client.json.JsonWriter.OBJECT_START);
        __serializeJsonObject(sw, minimal, false);
        sw.writeByte(com.dslplatform.client.json.JsonWriter.OBJECT_END);
    }

    void __serializeJsonObject(com.dslplatform.client.json.JsonWriter sw, boolean minimal, boolean hasWrittenProperty) {
        if (this.media != null) {
            if (hasWrittenProperty) sw.writeByte(com.dslplatform.client.json.JsonWriter.COMMA);
            hasWrittenProperty = true;
            sw.writeAscii("\"media\":");
            this.media.serialize(sw, minimal);
        } else if (!minimal) {
            if (hasWrittenProperty) sw.writeByte(com.dslplatform.client.json.JsonWriter.COMMA);
            hasWrittenProperty = true;
            sw.writeAscii("\"media\":null");
        }

        if (this.images.size() != 0) {
            if (hasWrittenProperty) sw.writeByte(com.dslplatform.client.json.JsonWriter.COMMA);
            hasWrittenProperty = true;
            sw.writeAscii("\"images\":[");
            serializers.dslplatform.media.Image item = this.images.get(0);
            item.serialize(sw, minimal);
            for (int i = 1; i < this.images.size(); i++) {
                sw.writeByte(com.dslplatform.client.json.JsonWriter.COMMA);
                item = this.images.get(i);
                item.serialize(sw, minimal);
            }
            sw.writeByte(com.dslplatform.client.json.JsonWriter.ARRAY_END);
        } else if (!minimal) {
            if (hasWrittenProperty) sw.writeByte(com.dslplatform.client.json.JsonWriter.COMMA);
            hasWrittenProperty = true;
            sw.writeAscii("\"images\":[]");
        }
    }

    public static com.dslplatform.client.json.JsonReader.ReadJsonObject<MediaContent> JSON_READER = new com.dslplatform.client.json.JsonReader.ReadJsonObject<MediaContent>() {
        @Override
        public MediaContent deserialize(
                final com.dslplatform.client.json.JsonReader reader,
                final com.dslplatform.patterns.ServiceLocator locator) throws java.io.IOException {
            return new serializers.dslplatform.media.MediaContent(reader, locator);
        }
    };

    private MediaContent(
            final com.dslplatform.client.json.JsonReader reader,
            final com.dslplatform.patterns.ServiceLocator _serviceLocator) throws java.io.IOException {
        serializers.dslplatform.media.Media _media_ = null;
        java.util.List<serializers.dslplatform.media.Image> _images_ = new java.util.ArrayList<serializers.dslplatform.media.Image>();
        byte nextToken = reader.last();
        if (nextToken != '}') {
            int nameHash = reader.fillName();
            nextToken = reader.getNextToken();
            if (nextToken == 'n') {
                if (reader.wasNull()) {
                    nextToken = reader.getNextToken();
                } else {
                    throw new java.io.IOException("Expecting 'u' (as null) at position " + reader.positionInStream()
                            + ". Found " + (char) nextToken);
                }
            } else {
                switch (nameHash) {
                    case -56959229:

                        if (nextToken == '{') {
                            reader.getNextToken();
                            _media_ = serializers.dslplatform.media.Media.JSON_READER.deserialize(reader,
                                    _serviceLocator);
                            nextToken = reader.getNextToken();
                        } else throw new java.io.IOException("Expecting '{' at position " + reader.positionInStream()
                                + ". Found " + (char) nextToken);
                        break;
                    case -774420821:

                        if (nextToken == '[') {
                            nextToken = reader.getNextToken();
                            if (nextToken != ']') {
                                reader.deserializeCollection(serializers.dslplatform.media.Image.JSON_READER, _images_);
                            }
                            nextToken = reader.getNextToken();
                        } else throw new java.io.IOException("Expecting '[' at position " + reader.positionInStream()
                                + ". Found " + (char) nextToken);
                        break;
                    default:
                        nextToken = reader.skip();
                        break;
                }
            }
            while (nextToken == ',') {
                nextToken = reader.getNextToken();
                nameHash = reader.fillName();
                nextToken = reader.getNextToken();
                if (nextToken == 'n') {
                    if (reader.wasNull()) {
                        nextToken = reader.getNextToken();
                        continue;
                    } else {
                        throw new java.io.IOException("Expecting 'u' (as null) at position "
                                + reader.positionInStream() + ". Found " + (char) nextToken);
                    }
                }
                switch (nameHash) {
                    case -56959229:

                        if (nextToken == '{') {
                            reader.getNextToken();
                            _media_ = serializers.dslplatform.media.Media.JSON_READER.deserialize(reader,
                                    _serviceLocator);
                            nextToken = reader.getNextToken();
                        } else throw new java.io.IOException("Expecting '{' at position " + reader.positionInStream()
                                + ". Found " + (char) nextToken);
                        break;
                    case -774420821:

                        if (nextToken == '[') {
                            nextToken = reader.getNextToken();
                            if (nextToken != ']') {
                                reader.deserializeCollection(serializers.dslplatform.media.Image.JSON_READER, _images_);
                            }
                            nextToken = reader.getNextToken();
                        } else throw new java.io.IOException("Expecting '[' at position " + reader.positionInStream()
                                + ". Found " + (char) nextToken);
                        break;
                    default:
                        nextToken = reader.skip();
                        break;
                }
            }
            if (nextToken != '}') { throw new java.io.IOException("Expecting '}' at position "
                    + reader.positionInStream() + ". Found " + (char) nextToken); }
        }

        this.media = _media_;
        this.images = _images_;
    }

    public static Object deserialize(
            final com.dslplatform.client.json.JsonReader reader,
            final com.dslplatform.patterns.ServiceLocator locator) throws java.io.IOException {
        switch (reader.getNextToken()) {
            case 'n':
                if (reader.wasNull()) return null;
                throw new java.io.IOException("Invalid null value found at: " + reader.positionInStream());
            case '{':
                reader.getNextToken();
                return new serializers.dslplatform.media.MediaContent(reader, locator);
            case '[':
                return reader.deserializeNullableCollection(JSON_READER);
            default:
                throw new java.io.IOException("Invalid char value found at: " + reader.positionInStream()
                        + ". Expecting null, { or [. Found: " + (char) reader.last());
        }
    }
}
