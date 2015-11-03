package serializers.dslplatform.minified;



public final class ImageMinified   implements java.lang.Cloneable, java.io.Serializable, com.dslplatform.json.JsonObject {
	
	
	
	public ImageMinified(
			final String uri,
			final String title,
			final int width,
			final int height,
			final serializers.dslplatform.shared.Size size) {
			
		setUri(uri);
		setTitle(title);
		setWidth(width);
		setHeight(height);
		setSize(size);
	}

	
	
	public ImageMinified() {
			
		this.uri = "";
		this.width = 0;
		this.height = 0;
		this.size = serializers.dslplatform.shared.Size.SMALL;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + 2063000861;
		result = prime * result + (this.uri.hashCode());
		result = prime * result + (this.title != null ? this.title.hashCode() : 0);
		result = prime * result + (this.width);
		result = prime * result + (this.height);
		result = prime * result + (this.size.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ImageMinified))
			return false;
		return deepEquals((ImageMinified) obj);
	}

	public boolean deepEquals(final ImageMinified other) {
		if (other == null)
			return false;
		
		if(!(this.uri.equals(other.uri)))
			return false;
		if(!(this.title == other.title || this.title != null && this.title.equals(other.title)))
			return false;
		if(!(this.width == other.width))
			return false;
		if(!(this.height == other.height))
			return false;
		if(!(this.size.equals(other.size)))
			return false;
		return true;
	}

	private ImageMinified(ImageMinified other) {
		
		this.uri = other.uri;
		this.title = other.title;
		this.width = other.width;
		this.height = other.height;
		this.size = other.size;
	}

	@Override
	public Object clone() {
		return new ImageMinified(this);
	}

	@Override
	public String toString() {
		return "ImageMinified(" + uri + ',' + title + ',' + width + ',' + height + ',' + size + ')';
	}
	private static final long serialVersionUID = -3855056898654185702L;
	
	private String uri;

	
	public String getUri()  {
		
		return uri;
	}

	
	public ImageMinified setUri(final String value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"uri\" cannot be null!");
		this.uri = value;
		
		return this;
	}

	
	private String title;

	
	public String getTitle()  {
		
		return title;
	}

	
	public ImageMinified setTitle(final String value) {
		
		this.title = value;
		
		return this;
	}

	
	private int width;

	
	public int getWidth()  {
		
		return width;
	}

	
	public ImageMinified setWidth(final int value) {
		
		this.width = value;
		
		return this;
	}

	
	private int height;

	
	public int getHeight()  {
		
		return height;
	}

	
	public ImageMinified setHeight(final int value) {
		
		this.height = value;
		
		return this;
	}

	
	private serializers.dslplatform.shared.Size size;

	
	public serializers.dslplatform.shared.Size getSize()  {
		
		return size;
	}

	
	public ImageMinified setSize(final serializers.dslplatform.shared.Size value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"size\" cannot be null!");
		this.size = value;
		
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

	static void __serializeJsonObjectMinimal(final ImageMinified self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (!(self.uri.length() == 0)) {
			hasWrittenProperty = true;
				sw.writeAscii("\"u\":", 4);
				sw.writeString(self.uri);
			}
		
			if (self.title != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"t\":", 4);
				sw.writeString(self.title);
			}
		
			if (self.width != 0) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"w\":", 4);
				com.dslplatform.json.NumberConverter.serialize(self.width, sw);
			}
		
			if (self.height != 0) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"h\":", 4);
				com.dslplatform.json.NumberConverter.serialize(self.height, sw);
			}
		
		if(self.size != serializers.dslplatform.shared.Size.SMALL) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
			sw.writeAscii("\"s\":\"LARGE\"", 11);
		}
	}

	static void __serializeJsonObjectFull(final ImageMinified self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			sw.writeAscii("\"u\":", 4);
			sw.writeString(self.uri);
		
			
			if (self.title != null) {
				sw.writeAscii(",\"t\":", 5);
				sw.writeString(self.title);
			} else {
				sw.writeAscii(",\"t\":null", 9);
			}
		
			
			sw.writeAscii(",\"w\":", 5);
			com.dslplatform.json.NumberConverter.serialize(self.width, sw);
		
			
			sw.writeAscii(",\"h\":", 5);
			com.dslplatform.json.NumberConverter.serialize(self.height, sw);
		
		
		sw.writeAscii(",\"s\":\"", 6);
		sw.writeAscii(self.size.name());
		sw.writeByte(com.dslplatform.json.JsonWriter.QUOTE);
	}

	public static final com.dslplatform.json.JsonReader.ReadJsonObject<ImageMinified> JSON_READER = new com.dslplatform.json.JsonReader.ReadJsonObject<ImageMinified>() {
		@Override
		public ImageMinified deserialize(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			return new serializers.dslplatform.minified.ImageMinified(reader);
		}
	};

	private ImageMinified(final com.dslplatform.json.JsonReader<Object> reader) throws java.io.IOException {
		
		String _uri_ = "";
		String _title_ = null;
		int _width_ = 0;
		int _height_ = 0;
		serializers.dslplatform.shared.Size _size_ = serializers.dslplatform.shared.Size.SMALL;
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
					
					case -267633648:
						_uri_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case -250856029:
						_title_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case -234078410:
						_width_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case -317966505:
						_height_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case -166967934:
						
					if (nextToken == '"') {
						switch(reader.calcHash()) {
							case -2045258836: _size_ = serializers.dslplatform.shared.Size.SMALL; break;
							case -1271305644: _size_ = serializers.dslplatform.shared.Size.LARGE; break;
							default:
								throw new java.io.IOException("Unknown enum value: '" + reader.getLastName() + "' at position " + reader.positionInStream());
						}
						nextToken = reader.getNextToken();
					} else throw new java.io.IOException("Expecting '\"' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
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
					
					case -267633648:
						_uri_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case -250856029:
						_title_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case -234078410:
						_width_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case -317966505:
						_height_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case -166967934:
						
					if (nextToken == '"') {
						switch(reader.calcHash()) {
							case -2045258836: _size_ = serializers.dslplatform.shared.Size.SMALL; break;
							case -1271305644: _size_ = serializers.dslplatform.shared.Size.LARGE; break;
							default:
								throw new java.io.IOException("Unknown enum value: '" + reader.getLastName() + "' at position " + reader.positionInStream());
						}
						nextToken = reader.getNextToken();
					} else throw new java.io.IOException("Expecting '\"' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
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
		
		this.uri = _uri_;
		this.title = _title_;
		this.width = _width_;
		this.height = _height_;
		this.size = _size_;
	}

	public static Object deserialize(final com.dslplatform.json.JsonReader<Object> reader) throws java.io.IOException {
		switch (reader.getNextToken()) {
			case 'n':
				if (reader.wasNull())
					return null;
				throw new java.io.IOException("Invalid null value found at: " + reader.positionInStream());
			case '{':
				reader.getNextToken();
				return new serializers.dslplatform.minified.ImageMinified(reader);
			case '[':
				return reader.deserializeNullableCollection(JSON_READER);
			default:
				throw new java.io.IOException("Invalid char value found at: " + reader.positionInStream() + ". Expecting null, { or [. Found: " + (char)reader.last());
		}
	}
}
