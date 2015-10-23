package serializers.xml;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbMediaContent
{
	public JaxbMedia media;
	public List<serializers.xml.JaxbImage> images;

	public JaxbMediaContent() {}

	public JaxbMediaContent(JaxbMedia media, List<serializers.xml.JaxbImage> images) {
		this.media = media;
		this.images = images;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JaxbMediaContent that = (JaxbMediaContent) o;

		if (images != null ? !images.equals(that.images) : that.images != null) return false;
		if (media != null ? !media.equals(that.media) : that.media != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = media != null ? media.hashCode() : 0;
		result = 31 * result + (images != null ? images.hashCode() : 0);
		return result;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[MediaContent: ");
		sb.append("media=").append(media);
		sb.append(", images=").append(images);
		sb.append("]");
		return sb.toString();
	}

    public void setMedia(JaxbMedia media) {
        this.media = media;
    }

    public void setImages(List<serializers.xml.JaxbImage> images) {
        this.images = images;
    }

    public JaxbMedia getMedia() {
        return media;
    }

    public List<serializers.xml.JaxbImage> getImages() {
        return images;
    }
}
