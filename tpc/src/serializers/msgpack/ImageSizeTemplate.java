package serializers.msgpack;

import org.msgpack.template.OrdinalEnumTemplate;
import org.msgpack.template.Template;

import data.media.Image;
import data.media.Image.Size;

final class ImageSizeTemplate extends OrdinalEnumTemplate<Image.Size> {
     static final Template<Image.Size> INSTANCE = new ImageSizeTemplate(Image.Size.class);

     public ImageSizeTemplate(Class<Image.Size> targetClass) {
 	super(targetClass);
     }
 }