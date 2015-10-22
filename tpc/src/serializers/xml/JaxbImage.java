package serializers.xml;

import javax.xml.bind.annotation.*;

import static data.ReprUtil.repr;

@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbImage implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

	@XmlTransient
    public enum Size {
		SMALL, LARGE
	}

	@XmlElement
	public String uri;
	@XmlElement
	public String title;  // Can be null
	@XmlElement
	public int width;
	@XmlElement
	public int height;
	@XmlElement
	public Size size;

	public JaxbImage() {}

	public JaxbImage(String uri, String title, int width, int height, Size size) {
		this.height = height;
		this.title = title;
		this.uri = uri;
		this.width = width;
		this.size = size;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JaxbImage image = (JaxbImage) o;

		if (height != image.height) return false;
		if (width != image.width) return false;
		if (size != image.size) return false;
		if (title != null ? !title.equals(image.title) : image.title != null) return false;
		if (uri != null ? !uri.equals(image.uri) : image.uri != null) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = uri != null ? uri.hashCode() : 0;
		result = 31 * result + (title != null ? title.hashCode() : 0);
		result = 31 * result + width;
		result = 31 * result + height;
		result = 31 * result + (size != null ? size.hashCode() : 0);
		return result;
	}

	public String toString () {
		StringBuilder sb = new StringBuilder();
		sb.append("[Image ");
		sb.append("uri=").append(repr(uri));
		sb.append(", title=").append(repr(title));
		sb.append(", width=").append(width);
		sb.append(", height=").append(height);
		sb.append(", size=").append(size);
		sb.append("]");
		return sb.toString();
	}

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Size getSize() {
        return size;
    }
}
