package serializers.coherence.media;

import com.seovic.pof.annotations.PortableType;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;

import static data.ReprUtil.repr;

@PortableType(id=20002, version = 1)
public class Image
{
	private static final long serialVersionUID = 1L;


	public String uri;

	public String title;  // Can be null
	public int width;
	public int height;
	public Size size;

	public Image() {}

	public Image (String uri, String title, int width, int height, Size size) {
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

		Image image = (Image) o;

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

	private void readExternal(PofReader pofReader)
			throws IOException
	{
		if (pofReader.getVersionId()>= (1)) {

			this.uri = pofReader.readString(0);
			this.title = pofReader.readString(1);
			this.width = pofReader.readInt(2);
			this.height = pofReader.readInt(3);
			this.size = (Size) pofReader.readObject(4);
		}

	}

	private void writeExternal(PofWriter pofWriter)
			throws IOException
	{

		pofWriter.writeString(0, this.uri);
		pofWriter.writeString(1, this.title);
		pofWriter.writeInt(2, this.width);
		pofWriter.writeInt(3, this.height);
		pofWriter.writeObject(4, this.size);

	}
}
