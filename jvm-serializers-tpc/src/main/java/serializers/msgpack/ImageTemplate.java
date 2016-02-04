package serializers.msgpack;

import java.io.IOException;

import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.NotNullableTemplate;
import org.msgpack.template.Template;
import org.msgpack.template.Templates;
import org.msgpack.unpacker.Unpacker;

import data.media.Image;
import data.media.Image.Size;

final class ImageTemplate extends AbstractTemplate<Image> {
     static final Template<Image> INSTANCE = new ImageTemplate();
     static final Template<Image.Size> imageSizeTemplate =
 	new NotNullableTemplate<Image.Size>(ImageSizeTemplate.INSTANCE);

     @Override
     public void write(Packer packer, Image v, boolean required) throws IOException {
 	packer.writeArrayBegin(5);
 	Templates.TString.write(packer, v.uri, true);
 	Templates.TString.write(packer, v.title, false);
 	Templates.TInteger.write(packer, v.width, true);
 	Templates.TInteger.write(packer, v.height, true);
 	imageSizeTemplate.write(packer, v.size);
 	packer.writeArrayEnd();
     }

     @Override
     public Image read(Unpacker unpacker, Image to, boolean required) throws IOException {
 	unpacker.readArrayBegin();
 	to = new Image(
 		Templates.TString.read(unpacker, null, true),
 		Templates.TString.read(unpacker, null, false),
 		Templates.TInteger.read(unpacker, null, true),
 		Templates.TInteger.read(unpacker, null, true),
 		imageSizeTemplate.read(unpacker, null, true)
 	);
 	unpacker.readArrayEnd();
 	return to;
     }
 }