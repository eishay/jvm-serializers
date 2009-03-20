package serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javolution.xml.XMLBinding;
import javolution.xml.XMLFormat;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;

import serializers.java.Image;
import serializers.java.Media;
import serializers.java.MediaContent;
import serializers.java.Image.Size;
import serializers.java.Media.Player;

public class JavolutionXMLFormatSerializer extends StdMediaSerializer
{
  private MediaContentBinding _binding = new MediaContentBinding();

    public JavolutionXMLFormatSerializer()
    {
        super("javolution xmlformat");
    }

  public MediaContent deserialize(byte[] array) throws Exception
  {
    XMLObjectReader reader = XMLObjectReader.newInstance(new ByteArrayInputStream(array)).setBinding(_binding);
    try {
      return reader.read("mc", MediaContent.class);
    } finally {
      reader.close();
    }
  }

  public byte[] serialize(MediaContent content) throws IOException,
      Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    XMLObjectWriter writer = XMLObjectWriter.newInstance(baos).setBinding(_binding);
    writer.write(content, "mc", MediaContent.class);
    writer.close();
    //System.out.println(new String(baos.toByteArray()));
    return baos.toByteArray();
  }

  //XML binding using reflection.
  static class MediaContentBinding extends XMLBinding
  {
      private static final long serialVersionUID = 1L;

      private XMLFormat<MediaContent> _mediaContentCnv = new MediaContentConverter();
      private XMLFormat<Media> _mediaCnv = new MediaConverter();
      private XMLFormat<Image> _imageCnv = new ImageConverter();

      public MediaContentBinding()
      {
        setAlias(MediaContent.class, "mc");
        setAlias(Image.class, "im");
        setAlias(Media.class, "me");
        setAlias(String.class, "str");
      }

      @SuppressWarnings("unchecked")
      public <T> XMLFormat<T> getFormat(Class<T> cls) {
        if (MediaContent.class.equals(cls))
          return (XMLFormat<T>) _mediaContentCnv;
        if (Media.class.equals(cls))
          return (XMLFormat<T>) _mediaCnv;
        if (Image.class.equals(cls))
          return (XMLFormat<T>) _imageCnv;
        return super.getFormat(cls);
      }
  }

  static class MediaContentConverter extends XMLFormat<MediaContent>
  {
    public MediaContentConverter()
    {
      super(MediaContent.class);
    }

    @Override
    public void write(MediaContent content, XMLFormat.OutputElement xml) throws XMLStreamException
    {
      xml.add(content.getMedia());
      xml.add(content.getImage(0));
      xml.add(content.getImage(1));
    }

    @Override
    public MediaContent newInstance(java.lang.Class<MediaContent> cls, XMLFormat.InputElement xml) throws XMLStreamException
    {
      Media media = (Media) xml.getNext();
      MediaContent content = new MediaContent(media);
      content.addImage((Image) xml.getNext());
      content.addImage((Image) xml.getNext());
      return content;
    }

    @Override
    public void read(javolution.xml.XMLFormat.InputElement arg0, MediaContent arg1) throws XMLStreamException
    {
      // do Nothing object loaded by newInstance
    }

  }

  static class ImageConverter extends XMLFormat<Image>
  {
    public ImageConverter()
    {
      super(Image.class);
    }

    @Override
    public void write(Image image, XMLFormat.OutputElement xml) throws XMLStreamException
    {
      xml.setAttribute("ul", image.getUri());
      xml.setAttribute("tl", image.getTitle());
      xml.setAttribute("wd", image.getWidth());
      xml.setAttribute("hg", image.getHeight());
      xml.setAttribute("sz", image.getSize().ordinal());
    }

    @Override
    public void read(XMLFormat.InputElement xml, Image image) throws XMLStreamException
    {
      image.setUri(xml.getAttribute("ul").toString());
      image.setTitle(xml.getAttribute("tl").toString());
      image.setWidth(xml.getAttribute("wd", 0));
      image.setHeight(xml.getAttribute("hg", 0));
      image.setSize(Size.values()[xml.getAttribute("sz", 0)]);
    }

  }

  static class MediaConverter extends XMLFormat<Media>
  {
    public MediaConverter()
    {
      super(Media.class);
    }

    @Override
    public void write(Media media, XMLFormat.OutputElement xml) throws XMLStreamException
    {
      xml.setAttribute("pl", media.getPlayer().ordinal());
      xml.setAttribute("ul", media.getUri());
      xml.setAttribute("tl", media.getTitle());
      xml.setAttribute("wd", media.getWidth());
      xml.setAttribute("hg", media.getHeight());
      xml.setAttribute("fr", media.getFormat());
      xml.setAttribute("dr", media.getDuration());
      xml.setAttribute("sz", media.getSize());
      xml.setAttribute("br", media.getBitrate());
      List<String> persons = media.getPersons();
      xml.setAttribute("ps", persons.size());
      for (String p : persons)
      {
        xml.add(p);
      }
    }

    @Override
    public void read(XMLFormat.InputElement xml, Media media) throws XMLStreamException
    {
      media.setPlayer(Player.values()[xml.getAttribute("pl", 0)]);
      media.setUri(xml.getAttribute("ul").toString());
      media.setTitle(xml.getAttribute("tl").toString());
      media.setWidth(xml.getAttribute("wd", 0));
      media.setHeight(xml.getAttribute("hg", 0));
      media.setFormat(xml.getAttribute("fr").toString());
      media.setDuration(xml.getAttribute("dr", 0l));
      media.setSize(xml.getAttribute("sz", 0l));
      media.setBitrate(xml.getAttribute("br", 0));
      int ps = xml.getAttribute("ps", 0);
      for (int i = 0; i<ps; i++) {
        media.addToPerson((String)xml.getNext());
      }
    }
  }
}
