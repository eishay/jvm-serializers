package data.media;

import java.util.List;

public class Media implements java.io.Serializable {
	public enum Player {
		JAVA, FLASH
	}

	public String uri;
	public String title;        // Can be unset.
	public int width;
	public int height;
	public String format;
	public long duration;
	public long size;
	public int bitrate;         // Can be unset.
	public boolean hasBitrate;
	public List<String> persons;
	public Player player;
	public String copyright;    // Can be unset.

	public Media() {}

	public Media(String uri, String title, int width, int height, String format, long duration, long size, int bitrate, boolean hasBitrate, List<String> persons, Player player, String copyright)
	{
		this.uri = uri;
		this.title = title;
		this.width = width;
		this.height = height;
		this.format = format;
		this.duration = duration;
		this.size = size;
		this.bitrate = bitrate;
		this.hasBitrate = hasBitrate;
		this.persons = persons;
		this.player = player;
		this.copyright = copyright;
	}

	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + bitrate;
		result = prime * result + ((copyright == null) ? 0 : copyright.hashCode());
		result = prime * result + (int)(duration ^ (duration >>> 32));
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + height;
		result = prime * result + ((persons == null) ? 0 : persons.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result + (int)(size ^ (size >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + width;
		return result;
	}

	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Media other = (Media)obj;
		if (hasBitrate != other.hasBitrate) return false;
		if (hasBitrate && (bitrate != other.bitrate)) return false;
		if (copyright == null) {
			if (other.copyright != null) return false;
		} else if (!copyright.equals(other.copyright)) return false;
		if (duration != other.duration) return false;
		if (format == null) {
			if (other.format != null) return false;
		} else if (!format.equals(other.format)) return false;
		if (height != other.height) return false;
		if (persons == null) {
			if (other.persons != null) return false;
		} else if (!persons.equals(other.persons)) return false;
		if (player == null) {
			if (other.player != null) return false;
		} else if (!player.equals(other.player)) return false;
		if (size != other.size) return false;
		if (title == null) {
			if (other.title != null) return false;
		} else if (!title.equals(other.title)) return false;
		if (uri == null) {
			if (other.uri != null) return false;
		} else if (!uri.equals(other.uri)) return false;
		if (width != other.width) return false;
		return true;
	}

	public String toString () {
		StringBuilder sb = new StringBuilder();
		sb.append("[Media ");
		sb.append("uri=").append(uri);
		sb.append(", title=").append(title);
		sb.append(", width=").append(width);
		sb.append(", height=").append(height);
		sb.append(", format=").append(format);
		sb.append(", duration=").append(duration);
		sb.append(", size=").append(size);
		sb.append(", bitrate=").append(hasBitrate ? Integer.toString(bitrate) : "?");
		sb.append(", persons=").append(persons);
		sb.append(", player=").append(player);
		sb.append(", copyright=").append(copyright);
		sb.append("]");
		return sb.toString();
	}
}
