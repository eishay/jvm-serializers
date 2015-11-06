package serializers.coherence.media;

import com.seovic.pof.annotations.PortableType;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@PortableType(id=20004, version = 1)
public class MediaContent
{
	public Media media;
	public List<Image> images = new ArrayList<>();

	public MediaContent() {}

	public MediaContent (Media media, List<Image> images) {
		this.media = media;
		this.images = images;
	}

	public int getImageSize() {
		return (this.images == null) ? 0 : this.images.size();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MediaContent that = (MediaContent) o;

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

	public void setMedia(Media media) {
		this.media = media;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public Media getMedia() {
		return media;
	}

	public List<Image> getImages() {
		return images;
	}

	private void readExternal(PofReader pofReader)
			throws IOException
	{
		if (pofReader.getVersionId()>= (1)) {

			this.media = (Media)pofReader.readObject(0);
			this.images = (List<Image>) pofReader.readCollection(1,null );
		}

	}

	private void writeExternal(PofWriter pofWriter)
			throws IOException
	{

		pofWriter.writeObject(0, this.media);
		pofWriter.writeCollection(1, this.images);

	}
}
