package serializers.dslplatform.minified;



public final class MediaContentMinified   implements java.lang.Cloneable, java.io.Serializable, com.dslplatform.json.JsonObject {
	
	
	
	public MediaContentMinified(
			final serializers.dslplatform.minified.MediaMinified media,
			final java.util.List<serializers.dslplatform.minified.ImageMinified> images) {
			
		setMedia(media);
		setImages(images);
	}

	
	
	public MediaContentMinified() {
			
		this.media = new serializers.dslplatform.minified.MediaMinified();
		this.images = new java.util.ArrayList<serializers.dslplatform.minified.ImageMinified>(4);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + 1284860741;
		result = prime * result + (this.media.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MediaContentMinified))
			return false;
		return deepEquals((MediaContentMinified) obj);
	}

	public boolean deepEquals(final MediaContentMinified other) {
		if (other == null)
			return false;
		
		if(!(this.media.equals(other.media)))
			return false;
		if(!((this.images == other.images || this.images != null && this.images.equals(other.images))))
			return false;
		return true;
	}

	private MediaContentMinified(MediaContentMinified other) {
		
		this.media = (serializers.dslplatform.minified.MediaMinified)(other.media.clone());
		this.images = new java.util.ArrayList<serializers.dslplatform.minified.ImageMinified>(other.images.size());
			if (other.images != null) {
				for (serializers.dslplatform.minified.ImageMinified it : other.images) {
					this.images.add((serializers.dslplatform.minified.ImageMinified)it.clone());
				}
			};
	}

	@Override
	public Object clone() {
		return new MediaContentMinified(this);
	}

	@Override
	public String toString() {
		return "MediaContentMinified(" + media + ',' + images + ')';
	}
	private static final long serialVersionUID = -1681424374773950534L;
	
	private serializers.dslplatform.minified.MediaMinified media;

	
	public serializers.dslplatform.minified.MediaMinified getMedia()  {
		
		return media;
	}

	
	public MediaContentMinified setMedia(final serializers.dslplatform.minified.MediaMinified value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"media\" cannot be null!");
		this.media = value;
		
		return this;
	}

	
	private java.util.List<serializers.dslplatform.minified.ImageMinified> images;

	
	public java.util.List<serializers.dslplatform.minified.ImageMinified> getImages()  {
		
		return images;
	}

	
	public MediaContentMinified setImages(final java.util.List<serializers.dslplatform.minified.ImageMinified> value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"images\" cannot be null!");
		serializers.dslplatform.Guards.checkNulls(value);
		this.images = value;
		
		return this;
	}

	
	public void serialize(final com.dslplatform.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(this, sw, false);
		} else {
			__serializeJsonObjectFull(this, sw, false);
		}
		sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
	}

	static void __serializeJsonObjectMinimal(final MediaContentMinified self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
		
		sw.writeAscii("\"m\":{", 5);
		
					serializers.dslplatform.minified.MediaMinified.__serializeJsonObjectMinimal(self.media, sw, false);
					sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
		
		if(self.images.size() != 0) {
			sw.writeAscii(",\"i\":[", 6);
			serializers.dslplatform.minified.ImageMinified item = self.images.get(0);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
				serializers.dslplatform.minified.ImageMinified.__serializeJsonObjectMinimal(item, sw, false);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
			for(int i = 1; i < self.images.size(); i++) {
				sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);	
				item = self.images.get(i);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
				serializers.dslplatform.minified.ImageMinified.__serializeJsonObjectMinimal(item, sw, false);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
			}
			sw.writeByte(com.dslplatform.json.JsonWriter.ARRAY_END);
		}
	}

	static void __serializeJsonObjectFull(final MediaContentMinified self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
		
		sw.writeAscii("\"m\":{", 5);
		
					serializers.dslplatform.minified.MediaMinified.__serializeJsonObjectFull(self.media, sw, false);
					sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
		
		if(self.images.size() != 0) {
			sw.writeAscii(",\"i\":[", 6);
			serializers.dslplatform.minified.ImageMinified item = self.images.get(0);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
				serializers.dslplatform.minified.ImageMinified.__serializeJsonObjectFull(item, sw, false);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
			for(int i = 1; i < self.images.size(); i++) {
				sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);	
				item = self.images.get(i);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
				serializers.dslplatform.minified.ImageMinified.__serializeJsonObjectFull(item, sw, false);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
			}
			sw.writeByte(com.dslplatform.json.JsonWriter.ARRAY_END);
		}
		else sw.writeAscii(",\"i\":[]", 7);
	}

	public static final com.dslplatform.json.JsonReader.ReadJsonObject<MediaContentMinified> JSON_READER = new com.dslplatform.json.JsonReader.ReadJsonObject<MediaContentMinified>() {
		@Override
		public MediaContentMinified deserialize(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			return new serializers.dslplatform.minified.MediaContentMinified(reader);
		}
	};

	private MediaContentMinified(final com.dslplatform.json.JsonReader<Object> reader) throws java.io.IOException {
		
		serializers.dslplatform.minified.MediaMinified _media_ = null;
		java.util.List<serializers.dslplatform.minified.ImageMinified> _images_ = new java.util.ArrayList<serializers.dslplatform.minified.ImageMinified>(4);
		byte nextToken = reader.last();
		if(nextToken != '}') {
			int nameHash = reader.fillName();
			nextToken = reader.getNextToken();
			if(nextToken == 'n') {
				if (reader.wasNull()) {
					nextToken = reader.getNextToken();
				} else {
					throw new java.io.IOException("Expecting 'u' (as null) at position " + reader.positionInStream() + ". Found " + (char)nextToken);
				}
			} else {
				switch(nameHash) {
					
					case -401854600:
						
					if (nextToken == '{') {
						reader.getNextToken();
						_media_ = serializers.dslplatform.minified.MediaMinified.JSON_READER.deserialize(reader);
						nextToken = reader.getNextToken();
					} else throw new java.io.IOException("Expecting '{' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
						break;
					case -334744124:
						
					if (nextToken == '[') {
						nextToken = reader.getNextToken();
						if (nextToken != ']') {
							reader.deserializeCollection(serializers.dslplatform.minified.ImageMinified.JSON_READER, _images_);
						}
						nextToken = reader.getNextToken();
					} else throw new java.io.IOException("Expecting '[' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
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
				if(nextToken == 'n') {
					if (reader.wasNull()) {
						nextToken = reader.getNextToken();
						continue;
					} else {
						throw new java.io.IOException("Expecting 'u' (as null) at position " + reader.positionInStream() + ". Found " + (char)nextToken);
					}
				}
				switch(nameHash) {
					
					case -401854600:
						
					if (nextToken == '{') {
						reader.getNextToken();
						_media_ = serializers.dslplatform.minified.MediaMinified.JSON_READER.deserialize(reader);
						nextToken = reader.getNextToken();
					} else throw new java.io.IOException("Expecting '{' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
						break;
					case -334744124:
						
					if (nextToken == '[') {
						nextToken = reader.getNextToken();
						if (nextToken != ']') {
							reader.deserializeCollection(serializers.dslplatform.minified.ImageMinified.JSON_READER, _images_);
						}
						nextToken = reader.getNextToken();
					} else throw new java.io.IOException("Expecting '[' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
						break;
					default:
						nextToken = reader.skip();
						break;
				}
			}
			if (nextToken != '}') {
				throw new java.io.IOException("Expecting '}' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
			}
		}
		
		this.media = _media_;
		this.images = _images_;
	}

	public static Object deserialize(final com.dslplatform.json.JsonReader<Object> reader) throws java.io.IOException {
		switch (reader.getNextToken()) {
			case 'n':
				if (reader.wasNull())
					return null;
				throw new java.io.IOException("Invalid null value found at: " + reader.positionInStream());
			case '{':
				reader.getNextToken();
				return new serializers.dslplatform.minified.MediaContentMinified(reader);
			case '[':
				return reader.deserializeNullableCollection(JSON_READER);
			default:
				throw new java.io.IOException("Invalid char value found at: " + reader.positionInStream() + ". Expecting null, { or [. Found: " + (char)reader.last());
		}
	}
}
