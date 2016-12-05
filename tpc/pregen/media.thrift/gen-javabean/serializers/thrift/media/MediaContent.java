/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package serializers.thrift.media;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2016-12-04")
public class MediaContent implements org.apache.thrift.TBase<MediaContent, MediaContent._Fields>, java.io.Serializable, Cloneable, Comparable<MediaContent> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("MediaContent");

  private static final org.apache.thrift.protocol.TField IMAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("image", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField MEDIA_FIELD_DESC = new org.apache.thrift.protocol.TField("media", org.apache.thrift.protocol.TType.STRUCT, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new MediaContentStandardSchemeFactory());
    schemes.put(TupleScheme.class, new MediaContentTupleSchemeFactory());
  }

  public List<Image> image; // required
  public Media media; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    IMAGE((short)1, "image"),
    MEDIA((short)2, "media");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // IMAGE
          return IMAGE;
        case 2: // MEDIA
          return MEDIA;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.IMAGE, new org.apache.thrift.meta_data.FieldMetaData("image", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Image.class))));
    tmpMap.put(_Fields.MEDIA, new org.apache.thrift.meta_data.FieldMetaData("media", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, Media.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(MediaContent.class, metaDataMap);
  }

  public MediaContent() {
  }

  public MediaContent(
    List<Image> image,
    Media media)
  {
    this();
    this.image = image;
    this.media = media;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public MediaContent(MediaContent other) {
    if (other.isSetImage()) {
      List<Image> __this__image = new ArrayList<Image>(other.image.size());
      for (Image other_element : other.image) {
        __this__image.add(new Image(other_element));
      }
      this.image = __this__image;
    }
    if (other.isSetMedia()) {
      this.media = new Media(other.media);
    }
  }

  public MediaContent deepCopy() {
    return new MediaContent(this);
  }

  @Override
  public void clear() {
    this.image = null;
    this.media = null;
  }

  public int getImageSize() {
    return (this.image == null) ? 0 : this.image.size();
  }

  public java.util.Iterator<Image> getImageIterator() {
    return (this.image == null) ? null : this.image.iterator();
  }

  public void addToImage(Image elem) {
    if (this.image == null) {
      this.image = new ArrayList<Image>();
    }
    this.image.add(elem);
  }

  public List<Image> getImage() {
    return this.image;
  }

  public MediaContent setImage(List<Image> image) {
    this.image = image;
    return this;
  }

  public void unsetImage() {
    this.image = null;
  }

  /** Returns true if field image is set (has been assigned a value) and false otherwise */
  public boolean isSetImage() {
    return this.image != null;
  }

  public void setImageIsSet(boolean value) {
    if (!value) {
      this.image = null;
    }
  }

  public Media getMedia() {
    return this.media;
  }

  public MediaContent setMedia(Media media) {
    this.media = media;
    return this;
  }

  public void unsetMedia() {
    this.media = null;
  }

  /** Returns true if field media is set (has been assigned a value) and false otherwise */
  public boolean isSetMedia() {
    return this.media != null;
  }

  public void setMediaIsSet(boolean value) {
    if (!value) {
      this.media = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case IMAGE:
      if (value == null) {
        unsetImage();
      } else {
        setImage((List<Image>)value);
      }
      break;

    case MEDIA:
      if (value == null) {
        unsetMedia();
      } else {
        setMedia((Media)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case IMAGE:
      return getImage();

    case MEDIA:
      return getMedia();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case IMAGE:
      return isSetImage();
    case MEDIA:
      return isSetMedia();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof MediaContent)
      return this.equals((MediaContent)that);
    return false;
  }

  public boolean equals(MediaContent that) {
    if (that == null)
      return false;

    boolean this_present_image = true && this.isSetImage();
    boolean that_present_image = true && that.isSetImage();
    if (this_present_image || that_present_image) {
      if (!(this_present_image && that_present_image))
        return false;
      if (!this.image.equals(that.image))
        return false;
    }

    boolean this_present_media = true && this.isSetMedia();
    boolean that_present_media = true && that.isSetMedia();
    if (this_present_media || that_present_media) {
      if (!(this_present_media && that_present_media))
        return false;
      if (!this.media.equals(that.media))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_image = true && (isSetImage());
    list.add(present_image);
    if (present_image)
      list.add(image);

    boolean present_media = true && (isSetMedia());
    list.add(present_media);
    if (present_media)
      list.add(media);

    return list.hashCode();
  }

  @Override
  public int compareTo(MediaContent other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetImage()).compareTo(other.isSetImage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetImage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.image, other.image);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMedia()).compareTo(other.isSetMedia());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMedia()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.media, other.media);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("MediaContent(");
    boolean first = true;

    sb.append("image:");
    if (this.image == null) {
      sb.append("null");
    } else {
      sb.append(this.image);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("media:");
    if (this.media == null) {
      sb.append("null");
    } else {
      sb.append(this.media);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (image == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'image' was not present! Struct: " + toString());
    }
    if (media == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'media' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (media != null) {
      media.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class MediaContentStandardSchemeFactory implements SchemeFactory {
    public MediaContentStandardScheme getScheme() {
      return new MediaContentStandardScheme();
    }
  }

  private static class MediaContentStandardScheme extends StandardScheme<MediaContent> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, MediaContent struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // IMAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list8 = iprot.readListBegin();
                struct.image = new ArrayList<Image>(_list8.size);
                Image _elem9;
                for (int _i10 = 0; _i10 < _list8.size; ++_i10)
                {
                  _elem9 = new Image();
                  _elem9.read(iprot);
                  struct.image.add(_elem9);
                }
                iprot.readListEnd();
              }
              struct.setImageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // MEDIA
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.media = new Media();
              struct.media.read(iprot);
              struct.setMediaIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, MediaContent struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.image != null) {
        oprot.writeFieldBegin(IMAGE_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.image.size()));
          for (Image _iter11 : struct.image)
          {
            _iter11.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.media != null) {
        oprot.writeFieldBegin(MEDIA_FIELD_DESC);
        struct.media.write(oprot);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class MediaContentTupleSchemeFactory implements SchemeFactory {
    public MediaContentTupleScheme getScheme() {
      return new MediaContentTupleScheme();
    }
  }

  private static class MediaContentTupleScheme extends TupleScheme<MediaContent> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, MediaContent struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      {
        oprot.writeI32(struct.image.size());
        for (Image _iter12 : struct.image)
        {
          _iter12.write(oprot);
        }
      }
      struct.media.write(oprot);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, MediaContent struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      {
        org.apache.thrift.protocol.TList _list13 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.image = new ArrayList<Image>(_list13.size);
        Image _elem14;
        for (int _i15 = 0; _i15 < _list13.size; ++_i15)
        {
          _elem14 = new Image();
          _elem14.read(iprot);
          struct.image.add(_elem14);
        }
      }
      struct.setImageIsSet(true);
      struct.media = new Media();
      struct.media.read(iprot);
      struct.setMediaIsSet(true);
    }
  }

}

