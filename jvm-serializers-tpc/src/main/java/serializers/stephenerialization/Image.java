package serializers.stephenerialization;

import static data.ReprUtil.repr;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.enragedginger.stephenerialization.StephenerializationLookupService;
import com.enragedginger.stephenerialization.StephenerializationService;
import com.enragedginger.stephenerialization.annotations.Stephenerializable;
import com.enragedginger.stephenerialization.annotations.Stephenerialize;

@Stephenerializable(version = 20121225)
public class Image implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    public enum Size {
        SMALL, LARGE
    }

    @Stephenerialize(minVersion = 20121225, priority = 1)
    public String uri;
    @Stephenerialize(minVersion = 20121225, priority = 2)
    public String title;  // Can be null
    @Stephenerialize(minVersion = 20121225, priority = 3)
    public int width;
    @Stephenerialize(minVersion = 20121225, priority = 4)
    public int height;
    @Stephenerialize(minVersion = 20121225, priority = 5)
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

    /**
     * Writes this object out to the stream using Stephenerialization.
     * @param streamer The output stream to use.
     */
    private void writeObject(ObjectOutputStream streamer) {
        ImageStephenerializer.stephenerialize(this, streamer);
        /*final StephenerializationService service = StephenerializationLookupService.lookup();
        service.stephenerialize(this, streamer, Image.class);*/
    }

    /**
     * Reads this object from the stream using Stephenerialization.
     * @param streamer The input stream to use.
     */
    private void readObject(ObjectInputStream streamer) {
        ImageStephenerializer.destephenerialize(this, streamer);
        /*final StephenerializationService service = StephenerializationLookupService.lookup();
        service.destephenerialize(this, streamer, Image.class);*/
    }
}