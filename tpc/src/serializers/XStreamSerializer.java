package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

import serializers.java.Image;
import serializers.java.Media;
import serializers.java.MediaContent;
import serializers.java.Media.Player;
import serializers.java.Image.Size;

public class XStreamSerializer extends StdMediaSerializer
{
  private XStream xstream = null;
  private String name = "xstream";

    public XStreamSerializer(String name, boolean withSpecialConverter, boolean withStax) throws Exception
    {
        super(name);
        if (withStax) {
            xstream = new XStream(new StaxDriver());
        } else {
            xstream = new XStream(new XppDriver());
        }
        if (withSpecialConverter) {
            registerConverters();
        }
    }

  public MediaContent deserialize(byte[] array) throws Exception
  {
    return (MediaContent) xstream.fromXML(new ByteArrayInputStream(array));
  }

  public byte[] serialize(MediaContent content) throws IOException,
      Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    xstream.toXML(content, baos);
    baos.close();
    return baos.toByteArray();
  }

  public void registerConverters() throws Exception
  {
    xstream.alias("im", Image.class);
    xstream.registerConverter(new ImageConverter());

    xstream.alias("md", Image.class);
    xstream.registerConverter(new MediaConverter());

    xstream.alias("mc", MediaContent.class);
    xstream.registerConverter(new MediaContentConverter());
  }

  static class MediaContentConverter implements Converter
  {

    @Override
    public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
    {
      MediaContent content = (MediaContent) obj;
      writer.startNode("md");
      context.convertAnother(content.getMedia());
      writer.endNode();
      writer.startNode("im");
      context.convertAnother(content.getImage(0));
      writer.endNode();
      writer.startNode("im");
      context.convertAnother(content.getImage(1));
      writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
    {
      reader.moveDown();
      Media media = (Media) context.convertAnother(null, Media.class);
      reader.moveUp();
      MediaContent content = new MediaContent(media);
      reader.moveDown();
      content.addImage((Image) context.convertAnother(content, Image.class));
      reader.moveUp();
      reader.moveDown();
      content.addImage((Image) context.convertAnother(content, Image.class));
      reader.moveUp();
      return content;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean canConvert(Class arg0)
    {
      return MediaContent.class.equals(arg0);
    }

  }

  static class ImageConverter implements Converter
  {

    @Override
    public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
    {
      Image image = (Image) obj;
      writer.addAttribute("ul", image.getUri());
      writer.addAttribute("tl", image.getTitle());
      writer.addAttribute("wd", String.valueOf(image.getWidth()));
      writer.addAttribute("hg", String.valueOf(image.getHeight()));
      writer.addAttribute("sz", String.valueOf(image.getSize().ordinal()));
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
    {
      Image image = new Image();
      image.setUri(reader.getAttribute("ul"));
      image.setTitle(reader.getAttribute("tl"));
      image.setWidth(Integer.valueOf(reader.getAttribute("wd")));
      image.setHeight(Integer.valueOf(reader.getAttribute("hg")));
      image.setSize(Size.values()[Integer.valueOf(reader.getAttribute("sz"))]);
      return image;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean canConvert(Class arg0)
    {
      return Image.class.equals(arg0);
    }

  }

  static class MediaConverter implements Converter
  {

    @Override
    public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
    {
      Media media = (Media) obj;
      writer.addAttribute("pl", String.valueOf(media.getPlayer().ordinal()));
      writer.addAttribute("ul", media.getUri());
      writer.addAttribute("tl", media.getTitle());
      writer.addAttribute("wd", String.valueOf(media.getWidth()));
      writer.addAttribute("hg", String.valueOf(media.getHeight()));
      writer.addAttribute("fr", media.getFormat());
      writer.addAttribute("dr", String.valueOf(media.getDuration()));
      writer.addAttribute("sz", String.valueOf(media.getSize()));
      writer.addAttribute("br", String.valueOf(media.getBitrate()));
      for (String p : media.getPersons())
      {
        writer.startNode("pr");
        writer.setValue(p);
        writer.endNode();
      }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
    {
      Media media = new Media();
      media.setPlayer(Player.values()[Integer.valueOf(reader.getAttribute("pl"))]);
      media.setUri(reader.getAttribute("ul"));
      media.setTitle(reader.getAttribute("tl"));
      media.setWidth(Integer.valueOf(reader.getAttribute("wd")));
      media.setHeight(Integer.valueOf(reader.getAttribute("hg")));
      media.setFormat(reader.getAttribute("fr"));
      media.setDuration(Long.valueOf(reader.getAttribute("dr")));
      media.setSize(Long.valueOf(reader.getAttribute("sz")));
      media.setBitrate(Integer.valueOf(reader.getAttribute("br")));
      while (reader.hasMoreChildren())
      {
        reader.moveDown();
        media.addToPerson(reader.getValue());
        reader.moveUp();
      }
      return media;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean canConvert(Class arg0)
    {
      return Media.class.equals(arg0);
    }

  }
}
