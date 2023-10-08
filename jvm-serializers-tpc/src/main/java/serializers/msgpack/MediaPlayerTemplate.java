package serializers.msgpack;

import org.msgpack.template.OrdinalEnumTemplate;
//import org.msgpack.template.Template;

import data.media.Media;

final class MediaPlayerTemplate extends OrdinalEnumTemplate<Media.Player> {
     public MediaPlayerTemplate() {
         super(Media.Player.class);
     }
 }