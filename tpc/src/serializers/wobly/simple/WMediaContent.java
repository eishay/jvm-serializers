package serializers.wobly.simple;

import java.util.List;

import com.wowd.wobly.UnmodifiableWoblyImpl;
import com.wowd.wobly.WoblyUtils.Format;
import com.wowd.wobly.annotations.WoblyField;
import com.wowd.wobly.annotations.WoblyTypeOptions;

@WoblyTypeOptions(unmodifiable = true, specialFormat = Format.NO_SIZE_FIELD)
public class WMediaContent extends UnmodifiableWoblyImpl
{
	@WoblyField(id = -1, required = true)
	List<WImage> images;

	@WoblyField(id = -2, required = true)
	WMedia media;

	
	public WMediaContent(List<WImage> images, WMedia media)
	{
		this.images = images;
		this.media = media;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((images == null) ? 0 : images.hashCode());
		result = prime * result + ((media == null) ? 0 : media.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WMediaContent other = (WMediaContent) obj;
		if (images == null) {
			if (other.images != null)
				return false;
		} else if (!images.equals(other.images))
			return false;
		if (media == null) {
			if (other.media != null)
				return false;
		} else if (!media.equals(other.media))
			return false;
		return true;
	}
	
	//-------------- WOBLY AUTO GENERATED CODE FOR SERIALIZATION ----------
	//---------------------------------------------------------------------
	
	public static final com.wowd.wobly.WoblyReader<WMediaContent> objectReader = new com.wowd.wobly.WoblyReaderImpl<WMediaContent>() {
		@Override
		public WMediaContent readObject(java.nio.ByteBuffer buf)
		{
			return read(buf);
		}};
	@Override
	public void write(final java.nio.ByteBuffer buf) {
		try {
			{
				this.media.write(buf);
			}
			{
				int startFieldMark = buf.position();
				buf.position(buf.position()+4);
				com.wowd.wobly.WoblyUtils.Buffers.putVInt(buf, this.images.size());
				for (WImage v1 : this.images) {
					v1.write(buf);
				}
				buf.putInt(startFieldMark, buf.position() - startFieldMark - 4);
			}
		} catch (com.wowd.wobly.exceptions.WoblyWriteException e) {
			throw e;
		} catch (java.lang.Throwable t) {
			throw new com.wowd.wobly.exceptions.WoblyWriteException(t);
		}
	}
	private WMediaContent(final java.nio.ByteBuffer buf) {
		
		{
			this.media = WMedia.read(buf);
		}
		
		{
			buf.getInt(); //read size
			int size1 = com.wowd.wobly.WoblyUtils.Buffers.getVInt(buf);
			this.images = new java.util.ArrayList<WImage>(size1);
			for (int i1 = 0; i1 < size1; i1++) {
				WImage tmp1;
				tmp1 = WImage.read(buf);
				this.images.add(tmp1);
			}
		}
	}
	@com.wowd.wobly.annotations.ReadStatic
	public static WMediaContent read(java.nio.ByteBuffer buf) {
		try {
			WMediaContent object = new WMediaContent(buf);
			return object;
		} catch (com.wowd.wobly.exceptions.WoblyReadException e) {
			throw e;
		} catch (java.lang.Throwable t) {
			throw new com.wowd.wobly.exceptions.WoblyReadException(t);
		}
	}
	public static WMediaContent read(byte[] buf) {
		return read(java.nio.ByteBuffer.wrap(buf));
	}
	@Override
	public int getSize() {
		int size = 0;
		{
			size += this.media.getSize();
		}
		{
			size += 4;
			size += com.wowd.wobly.WoblyUtils.Buffers.sizeVInt(this.images.size());
			for (WImage v1 : this.images) {
				size += v1.getSize();
			}
		}
		return size;
	}
	
	//---------------------------------------------------------------------
	//-------------- END OF AUTO GENERATED CODE FOR SERIALIZATION ---------
}
