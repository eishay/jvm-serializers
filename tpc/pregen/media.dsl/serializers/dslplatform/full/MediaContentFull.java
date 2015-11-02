package serializers.dslplatform.full;



public final class MediaContentFull   implements java.lang.Cloneable, java.io.Serializable, com.dslplatform.json.JsonObject {
	
	
	
	public MediaContentFull(
			final serializers.dslplatform.full.MediaFull media,
			final java.util.List<serializers.dslplatform.full.ImageFull> images) {
			
		setMedia(media);
		setImages(images);
	}

	
	
	public MediaContentFull() {
			
		this.media = new serializers.dslplatform.full.MediaFull();
		this.images = new java.util.ArrayList<serializers.dslplatform.full.ImageFull>(4);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + 795525541;
		result = prime * result + (this.media.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MediaContentFull))
			return false;
		return deepEquals((MediaContentFull) obj);
	}

	public boolean deepEquals(final MediaContentFull other) {
		if (other == null)
			return false;
		
		if(!(this.media.equals(other.media)))
			return false;
		if(!((this.images == other.images || this.images != null && this.images.equals(other.images))))
			return false;
		return true;
	}

	private MediaContentFull(MediaContentFull other) {
		
		this.media = (serializers.dslplatform.full.MediaFull)(other.media.clone());
		this.images = new java.util.ArrayList<serializers.dslplatform.full.ImageFull>(other.images.size());
			if (other.images != null) {
				for (serializers.dslplatform.full.ImageFull it : other.images) {
					this.images.add((serializers.dslplatform.full.ImageFull)it.clone());
				}
			};
	}

	@Override
	public Object clone() {
		return new MediaContentFull(this);
	}

	@Override
	public String toString() {
		return "MediaContentFull(" + media + ',' + images + ')';
	}
	private static final long serialVersionUID = -7505023629908384582L;
	
	private serializers.dslplatform.full.MediaFull media;

	
	public serializers.dslplatform.full.MediaFull getMedia()  {
		
		return media;
	}

	
	public MediaContentFull setMedia(final serializers.dslplatform.full.MediaFull value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"media\" cannot be null!");
		this.media = value;
		
		return this;
	}

	
	private java.util.List<serializers.dslplatform.full.ImageFull> images;

	
	public java.util.List<serializers.dslplatform.full.ImageFull> getImages()  {
		
		return images;
	}

	
	public MediaContentFull setImages(final java.util.List<serializers.dslplatform.full.ImageFull> value) {
		
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

	static void __serializeJsonObjectMinimal(final MediaContentFull self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
		
		sw.writeAscii("\"media\":{", 9);
		
					serializers.dslplatform.full.MediaFull.__serializeJsonObjectMinimal(self.media, sw, false);
					sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
		
		if(self.images.size() != 0) {
			sw.writeAscii(",\"images\":[", 11);
			serializers.dslplatform.full.ImageFull item = self.images.get(0);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
				serializers.dslplatform.full.ImageFull.__serializeJsonObjectMinimal(item, sw, false);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
			for(int i = 1; i < self.images.size(); i++) {
				sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);	
				item = self.images.get(i);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
				serializers.dslplatform.full.ImageFull.__serializeJsonObjectMinimal(item, sw, false);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
			}
			sw.writeByte(com.dslplatform.json.JsonWriter.ARRAY_END);
		}
	}

	static void __serializeJsonObjectFull(final MediaContentFull self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
		
		sw.writeAscii("\"media\":{", 9);
		
					serializers.dslplatform.full.MediaFull.__serializeJsonObjectFull(self.media, sw, false);
					sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
		
		if(self.images.size() != 0) {
			sw.writeAscii(",\"images\":[", 11);
			serializers.dslplatform.full.ImageFull item = self.images.get(0);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
				serializers.dslplatform.full.ImageFull.__serializeJsonObjectFull(item, sw, false);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
			for(int i = 1; i < self.images.size(); i++) {
				sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);	
				item = self.images.get(i);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_START);
				serializers.dslplatform.full.ImageFull.__serializeJsonObjectFull(item, sw, false);
				sw.writeByte(com.dslplatform.json.JsonWriter.OBJECT_END);
			}
			sw.writeByte(com.dslplatform.json.JsonWriter.ARRAY_END);
		}
		else sw.writeAscii(",\"images\":[]", 12);
	}

	public static final com.dslplatform.json.JsonReader.ReadJsonObject<MediaContentFull> JSON_READER = new com.dslplatform.json.JsonReader.ReadJsonObject<MediaContentFull>() {
		@Override
		public MediaContentFull deserialize(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			return new serializers.dslplatform.full.MediaContentFull(reader);
		}
	};

	private MediaContentFull(final com.dslplatform.json.JsonReader<Object> reader) throws java.io.IOException {
		
		serializers.dslplatform.full.MediaFull _media_ = null;
		java.util.List<serializers.dslplatform.full.ImageFull> _images_ = new java.util.ArrayList<serializers.dslplatform.full.ImageFull>(4);
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
					
					case -56959229:
						
					if (nextToken == '{') {
						reader.getNextToken();
						_media_ = serializers.dslplatform.full.MediaFull.JSON_READER.deserialize(reader);
						nextToken = reader.getNextToken();
					} else throw new java.io.IOException("Expecting '{' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
						break;
					case -774420821:
						
					if (nextToken == '[') {
						nextToken = reader.getNextToken();
						if (nextToken != ']') {
							reader.deserializeCollection(serializers.dslplatform.full.ImageFull.JSON_READER, _images_);
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
					
					case -56959229:
						
					if (nextToken == '{') {
						reader.getNextToken();
						_media_ = serializers.dslplatform.full.MediaFull.JSON_READER.deserialize(reader);
						nextToken = reader.getNextToken();
					} else throw new java.io.IOException("Expecting '{' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
						break;
					case -774420821:
						
					if (nextToken == '[') {
						nextToken = reader.getNextToken();
						if (nextToken != ']') {
							reader.deserializeCollection(serializers.dslplatform.full.ImageFull.JSON_READER, _images_);
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
				return new serializers.dslplatform.full.MediaContentFull(reader);
			case '[':
				return reader.deserializeNullableCollection(JSON_READER);
			default:
				throw new java.io.IOException("Invalid char value found at: " + reader.positionInStream() + ". Expecting null, { or [. Found: " + (char)reader.last());
		}
	}
}
