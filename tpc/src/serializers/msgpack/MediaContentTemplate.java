package serializers.msgpack;

import java.io.IOException;
import java.util.List;

import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.ListTemplate;
import org.msgpack.template.NotNullableTemplate;
import org.msgpack.template.Template;
import org.msgpack.unpacker.Unpacker;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

final class MediaContentTemplate extends AbstractTemplate<MediaContent> {
     static final Template<MediaContent> INSTANCE = new MediaContentTemplate();
     static final Template<Media> mediaTemplate = new NotNullableTemplate<Media>(MediaTemplate.INSTANCE);
     @SuppressWarnings({ "rawtypes", "unchecked" })
     static final Template<List<Image>> imageListTemplate = 
 	new NotNullableTemplate<List<Image>>(new ListTemplate(
 		new NotNullableTemplate<Image>(ImageTemplate.INSTANCE)));

     @Override
     public void write(Packer packer, MediaContent v, boolean required) throws IOException {
 	packer.writeArrayBegin(2);
 	mediaTemplate.write(packer, v.media, true);
 	imageListTemplate.write(packer, v.images, true);
 	packer.writeArrayEnd();
     }

     @Override
     public MediaContent read(Unpacker unpacker, MediaContent to, boolean required) throws IOException {
 	unpacker.readArrayBegin();
 	to = new MediaContent(
 		mediaTemplate.read(unpacker, null, true),
 		imageListTemplate.read(unpacker, null, true)
 	);
 	unpacker.readArrayEnd();
 	return to;
     }
 }