package serializers.coherence.media;

import com.seovic.pof.annotations.PortableType;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static data.ReprUtil.repr;

@SuppressWarnings("serial")
@PortableType(id=20000, version = 1)
public class Media  {



	public String uri;
	public String title;        // Can be unset.
	public int width;
	public int height;
	public String format;
	public long duration;
	public long size;
	public int bitrate;         // Can be unset.

	public boolean hasBitrate;

	public List<String> persons = new ArrayList<>();

	public Player player = Player.JAVA;

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

	private void readExternal(PofReader pofReader)
			throws IOException
	{
		if (pofReader.getVersionId()>= (1)) {

			this.uri = pofReader.readString(0);
			this.title = pofReader.readString(1);
			this.width = pofReader.readInt(2);
			;
			this.height = pofReader.readInt(3);
			this.format = pofReader.readString(4);
			this.duration = pofReader.readLong(5);
			this.size = pofReader.readLong(6);
			this.bitrate = pofReader.readInt(7);
			this.hasBitrate = pofReader.readBoolean(8);
			this.persons = (List<String>) pofReader.readCollection(9, null);
			this.player = (Player) pofReader.readObject(10);
			this.copyright = pofReader.readString(11 );
		}

	}

	private void writeExternal(PofWriter pofWriter)
			throws IOException
	{

		pofWriter.writeString(0, this.uri);
		pofWriter.writeString(1, this.title);
		pofWriter.writeInt(2, this.width);
		pofWriter.writeInt(3, this.height);
		pofWriter.writeString(4, this.format);
		pofWriter.writeLong(5, this.duration);
		pofWriter.writeLong(6, this.size);
		pofWriter.writeInt(7, this.bitrate);
		pofWriter.writeBoolean(8, this.hasBitrate);
		pofWriter.writeCollection(9, this.persons);
		pofWriter.writeObject(10, this.player);
		pofWriter.writeString(11, this.copyright);

	}
}
