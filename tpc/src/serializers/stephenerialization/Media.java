package serializers.stephenerialization;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.enragedginger.stephenerialization.StephenerializationLookupService;
import com.enragedginger.stephenerialization.StephenerializationService;
import com.enragedginger.stephenerialization.annotations.Stephenerializable;
import com.enragedginger.stephenerialization.annotations.Stephenerialize;

import static data.ReprUtil.repr;

@Stephenerializable(version = 20121225)
public class Media implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public enum Player {
        JAVA, FLASH;

        public static Player find(String str) {
            if (str == "JAVA") return JAVA;
            if (str == "FLASH") return FLASH;
            if ("JAVA".equals(str)) return JAVA;
            if ("FLASH".equals(str)) return FLASH;
            String desc = (str == null) ? "NULL" : String.format("'%s'", str);
            throw new IllegalArgumentException("No Player value of "+desc);
        }
    }

    @Stephenerialize(minVersion = 20121225, priority = 1)
    public String uri;
    @Stephenerialize(minVersion = 20121225, priority = 2)
    public String title;        // Can be unset.
    @Stephenerialize(minVersion = 20121225, priority = 3)
    public int width;
    @Stephenerialize(minVersion = 20121225, priority = 4)
    public int height;
    @Stephenerialize(minVersion = 20121225, priority = 5)
    public String format;
    @Stephenerialize(minVersion = 20121225, priority = 6)
    public long duration;
    @Stephenerialize(minVersion = 20121225, priority = 7)
    public long size;
    @Stephenerialize(minVersion = 20121225, priority = 8)
    public int bitrate;         // Can be unset.

    @Stephenerialize(minVersion = 20121225, priority = 9)
    public boolean hasBitrate;

    @Stephenerialize(minVersion = 20121225, priority = 10)
    public List<String> persons;

    @Stephenerialize(minVersion = 20121225, priority = 11)
    public Player player;

    @Stephenerialize(minVersion = 20121225, priority = 12)
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Media media = (Media) o;

        if (bitrate != media.bitrate) return false;
        if (duration != media.duration) return false;
        if (hasBitrate != media.hasBitrate) return false;
        if (height != media.height) return false;
        if (size != media.size) return false;
        if (width != media.width) return false;
        if (copyright != null ? !copyright.equals(media.copyright) : media.copyright != null) return false;
        if (format != null ? !format.equals(media.format) : media.format != null) return false;
        if (persons != null ? !persons.equals(media.persons) : media.persons != null) return false;
        if (player != media.player) return false;
        if (title != null ? !title.equals(media.title) : media.title != null) return false;
        if (uri != null ? !uri.equals(media.uri) : media.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + (format != null ? format.hashCode() : 0);
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + bitrate;
        result = 31 * result + (hasBitrate ? 1 : 0);
        result = 31 * result + (persons != null ? persons.hashCode() : 0);
        result = 31 * result + (player != null ? player.hashCode() : 0);
        result = 31 * result + (copyright != null ? copyright.hashCode() : 0);
        return result;
    }

    public String toString () {
        StringBuilder sb = new StringBuilder();
        sb.append("[Media ");
        sb.append("uri=").append(repr(uri));
        sb.append(", title=").append(repr(title));
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", format=").append(repr(format));
        sb.append(", duration=").append(duration);
        sb.append(", size=").append(size);
        sb.append(", hasBitrate=").append(hasBitrate);
        sb.append(", bitrate=").append(String.valueOf(bitrate));
        sb.append(", persons=").append(repr(persons));
        sb.append(", player=").append(player);
        sb.append(", copyright=").append(repr(copyright));
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

    public void setFormat(String format) {
        this.format = format;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
        this.hasBitrate = true;
    }

    public void setPersons(List<String> persons) {
        this.persons = persons;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
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

    public String getFormat() {
        return format;
    }

    public long getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

    public int getBitrate() {
        return bitrate;
    }

    public List<String> getPersons() {
        return persons;
    }

    public Player getPlayer() {
        return player;
    }

    public String getCopyright() {
        return copyright;
    }

    public boolean isHasBitrate() {
        return hasBitrate;
    }

    public void setHasBitrate(boolean hasBitrate) {
        this.hasBitrate = hasBitrate;
    }

    /**
     * Writes this object out to the stream using Stephenerialization.
     * @param streamer The output stream to use.
     */
    private void writeObject(ObjectOutputStream streamer) {
        MediaStephenerializer.stephenerialize(this, streamer);
        /*final StephenerializationService service = StephenerializationLookupService.lookup();
        service.stephenerialize(this, streamer, Media.class);*/
    }

    /**
     * Reads this object from the stream using Stephenerialization.
     * @param streamer The input stream to use.
     */
    private void readObject(ObjectInputStream streamer) {
        MediaStephenerializer.destephenerialize(this, streamer);
        /*final StephenerializationService service = StephenerializationLookupService.lookup();
        service.destephenerialize(this, streamer, Media.class);*/
    }
}