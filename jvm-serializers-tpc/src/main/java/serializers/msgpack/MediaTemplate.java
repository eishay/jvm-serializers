package serializers.msgpack;

import java.io.IOException;
import java.util.List;

import org.msgpack.packer.Packer;
import org.msgpack.template.*;
import org.msgpack.unpacker.Unpacker;

import data.media.Media;

final class MediaTemplate extends AbstractTemplate<Media> {
    static final Template<Media> INSTANCE = new MediaTemplate();
     static final Template<Media.Player> mediaPlayerTemplate =
             new NotNullableTemplate<Media.Player>(new MediaPlayerTemplate());
     @SuppressWarnings({ "rawtypes", "unchecked" })
     static final Template<List<String>> personsTemplate = 
 	new NotNullableTemplate<List<String>>(new ListTemplate(
 		new NotNullableTemplate<String>(StringTemplate.getInstance())));

     @Override
     public void write(Packer packer, Media v, boolean required) throws IOException {
 	packer.writeArrayBegin(12);
 	Templates.TString.write(packer, v.uri, true);
 	Templates.TString.write(packer, v.title, false);
 	Templates.TInteger.write(packer, v.width, true);
 	Templates.TInteger.write(packer, v.height, true);
 	Templates.TString.write(packer, v.format, true);
 	Templates.TLong.write(packer, v.duration, true);
 	Templates.TLong.write(packer, v.size, true);
 	Templates.TInteger.write(packer, v.bitrate, true);
 	Templates.TBoolean.write(packer, v.hasBitrate, true);
 	personsTemplate.write(packer, v.persons, true);
 	mediaPlayerTemplate.write(packer, v.player, true);
 	Templates.TString.write(packer, v.copyright, false);
 	packer.writeArrayEnd();
     }

     @Override
     public Media read(Unpacker unpacker, Media to, boolean required) throws IOException {
 	unpacker.readArrayBegin();
 	to = new Media(
 		Templates.TString.read(unpacker, null, true),
 		Templates.TString.read(unpacker, null, false),
 		Templates.TInteger.read(unpacker, null, true),
 		Templates.TInteger.read(unpacker, null, true),
 		Templates.TString.read(unpacker, null, true),
 		Templates.TLong.read(unpacker, null, true),
 		Templates.TLong.read(unpacker, null, true),
 		Templates.TInteger.read(unpacker, null, true),
 		Templates.TBoolean.read(unpacker, null, true),
 		personsTemplate.read(unpacker, null, true),
 		mediaPlayerTemplate.read(unpacker, null, true),
 		Templates.TString.read(unpacker, null, false)
 	);
 	unpacker.readArrayEnd();
 	return to;
     }
 }