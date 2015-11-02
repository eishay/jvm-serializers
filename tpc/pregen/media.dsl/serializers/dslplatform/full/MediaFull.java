package serializers.dslplatform.full;



public final class MediaFull   implements java.lang.Cloneable, java.io.Serializable, com.dslplatform.json.JsonObject {
	
	
	
	public MediaFull(
			final String uri,
			final String title,
			final int width,
			final int height,
			final String format,
			final long duration,
			final long size,
			final int bitrate,
			final java.util.List<String> persons,
			final serializers.dslplatform.shared.Player player,
			final String copyright) {
			
		setUri(uri);
		setTitle(title);
		setWidth(width);
		setHeight(height);
		setFormat(format);
		setDuration(duration);
		setSize(size);
		setBitrate(bitrate);
		setPersons(persons);
		setPlayer(player);
		setCopyright(copyright);
	}

	
	
	public MediaFull() {
			
		this.uri = "";
		this.width = 0;
		this.height = 0;
		this.format = "";
		this.duration = 0L;
		this.size = 0L;
		this.bitrate = 0;
		this.persons = new java.util.ArrayList<String>(4);
		this.player = serializers.dslplatform.shared.Player.JAVA;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + 375800260;
		result = prime * result + (this.uri.hashCode());
		result = prime * result + (this.title != null ? this.title.hashCode() : 0);
		result = prime * result + (this.width);
		result = prime * result + (this.height);
		result = prime * result + (this.format.hashCode());
		result = prime * result + ((int) (this.duration ^ (this.duration >>> 32)));
		result = prime * result + ((int) (this.size ^ (this.size >>> 32)));
		result = prime * result + (this.bitrate);
		result = prime * result + (this.player.hashCode());
		result = prime * result + (this.copyright != null ? this.copyright.hashCode() : 0);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MediaFull))
			return false;
		return deepEquals((MediaFull) obj);
	}

	public boolean deepEquals(final MediaFull other) {
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
		if(!(this.format.equals(other.format)))
			return false;
		if(!(this.duration == other.duration))
			return false;
		if(!(this.size == other.size))
			return false;
		if(!(this.bitrate == other.bitrate))
			return false;
		if(!((this.persons == other.persons || this.persons != null && this.persons.equals(other.persons))))
			return false;
		if(!(this.player.equals(other.player)))
			return false;
		if(!(this.copyright == other.copyright || this.copyright != null && this.copyright.equals(other.copyright)))
			return false;
		return true;
	}

	private MediaFull(MediaFull other) {
		
		this.uri = other.uri;
		this.title = other.title;
		this.width = other.width;
		this.height = other.height;
		this.format = other.format;
		this.duration = other.duration;
		this.size = other.size;
		this.bitrate = other.bitrate;
		this.persons = new java.util.ArrayList<String>(other.persons);
		this.player = other.player;
		this.copyright = other.copyright;
	}

	@Override
	public Object clone() {
		return new MediaFull(this);
	}

	@Override
	public String toString() {
		return "MediaFull(" + uri + ',' + title + ',' + width + ',' + height + ',' + format + ',' + duration + ',' + size + ',' + bitrate + ',' + persons + ',' + player + ',' + copyright + ')';
	}
	private static final long serialVersionUID = 7632426365205875362L;
	
	private String uri;

	
	public String getUri()  {
		
		return uri;
	}

	
	public MediaFull setUri(final String value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"uri\" cannot be null!");
		this.uri = value;
		
		return this;
	}

	
	private String title;

	
	public String getTitle()  {
		
		return title;
	}

	
	public MediaFull setTitle(final String value) {
		
		this.title = value;
		
		return this;
	}

	
	private int width;

	
	public int getWidth()  {
		
		return width;
	}

	
	public MediaFull setWidth(final int value) {
		
		this.width = value;
		
		return this;
	}

	
	private int height;

	
	public int getHeight()  {
		
		return height;
	}

	
	public MediaFull setHeight(final int value) {
		
		this.height = value;
		
		return this;
	}

	
	private String format;

	
	public String getFormat()  {
		
		return format;
	}

	
	public MediaFull setFormat(final String value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"format\" cannot be null!");
		this.format = value;
		
		return this;
	}

	
	private long duration;

	
	public long getDuration()  {
		
		return duration;
	}

	
	public MediaFull setDuration(final long value) {
		
		this.duration = value;
		
		return this;
	}

	
	private long size;

	
	public long getSize()  {
		
		return size;
	}

	
	public MediaFull setSize(final long value) {
		
		this.size = value;
		
		return this;
	}

	
	private int bitrate;

	
	public int getBitrate()  {
		
		return bitrate;
	}

	
	public MediaFull setBitrate(final int value) {
		
		this.bitrate = value;
		
		return this;
	}

	
	private java.util.List<String> persons;

	
	public java.util.List<String> getPersons()  {
		
		return persons;
	}

	
	public MediaFull setPersons(final java.util.List<String> value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"persons\" cannot be null!");
		serializers.dslplatform.Guards.checkNulls(value);
		this.persons = value;
		
		return this;
	}

	
	private serializers.dslplatform.shared.Player player;

	
	public serializers.dslplatform.shared.Player getPlayer()  {
		
		return player;
	}

	
	public MediaFull setPlayer(final serializers.dslplatform.shared.Player value) {
		
		if(value == null) throw new IllegalArgumentException("Property \"player\" cannot be null!");
		this.player = value;
		
		return this;
	}

	
	private String copyright;

	
	public String getCopyright()  {
		
		return copyright;
	}

	
	public MediaFull setCopyright(final String value) {
		
		this.copyright = value;
		
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

	static void __serializeJsonObjectMinimal(final MediaFull self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			if (!(self.uri.length() == 0)) {
			hasWrittenProperty = true;
				sw.writeAscii("\"uri\":", 6);
				sw.writeString(self.uri);
			}
		
			if (self.title != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"title\":", 8);
				sw.writeString(self.title);
			}
		
			if (self.width != 0) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"width\":", 8);
				com.dslplatform.json.NumberConverter.serialize(self.width, sw);
			}
		
			if (self.height != 0) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"height\":", 9);
				com.dslplatform.json.NumberConverter.serialize(self.height, sw);
			}
		
			if (!(self.format.length() == 0)) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"format\":", 9);
				sw.writeString(self.format);
			}
		
			if (self.duration != 0L) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"duration\":", 11);
				com.dslplatform.json.NumberConverter.serialize(self.duration, sw);
			}
		
			if (self.size != 0L) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"size\":", 7);
				com.dslplatform.json.NumberConverter.serialize(self.size, sw);
			}
		
			if (self.bitrate != 0) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"bitrate\":", 10);
				com.dslplatform.json.NumberConverter.serialize(self.bitrate, sw);
			}
		
		if(self.persons.size() != 0) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
			sw.writeAscii("\"persons\":[", 11);
			sw.writeString(self.persons.get(0));
			for(int i = 1; i < self.persons.size(); i++) {
				sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
				sw.writeString(self.persons.get(i));
			}
			sw.writeByte(com.dslplatform.json.JsonWriter.ARRAY_END);
		}
		
		if(self.player != serializers.dslplatform.shared.Player.JAVA) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
			sw.writeAscii("\"player\":\"FLASH\"", 16);
		}
		
			if (self.copyright != null) {
			if(hasWrittenProperty) sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
			hasWrittenProperty = true;
				sw.writeAscii("\"copyright\":", 12);
				sw.writeString(self.copyright);
			}
	}

	static void __serializeJsonObjectFull(final MediaFull self, com.dslplatform.json.JsonWriter sw, boolean hasWrittenProperty) {
		
		
			
			sw.writeAscii("\"uri\":", 6);
			sw.writeString(self.uri);
		
			
			if (self.title != null) {
				sw.writeAscii(",\"title\":", 9);
				sw.writeString(self.title);
			} else {
				sw.writeAscii(",\"title\":null", 13);
			}
		
			
			sw.writeAscii(",\"width\":", 9);
			com.dslplatform.json.NumberConverter.serialize(self.width, sw);
		
			
			sw.writeAscii(",\"height\":", 10);
			com.dslplatform.json.NumberConverter.serialize(self.height, sw);
		
			
			sw.writeAscii(",\"format\":", 10);
			sw.writeString(self.format);
		
			
			sw.writeAscii(",\"duration\":", 12);
			com.dslplatform.json.NumberConverter.serialize(self.duration, sw);
		
			
			sw.writeAscii(",\"size\":", 8);
			com.dslplatform.json.NumberConverter.serialize(self.size, sw);
		
			
			sw.writeAscii(",\"bitrate\":", 11);
			com.dslplatform.json.NumberConverter.serialize(self.bitrate, sw);
		
		if(self.persons.size() != 0) {
			sw.writeAscii(",\"persons\":[", 12);
			sw.writeString(self.persons.get(0));
			for(int i = 1; i < self.persons.size(); i++) {
				sw.writeByte(com.dslplatform.json.JsonWriter.COMMA);
				sw.writeString(self.persons.get(i));
			}
			sw.writeByte(com.dslplatform.json.JsonWriter.ARRAY_END);
		}
		else sw.writeAscii(",\"persons\":[]", 13);
		
		
		sw.writeAscii(",\"player\":\"", 11);
		sw.writeAscii(self.player.name());
		sw.writeByte(com.dslplatform.json.JsonWriter.QUOTE);
		
			
			if (self.copyright != null) {
				sw.writeAscii(",\"copyright\":", 13);
				sw.writeString(self.copyright);
			} else {
				sw.writeAscii(",\"copyright\":null", 17);
			}
	}

	public static final com.dslplatform.json.JsonReader.ReadJsonObject<MediaFull> JSON_READER = new com.dslplatform.json.JsonReader.ReadJsonObject<MediaFull>() {
		@Override
		public MediaFull deserialize(final com.dslplatform.json.JsonReader reader) throws java.io.IOException {
			return new serializers.dslplatform.full.MediaFull(reader);
		}
	};

	private MediaFull(final com.dslplatform.json.JsonReader<Object> reader) throws java.io.IOException {
		
		String _uri_ = "";
		String _title_ = null;
		int _width_ = 0;
		int _height_ = 0;
		String _format_ = "";
		long _duration_ = 0L;
		long _size_ = 0L;
		int _bitrate_ = 0;
		java.util.List<String> _persons_ = new java.util.ArrayList<String>(4);
		serializers.dslplatform.shared.Player _player_ = serializers.dslplatform.shared.Player.JAVA;
		String _copyright_ = null;
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
						_uri_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case -1738164983:
						_title_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case -1786286561:
						_width_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case -708986046:
						_height_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case -1180859054:
						_format_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 799079693:
						_duration_ = com.dslplatform.json.NumberConverter.deserializeLong(reader);
					nextToken = reader.getNextToken();
						break;
					case 597743964:
						_size_ = com.dslplatform.json.NumberConverter.deserializeLong(reader);
					nextToken = reader.getNextToken();
						break;
					case 854445426:
						_bitrate_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case 181318793:
						
					if (nextToken == '[') {
						nextToken = reader.getNextToken();
						if (nextToken != ']') {
							com.dslplatform.json.StringConverter.deserializeCollection(reader, _persons_);
						}
						nextToken = reader.getNextToken();
					}
					else throw new java.io.IOException("Expecting '[' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
						break;
					case 748274432:
						
					if (nextToken == '"') {
						switch(reader.calcHash()) {
							case -247728219: _player_ = serializers.dslplatform.shared.Player.JAVA; break;
							case -1831302071: _player_ = serializers.dslplatform.shared.Player.FLASH; break;
							default:
								throw new java.io.IOException("Unknown enum value: '" + reader.getLastName() + "' at position " + reader.positionInStream());
						}
						nextToken = reader.getNextToken();
					} else throw new java.io.IOException("Expecting '\"' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
						break;
					case -1190269634:
						_copyright_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
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
						_uri_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case -1738164983:
						_title_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case -1786286561:
						_width_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case -708986046:
						_height_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case -1180859054:
						_format_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
						break;
					case 799079693:
						_duration_ = com.dslplatform.json.NumberConverter.deserializeLong(reader);
					nextToken = reader.getNextToken();
						break;
					case 597743964:
						_size_ = com.dslplatform.json.NumberConverter.deserializeLong(reader);
					nextToken = reader.getNextToken();
						break;
					case 854445426:
						_bitrate_ = com.dslplatform.json.NumberConverter.deserializeInt(reader);
					nextToken = reader.getNextToken();
						break;
					case 181318793:
						
					if (nextToken == '[') {
						nextToken = reader.getNextToken();
						if (nextToken != ']') {
							com.dslplatform.json.StringConverter.deserializeCollection(reader, _persons_);
						}
						nextToken = reader.getNextToken();
					}
					else throw new java.io.IOException("Expecting '[' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
						break;
					case 748274432:
						
					if (nextToken == '"') {
						switch(reader.calcHash()) {
							case -247728219: _player_ = serializers.dslplatform.shared.Player.JAVA; break;
							case -1831302071: _player_ = serializers.dslplatform.shared.Player.FLASH; break;
							default:
								throw new java.io.IOException("Unknown enum value: '" + reader.getLastName() + "' at position " + reader.positionInStream());
						}
						nextToken = reader.getNextToken();
					} else throw new java.io.IOException("Expecting '\"' at position " + reader.positionInStream() + ". Found " + (char)nextToken);
						break;
					case -1190269634:
						_copyright_ = com.dslplatform.json.StringConverter.deserialize(reader);
					nextToken = reader.getNextToken();
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
		this.format = _format_;
		this.duration = _duration_;
		this.size = _size_;
		this.bitrate = _bitrate_;
		this.persons = _persons_;
		this.player = _player_;
		this.copyright = _copyright_;
	}

	public static Object deserialize(final com.dslplatform.json.JsonReader<Object> reader) throws java.io.IOException {
		switch (reader.getNextToken()) {
			case 'n':
				if (reader.wasNull())
					return null;
				throw new java.io.IOException("Invalid null value found at: " + reader.positionInStream());
			case '{':
				reader.getNextToken();
				return new serializers.dslplatform.full.MediaFull(reader);
			case '[':
				return reader.deserializeNullableCollection(JSON_READER);
			default:
				throw new java.io.IOException("Invalid char value found at: " + reader.positionInStream() + ". Expecting null, { or [. Found: " + (char)reader.last());
		}
	}
}
