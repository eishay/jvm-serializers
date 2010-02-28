package data.media;

import java.util.List;

public class MediaContent implements java.io.Serializable
{
	public Media media;
	public List<Image> images;

	public MediaContent() {}

	public MediaContent (Media media, List<Image> images) {
		this.media = media;
		this.images = images;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((images == null) ? 0 : images.hashCode());
		result = prime * result + ((media == null) ? 0 : media.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MediaContent other = (MediaContent)obj;
		if (images == null) {
			if (other.images != null) return false;
		} else if (!images.equals(other.images)) return false;
		if (media == null) {
			if (other.media != null) return false;
		} else if (!media.equals(other.media)) return false;
		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[MediaContent: ");
		sb.append("media=").append(media);
		sb.append(", images=").append(images);
		sb.append("]");
		return sb.toString();
	}
}
