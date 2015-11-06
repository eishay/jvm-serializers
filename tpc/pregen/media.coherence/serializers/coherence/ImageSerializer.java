package serializers.coherence;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import data.media.Image;

import java.io.IOException;

/**
 * Created by a593223 on 6/29/2015.
 */
public class ImageSerializer implements PofSerializer{


    @Override
    public void serialize(PofWriter pofWriter, Object o) throws IOException {
        Image image = (Image) o;
        pofWriter.writeString(0, image.uri);
        pofWriter.writeString(1, image.title);
        pofWriter.writeInt(2, image.width);
        pofWriter.writeInt(3, image.height);
        pofWriter.writeObject(4, image.size);
        pofWriter.writeRemainder(null);
    }

    @Override
    public Object deserialize(PofReader pofReader) throws IOException {

        Image image = new Image();
        image.uri = pofReader.readString(0);
        image.title = pofReader.readString(1);
        image.width = pofReader.readInt(2);
        image.height = pofReader.readInt(3);
        image.size = (Image.Size) pofReader.readObject(4);

        return image;
    }
}
