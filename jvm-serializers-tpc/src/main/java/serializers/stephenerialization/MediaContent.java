package serializers.stephenerialization;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.enragedginger.stephenerialization.StephenerializationLookupService;
import com.enragedginger.stephenerialization.StephenerializationService;
import com.enragedginger.stephenerialization.annotations.Stephenerializable;
import com.enragedginger.stephenerialization.annotations.Stephenerialize;

@Stephenerializable(version = 20121225)
public class MediaContent implements java.io.Serializable
{

    private static final long serialVersionUID = 1L;

    @Stephenerialize(minVersion = 20121225, priority = 1)
    public Media media;
    @Stephenerialize(minVersion = 20121225, priority = 2)
    public List<Image> images;

    public MediaContent() {}

    public MediaContent (Media media, List<Image> images) {
        this.media = media;
        this.images = images;
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

    /**
     * Writes this object out to the stream using Stephenerialization.
     * @param streamer The output stream to use.
     */
    private void writeObject(ObjectOutputStream streamer) {
        MediaContentStephenerializer.stephenerialize(this, streamer);
        /*final StephenerializationService service = StephenerializationLookupService.lookup();
        service.stephenerialize(this, streamer, MediaContent.class);*/
    }

    /**
     * Reads this object from the stream using Stephenerialization.
     * @param streamer The input stream to use.
     */
    private void readObject(ObjectInputStream streamer) {
        MediaContentStephenerializer.destephenerialize(this, streamer);
        /*final StephenerializationService service = StephenerializationLookupService.lookup();
        service.destephenerialize(this, streamer, MediaContent.class);*/
    }
}