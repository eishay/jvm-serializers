package data.media;

public class Image
{
	public enum Size {
		SMALL, LARGE
	}

	public String uri;
	public String title;  // Can be null
	public int width;
	public int height;
	public Size size;

	public Image (String uri, String title, int width, int height, Size size) {
		this.height = height;
		this.title = title;
		this.uri = uri;
		this.width = width;
		this.size = size;
	}

	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		result = prime * result + width;
		return result;
	}

	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Image other = (Image)obj;
		if (height != other.height) return false;
		if (size == null) {
			if (other.size != null) return false;
		} else if (!size.equals(other.size)) return false;
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
		sb.append("[Image ");
		sb.append("uri=").append(uri);
		sb.append(", title=").append(title);
		sb.append(", width=").append(width);
		sb.append(", height=").append(height);
		sb.append(", size=").append(size);
		sb.append("]");
		return sb.toString();
	}
}
