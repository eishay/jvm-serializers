package serializers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dyuproject.protostuff.IOUtil;
//import com.dyuproject.protostuff.JsonIOUtil;

import serializers.protostuff.media.MediaContent;
import serializers.protostuff.media.Media;
import serializers.protostuff.media.Image;

public final class Protostuff
{
    
    public static void register(TestGroups groups)
    {
            groups.media.add(MediaTransformer, MediaSerializer);
            //groups.media.add(MediaTransformer, JsonMediaSerializer);
    }
    
    public static final Serializer<MediaContent> MediaSerializer = 
        new Serializer<MediaContent>()
    {

        public MediaContent deserialize(byte[] array) throws Exception
        {
            MediaContent mc = new MediaContent();
            IOUtil.mergeFrom(array, mc);
            return mc;
        }

        public byte[] serialize(MediaContent content) throws Exception
        {
            return IOUtil.toByteArray(content);
        }
        
        public String getName()
        {
            return "protobuf/protostuff-core";
        }
        
    };
    
    /*public static final Serializer<MediaContent> JsonMediaSerializer = 
        new Serializer<MediaContent>()
    {

        public MediaContent deserialize(byte[] array) throws Exception
        {
            MediaContent mc = new MediaContent();
            JsonIOUtil.mergeFrom(array, mc, true);
            return mc;
        }

        public byte[] serialize(MediaContent content) throws Exception
        {
            return JsonIOUtil.toByteArray(content, true);
        }
        
        public String getName()
        {
            return "json/protostuff-json";
        }
        
    };*/
    
    public static final Transformer<data.media.MediaContent,MediaContent> MediaTransformer = new Transformer<data.media.MediaContent,MediaContent>()
    {
            // ----------------------------------------------------------
            // Forward

            public MediaContent forward(data.media.MediaContent mc)
            {
                    MediaContent cb = new MediaContent(forwardMedia(mc.media));
                    ArrayList<Image> ims = new ArrayList<Image>(mc.images.size());
                    for (data.media.Image image : mc.images) {
                            ims.add(forwardImage(image));
                    }
                    
                    return cb.setImageList(ims);
            }

            private Media forwardMedia(data.media.Media media)
            {
                    // Media
                    return new Media(
                            media.uri,
                            media.width,
                            media.height,
                            media.format,
                            media.duration,
                            media.size,
                            forwardPlayer(media.player)
                            )
                        .setTitle(media.title)
                        .setBitrate(media.hasBitrate ? media.bitrate : null)
                        .setPersonList(new ArrayList<String>(media.persons))
                        .setCopyright(media.copyright);
            }

            public Media.Player forwardPlayer(data.media.Media.Player p)
            {
                    switch (p) {
                            case JAVA: return Media.Player.JAVA;
                            case FLASH: return Media.Player.FLASH;
                            default:
                                    throw new AssertionError("invalid case: " + p);
                    }
            }

            private Image forwardImage(data.media.Image image)
            {
                    return new Image(
                            image.uri, 
                            image.width,
                            image.height,
                            forwardSize(image.size)
                            )
                        .setTitle(image.title);
            }

            public Image.Size forwardSize(data.media.Image.Size s)
            {
                    switch (s) {
                            case SMALL: return Image.Size.SMALL;
                            case LARGE: return Image.Size.LARGE;
                            default:
                                    throw new AssertionError("invalid case: " + s);
                    }
            }

            // ----------------------------------------------------------
            // Reverse

            public data.media.MediaContent reverse(MediaContent mc)
            {
                    List<Image> ims = mc.getImageList();
                    List<data.media.Image> images = new ArrayList<data.media.Image>(ims.size());

                    for (Image image : ims) {
                            images.add(reverseImage(image));
                    }

                    return new data.media.MediaContent(reverseMedia(mc.getMedia()), images);
            }

            private data.media.Media reverseMedia(Media media)
            {
                
                Integer bitRate = media.getBitrate();
                    // Media
                    return new data.media.Media(
                            media.getUri(),
                            media.getTitle(),
                            media.getWidth(),
                            media.getHeight(),
                            media.getFormat(),
                            media.getDuration(),
                            media.getSize(),
                            bitRate == null ? 0 : bitRate,
                            bitRate != null,
                            new ArrayList<String>(media.getPersonList()),
                            reversePlayer(media.getPlayer()),
                            media.getCopyright()
                    );
            }

            public data.media.Media.Player reversePlayer(Media.Player p)
            {
                    switch (p) {
                            case JAVA:  return data.media.Media.Player.JAVA;
                            case FLASH: return data.media.Media.Player.FLASH;
                            default:
                                    throw new AssertionError("invalid case: " + p);
                    }
            }

            private data.media.Image reverseImage(Image image)
            {
                    return new data.media.Image(
                            image.getUri(),
                            image.getTitle(),
                            image.getWidth(),
                            image.getHeight(),
                            reverseSize(image.getSize()));
            }

            public data.media.Image.Size reverseSize(Image.Size s)
            {
                    switch (s) {
                            case SMALL: return data.media.Image.Size.SMALL;
                            case LARGE: return data.media.Image.Size.LARGE;
                            default:
                                    throw new AssertionError("invalid case: " + s);
                    }
            }

            public data.media.MediaContent shallowReverse(MediaContent mc)
            {
                    return new data.media.MediaContent(reverseMedia(mc.getMedia()), Collections.<data.media.Image>emptyList());
            }
    };

}
