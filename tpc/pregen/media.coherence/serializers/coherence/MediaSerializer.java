package serializers.coherence;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import data.media.Media;

import java.io.IOException;
import java.util.List;

/**
 * Created by a593223 on 6/29/2015.
 */

public class MediaSerializer  implements PofSerializer {



    @Override
    public void serialize(PofWriter pofWriter, Object o) throws IOException {
        Media media = (Media)o;
        pofWriter.writeString(0, media.uri);
        pofWriter.writeString(1, media.title);
        pofWriter.writeInt(2, media.width);
        pofWriter.writeInt(3, media.height);
        pofWriter.writeString(4, media.format);
        pofWriter.writeLong(5, media.duration);
        pofWriter.writeLong(6, media.size);
        pofWriter.writeInt(7, media.bitrate);
        pofWriter.writeBoolean(8, media.hasBitrate);
        pofWriter.writeCollection(9, media.persons);
        pofWriter.writeObject(10, media.player);
        pofWriter.writeString(11, media.copyright);
        pofWriter.writeRemainder(null);
    }

    @Override
    public Object deserialize(PofReader pofReader) throws IOException {
        Media media = new Media();
        media.uri = pofReader.readString(0);
        media.title = pofReader.readString(1);
        media.width = pofReader.readInt(2);
        ;
        media.height = pofReader.readInt(3);
        media.format = pofReader.readString(4);
        media.duration = pofReader.readLong(5);
        media.size = pofReader.readLong(6);
        media.bitrate = pofReader.readInt(7);
        media.hasBitrate = pofReader.readBoolean(8);
        media.persons = (List<String>) pofReader.readCollection(9, null);
        media.player = (Media.Player) pofReader.readObject(10);
        media.copyright = pofReader.readString(11 );
        return media;
    }
}
