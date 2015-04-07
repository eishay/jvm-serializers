package serializers.dslplatform.media;



public final class Image   implements java.io.Serializable, com.dslplatform.client.json.JsonObject {
	
	
	
	public Image(
			final String uri,
			final String title,
			final int width,
			final int height,
			final serializers.dslplatform.media.Size size) {
			
		setUri(uri);
		setTitle(title);
		setWidth(width);
		setHeight(height);
		setSize(size);
	}

	
	
	public Image() {
			
		this.uri = "";
		this.width = 0;
		this.height = 0;
		this.size = serializers.dslplatform.media.Size.SMALL;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + 70760763;
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
		if (obj == null)
			return false;

		if (!(obj instanceof Image))
			return false;
		final Image other = (Image) obj;
		
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

	@Override
	public String toString() {
		return "Image(" + uri + ',' + title + ',' + width + ',' + height + ',' + size + ')';
	}
	
	private static final long serialVersionUID = 0x0097000a;
	
	private String uri;

	
	public String getUri()  {
		
		return uri;
	}

	
	public Image setUri(final String value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"uri\" cannot be null!");
		this.uri = value;
		
		return this;
	}

	
	private String title;

	
	public String getTitle()  {
		
		return title;
	}

	
	public Image setTitle(final String value) {
		
		this.title = value;
		
		return this;
	}

	
	private int width;

	
	public int getWidth()  {
		
		return width;
	}

	
	public Image setWidth(final int value) {
		
		this.width = value;
		
		return this;
	}

	
	private int height;

	
	public int getHeight()  {
		
		return height;
	}

	
	public Image setHeight(final int value) {
		
		this.height = value;
		
		return this;
	}

	
	private serializers.dslplatform.media.Size size;

	
	public serializers.dslplatform.media.Size getSize()  {
		
		return size;
	}

	
	public Image setSize(final serializers.dslplatform.media.Size value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"size\" cannot be null!");
		this.size = value;
		
		return this;
	}

	
	public void serialize(final com.dslplatform.client.json.JsonWriter sw, final boolean minimal) {
		sw.writeByte(com.dslplatform.client.json.JsonWriter.OBJECT_START);
		if (minimal) {
			__serializeJsonObjectMinimal(sw, false);
		} else {
			__serializeJsonObjectFull(sw, false);
		}
		sw.writeByte(com.dslplatform.client.json.JsonWriter.OBJECT_END);
	}

	void __serializeJsonObjectMinimal(com.dslplatform.client.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (!(this.uri.length() == 0)) {
				if(hasWrittenProperty) sw.writeByte(com.dslplatform.client.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
			sw.writeAscii("\"uri\":", 6);
				com.dslplatform.client.json.StringConverter.serialize(this.uri, sw);
			}
		
			if (this.title != null) {
				if(hasWrittenProperty) sw.writeByte(com.dslplatform.client.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
			sw.writeAscii("\"title\":", 8);
				com.dslplatform.client.json.StringConverter.serialize(this.title, sw);
			}
		
			if (this.width != 0) {
				if(hasWrittenProperty) sw.writeByte(com.dslplatform.client.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
			sw.writeAscii("\"width\":", 8);
				com.dslplatform.client.json.NumberConverter.serialize(this.width, sw);
			}
		
			if (this.height != 0) {
				if(hasWrittenProperty) sw.writeByte(com.dslplatform.client.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
			sw.writeAscii("\"height\":", 9);
				com.dslplatform.client.json.NumberConverter.serialize(this.height, sw);
			}
		
		if(this.size != serializers.dslplatform.media.Size.SMALL) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.client.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
			sw.writeAscii("\"size\":\"", 8);
			sw.writeAscii(this.size.name());
			sw.writeByte(com.dslplatform.client.json.JsonWriter.QUOTE);
		}
	}

	void __serializeJsonObjectFull(com.dslplatform.client.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			sw.writeAscii("\"uri\":", 6);
			com.dslplatform.client.json.StringConverter.serialize(this.uri, sw);
		
			
			if (this.title != null) {
				sw.writeAscii(",\"title\":", 9);
				com.dslplatform.client.json.StringConverter.serialize(this.title, sw);
			} else {
				sw.writeAscii(",\"title\":null", 13);
			}
		
			
			sw.writeAscii(",\"width\":", 9);
			com.dslplatform.client.json.NumberConverter.serialize(this.width, sw);
		
			
			sw.writeAscii(",\"height\":", 10);
			com.dslplatform.client.json.NumberConverter.serialize(this.height, sw);
		
		
		sw.writeAscii(",\"size\":\"", 9);
		sw.writeAscii(this.size.name());
		sw.writeByte(com.dslplatform.client.json.JsonWriter.QUOTE);
	}

	public static com.dslplatform.client.json.JsonReader.ReadJsonObject<Image> JSON_READER = new com.dslplatform.client.json.JsonReader.ReadJsonObject<Image>() {
		@Override
		public Image deserialize(final com.dslplatform.client.json.JsonReader reader, final com.dslplatform.patterns.ServiceLocator locator) throws java.io.IOException {
			return new serializers.dslplatform.media.Image(reader, locator);
		}
	};

	private Image(final com.dslplatform.client.json.JsonReader reader, final com.dslplatform.patterns.ServiceLocator _serviceLocator) throws java.io.IOException {
		
		String _uri_ = "";
		String _title_ = null;
		int _width_ = 0;
		int _height_ = 0;
		serializers.dslplatform.media.Size _size_ = serializers.dslplatform.media.Size.SMALL;
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
					
					case 932140029:
						_uri_ = com.dslplatform.client.json.StringConverter.deserialize(reader);
							nextToken = reader.getNextToken();
						break;
					case -1738164983:
						_title_ = com.dslplatform.client.json.StringConverter.deserialize(reader);
							nextToken = reader.getNextToken();
						break;
					case -1786286561:
						_width_ = com.dslplatform.client.json.NumberConverter.deserializeInt(reader);
							nextToken = reader.moveToNextToken();
						break;
					case -708986046:
						_height_ = com.dslplatform.client.json.NumberConverter.deserializeInt(reader);
							nextToken = reader.moveToNextToken();
						break;
					case 597743964:
						
					if (nextToken == '"') {
						switch(reader.calcHash()) {
							case -2045258836: _size_ = serializers.dslplatform.media.Size.SMALL; break;
							case -1271305644: _size_ = serializers.dslplatform.media.Size.LARGE; break;
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
					
					case 932140029:
						_uri_ = com.dslplatform.client.json.StringConverter.deserialize(reader);
							nextToken = reader.getNextToken();
						break;
					case -1738164983:
						_title_ = com.dslplatform.client.json.StringConverter.deserialize(reader);
							nextToken = reader.getNextToken();
						break;
					case -1786286561:
						_width_ = com.dslplatform.client.json.NumberConverter.deserializeInt(reader);
							nextToken = reader.moveToNextToken();
						break;
					case -708986046:
						_height_ = com.dslplatform.client.json.NumberConverter.deserializeInt(reader);
							nextToken = reader.moveToNextToken();
						break;
					case 597743964:
						
					if (nextToken == '"') {
						switch(reader.calcHash()) {
							case -2045258836: _size_ = serializers.dslplatform.media.Size.SMALL; break;
							case -1271305644: _size_ = serializers.dslplatform.media.Size.LARGE; break;
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

	public static Object deserialize(final com.dslplatform.client.json.JsonReader reader, final com.dslplatform.patterns.ServiceLocator locator) throws java.io.IOException {
		switch (reader.getNextToken()) {
			case 'n':
				if (reader.wasNull())
					return null;
				throw new java.io.IOException("Invalid null value found at: " + reader.positionInStream());
			case '{':
				reader.getNextToken();
				return new serializers.dslplatform.media.Image(reader, locator);
			case '[':
				return reader.deserializeNullableCollection(JSON_READER);
			default:
				throw new java.io.IOException("Invalid char value found at: " + reader.positionInStream() + ". Expecting null, { or [. Found: " + (char)reader.last());
		}
	}
}
