package serializers;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.ByteOutput;
import org.jboss.marshalling.ClassExternalizerFactory;
import org.jboss.marshalling.ClassTable;
import org.jboss.marshalling.Creator;
import org.jboss.marshalling.Externalizer;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;
import org.jboss.marshalling.reflect.SunReflectiveCreator;
import org.jboss.marshalling.river.RiverMarshallerFactory;
import org.jboss.marshalling.serial.SerialMarshallerFactory;

import data.media.Image;
import data.media.Media;
import data.media.MediaContent;

public class JBossMarshalling {

	public static void register(final TestGroups groups) {
		MarshallerFactory riverFactory = new RiverMarshallerFactory();

		groups.media.add(
			JavaBuiltIn.mediaTransformer,
			new MarshallingSerializer<MediaContent>(
				MediaContent.class,
				"jboss-marshalling-river",
				riverFactory,
				false,
				false
			),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS,
                        SerClass.ZERO_KNOWLEDGE,
                        "full graph zero knowledge"
                )
        );
		groups.media.add(
			JavaBuiltIn.mediaTransformer,
			new MarshallingSerializer<MediaContent>(
				MediaContent.class,
				"jboss-marshalling-river-manual",
				riverFactory,
				false,
				true
			),
            new SerFeatures(
                    SerFormat.BINARY,
                    SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS,
                    SerClass.CLASS_SPECIFIC_MANUAL_OPTIMIZATIONS,
                    "full graph with manual optimizations"
            )
        );
		groups.media.add(
			JavaBuiltIn.mediaTransformer,
			new MarshallingSerializer<MediaContent>(
				MediaContent.class,
				"jboss-marshalling-river-ct",
				riverFactory,
				true,
				false
			),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS,
                        SerClass.CLASSES_KNOWN,
                        "full graph with preregistered classes"
                )
		);
		groups.media.add(
			JavaBuiltIn.mediaTransformer,
			new MarshallingSerializer<MediaContent>(
				MediaContent.class,
				"jboss-marshalling-river-ct-manual",
				riverFactory,
				true,
				true
			),
            new SerFeatures(
                    SerFormat.BINARY,
                    SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS,
                    SerClass.CLASS_SPECIFIC_MANUAL_OPTIMIZATIONS,
                    "full graph preregistered classes, manual optimization"
            )
		);
		groups.media.add(
			JavaBuiltIn.mediaTransformer,
			new MarshallingSerializer<MediaContent>(
				MediaContent.class,
				"jboss-marshalling-serial",
				new SerialMarshallerFactory(),
				false,
				false
			),
                new SerFeatures(
                        SerFormat.BINARY,
                        SerGraph.FULL_GRAPH_WITH_SHARED_OBJECTS,
                        SerClass.ZERO_KNOWLEDGE,
                        ""
                )
		);
	}

	private static final class MarshallingSerializer<T> extends Serializer<T> {

	    private final Class<T> clz;

	    private final Marshaller marshaller;

	    private final Unmarshaller unmarshaller;

	    private final String name;

	    private final ByteArrayInput input = new ByteArrayInput();

	    private final ByteArrayOutput output = new ByteArrayOutput();

	    public MarshallingSerializer(
    		final Class<T> clz,
    		final String name,
    		final MarshallerFactory marshallerFactory,
    		final boolean useCustomClassTable,
    		final boolean useExternalizers
		) {
	    	this.clz = clz;
	    	this.name = name;

	    	MarshallingConfiguration cfg = new MarshallingConfiguration();
	    	cfg.setBufferSize(Serializer.BUFFER_SIZE);
	    	cfg.setExternalizerCreator(new SunReflectiveCreator());

	    	if (useCustomClassTable) {
	    		cfg.setClassTable(new CustomClassTable());
	    	}

	    	if (useExternalizers) {
	    		cfg.setClassExternalizerFactory(new CustomCEF());
	    	}

	    	try {
				marshaller = marshallerFactory.createMarshaller(cfg);
				unmarshaller = marshallerFactory.createUnmarshaller(cfg);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	    }

        @Override
		public String getName() {
        	return name;
        }

        @Override
		public T deserialize(final byte[] array) throws Exception {
        	input.setBuffer(array);
        	unmarshaller.start(input);
			T val = unmarshaller.readObject(clz);
        	unmarshaller.finish();
	        return val;
	    }

	    @Override
		public byte[] serialize(final T data) throws IOException {
	    	marshaller.start(output);
	    	marshaller.writeObject(data);
	    	marshaller.finish();
	        return output.toByteArray();
	    }

        @Override
        public final void serializeItems(final T[] items, final OutputStream os)
    		throws Exception {
        	marshaller.start(Marshalling.createByteOutput(os));
            for (Object item : items) {
            	marshaller.writeObject(item);
            }
            marshaller.finish();
        }

		@Override
        public T[] deserializeItems(final InputStream in, final int numOfItems)
    		throws Exception {
        	unmarshaller.start(Marshalling.createByteInput(in));

			@SuppressWarnings("unchecked")
			T[] result = (T[]) Array.newInstance(clz, numOfItems);
            for (int i = 0; i < numOfItems; ++i) {
                result[i] = unmarshaller.readObject(clz);
            }
            unmarshaller.finish();

            return result;
        }

		private static void writeMaybeString(
			final ObjectOutput output,
			final String s
		) throws IOException {
			if (s != null) {
				output.writeBoolean(true);
				output.writeUTF(s);
			} else {
				output.writeBoolean(false);
			}
		}

		private static String readMaybeString(final ObjectInput input)
			throws IOException {
			if (input.readBoolean()) {
				return input.readUTF();
			} else {
				return null;
			}
		}

		private static Image readImage(final ObjectInput input)
			throws IOException {
			final Image image = new Image();
			readImage(input, image);
			return image;
		}

		private static void readImage(final ObjectInput input, final Image img)
			throws IOException {
			img.setUri(input.readUTF());
			img.setTitle(readMaybeString(input));
			img.setWidth(input.readInt());
			img.setHeight(input.readInt());
			img.setSize(Image.Size.values()[input.readByte()]);
		}

		private static void writeImage(
			final ObjectOutput output,
			final Image image
		) throws IOException {
			output.writeUTF(image.uri);
			writeMaybeString(output, image.title);
			output.writeInt(image.width);
			output.writeInt(image.height);
			output.writeByte(image.size.ordinal());
		}

		private static Media readMedia(final ObjectInput input)
			throws IOException {
			final Media m = new Media();
			readMedia(input, m);
			return m;
		}

		private static void readMedia(final ObjectInput input, final Media m)
			throws IOException {
			m.setUri(input.readUTF());
			m.setTitle(readMaybeString(input));
			m.setWidth(input.readInt());
			m.setHeight(input.readInt());
			m.setFormat(input.readUTF());
			m.setDuration(input.readLong());
			m.setSize(input.readLong());
			if (input.readBoolean()) {
				m.setBitrate(input.readInt());
			}
			int numPersons = input.readInt();
			ArrayList<String> persons = new ArrayList<String>(numPersons);
			for (int i = 0; i < numPersons; i++) {
				persons.add(input.readUTF());
			}
			m.setPersons(persons);
			m.setPlayer(Media.Player.values()[input.readByte()]);
			m.setCopyright(readMaybeString(input));
		}

		private static void writeMedia(
			final ObjectOutput output,
			final Media m
		) throws IOException {
			output.writeUTF(m.uri);
			writeMaybeString(output, m.title);
			output.writeInt(m.width);
			output.writeInt(m.height);
			output.writeUTF(m.format);
			output.writeLong(m.duration);
			output.writeLong(m.size);
			output.writeBoolean(m.hasBitrate);
			if (m.hasBitrate) {
				output.writeInt(m.bitrate);
			}
			output.writeInt(m.persons.size());
			for (String p : m.persons) {
				output.writeUTF(p);
			}
			output.writeByte(m.player.ordinal());
			writeMaybeString(output, m.copyright);
		}

		private static void writeMediaContent(
			final ObjectOutput output,
			final MediaContent mc
		) throws IOException {
			writeMedia(output, mc.media);
			output.writeInt(mc.images.size());
			for (Image image : mc.images) {
				writeImage(output, image);
			}
		}

		private static void readMediaContent(
			final ObjectInput input,
			final MediaContent mc
		) throws IOException {
			mc.setMedia(readMedia(input));
			int numImages = input.readInt();
            ArrayList<Image> images = new ArrayList<Image>(numImages);
            for (int i = 0; i < numImages; i++) {
                images.add(readImage(input));
            }
            mc.setImages(images);
		}

		private static final class CustomCEF
			implements ClassExternalizerFactory {

			private static Class<?>[] CLASSES = {
				 Media.class,
				 MediaContent.class,
				 Image.class,
				 MediaExternalizer.class,
				 MediaContentExternalizer.class,
				 ImageExternalizer.class
			};

			private static final Externalizer[] EXTERNALIZERS = {
				new MediaExternalizer(),
				new MediaContentExternalizer(),
				new ImageExternalizer(),
				null,
				null,
				null
			};

			public CustomCEF() {
				ExternalizerExternalizer ext = new ExternalizerExternalizer();

				EXTERNALIZERS[3] = ext;
				EXTERNALIZERS[4] = ext;
				EXTERNALIZERS[5] = ext;
			}

			@Override
			public Externalizer getExternalizer(final Class<?> type) {
				for (int i = 0; i < CLASSES.length; i++) {
					if (CLASSES[i].equals(type)) {
						return EXTERNALIZERS[i];
					}
				}

				if (!ExternalizerExternalizer.class.equals(type)) {
					System.err.println("No externalizer for type " + type);
				}

				return null;
			}
		}

		private static final class MediaExternalizer implements Externalizer {

			private static final long serialVersionUID = 1L;

			@Override
			public void writeExternal(
				final Object subject,
				final ObjectOutput output
			) throws IOException {
				writeMedia(output, (Media)subject);
			}

			@Override
			public void readExternal(
				final Object subject,
				final ObjectInput input
			) throws IOException, ClassNotFoundException {
				readMedia(input, (Media)subject);
			}

			@Override
			public Object createExternal(
				final Class<?> subjectType,
				final ObjectInput input,
				final Creator defaultCreator
			) throws IOException, ClassNotFoundException {
				return new Media();
			}
		}

		private static final class MediaContentExternalizer
			implements Externalizer {

			private static final long serialVersionUID = 1L;

			@Override
			public void writeExternal(
				final Object subject,
				final ObjectOutput output
			) throws IOException {
				writeMediaContent(output, (MediaContent)subject);
			}

			@Override
			public void readExternal(
				final Object subject,
				final ObjectInput input
			) throws IOException, ClassNotFoundException {
				readMediaContent(input, (MediaContent)subject);
			}

			@Override
			public Object createExternal(
				final Class<?> subjectType,
				final ObjectInput input,
				final Creator defaultCreator
			) throws IOException, ClassNotFoundException {
				return new MediaContent();
			}
		}

		private static final class ExternalizerExternalizer
			implements Externalizer {

			private static final long serialVersionUID = 1L;

			private static final Externalizer[] EXTERNALIZERS = {
				new MediaExternalizer(),
				new MediaContentExternalizer(),
				new ImageExternalizer()
			};

			@Override
			public void writeExternal(
				final Object subject,
				final ObjectOutput output
			) throws IOException {
				// there is no state
			}

			@Override
			public void readExternal(
				final Object subject,
				final ObjectInput input
			) throws IOException, ClassNotFoundException {
				// there is no state
			}

			@Override
			public Object createExternal(
				final Class<?> subjectType,
				final ObjectInput input,
				final Creator defaultCreator
			) throws IOException, ClassNotFoundException {
				for (Externalizer ext : EXTERNALIZERS) {
					if (ext.getClass().equals(subjectType)) {
						return ext;
					}
				}

				throw new ClassNotFoundException("Unknown type " + subjectType);
			}
		}

		private static final class ImageExternalizer
			implements Externalizer {

			private static final long serialVersionUID = 1L;

			@Override
			public void writeExternal(
				final Object subject,
				final ObjectOutput output
			) throws IOException {
				writeImage(output, (Image)subject);
			}

			@Override
			public void readExternal(
				final Object subject,
				final ObjectInput input
			) throws IOException, ClassNotFoundException {
				readImage(input, (Image)subject);
			}

			@Override
			public Object createExternal(
				final Class<?> subjectType,
				final ObjectInput input,
				final Creator defaultCreator
			) throws IOException, ClassNotFoundException {
				return new Image();
			}
		}

		private static final class CustomClassTable implements ClassTable {

			private static final Class<?>[] CLASSES = {
				 MediaContent.class,
				 Media.Player.class,
				 Media.class,
				 Image.Size.class,
				 Image.class,
				 CustomCEF.class,
				 MediaExternalizer.class,
				 MediaContentExternalizer.class,
				 ImageExternalizer.class,
				 ExternalizerExternalizer.class,
				 ArrayList.class
			};

			private static final Writer WRITERS[] = new Writer[CLASSES.length];

			public CustomClassTable() {
				for (int i = 0; i < WRITERS.length; i++) {
					final byte b = (byte)i;
					WRITERS[i] = new Writer() {
						@Override
						public void writeClass(
							final Marshaller marshaller,
							final Class<?> clazz
						) throws IOException {
							marshaller.writeByte(b);
						}
					};
				}
			}

			@Override
			public Writer getClassWriter(final Class<?> c) throws IOException {
				for (int i = 0; i < CLASSES.length; i++) {
					if (CLASSES[i].equals(c)) {
						return WRITERS[i];
					}
				}

				throw new IOException("Unexpected class " + c);
			}

			@Override
			public Class<?> readClass(final Unmarshaller unmarshaller)
				throws IOException, ClassNotFoundException {
				byte b = unmarshaller.readByte();

				if (b < 0 || b >= CLASSES.length) {
					throw new ClassNotFoundException(
						"Unexcepted class number " + b
					);
				}

				return CLASSES[b];
			}
		}

        private static final class ByteArrayInput implements ByteInput {

        	private byte[] buffer;

        	private int position;

        	public void setBuffer(final byte[] buffer) {
        		this.buffer = buffer;
        		position = 0;
        	}

			@Override
			public void close() throws IOException {
				buffer = null;
				position = -1;
			}

			@Override
			public int read() throws IOException {
				if (position >= buffer.length) {
					return -1;
				}

				return buffer[position++];
			}

			@Override
			public int read(final byte[] b) throws IOException {
				return read(b, 0, b.length);
			}

			@Override
			public int read(final byte[] b, final int off, final int len)
				throws IOException {
				if (position >= buffer.length) {
					return -1;
				}

				int n = len;
				if (n > buffer.length - position) {
					n = buffer.length - position;
				}

				System.arraycopy(buffer, position, b, off, n);
				position += n;

				return n;
			}

			@Override
			public int available() throws IOException {
				return buffer.length - position;
			}

			@Override
			public long skip(final long n) throws IOException {
				throw new IOException("Unsupported operation");
			}
        }

        private static final class ByteArrayOutput implements ByteOutput {

        	private byte[] buffer = new byte[Serializer.BUFFER_SIZE];

        	private int position;

			@Override
			public void close() throws IOException {
				position = 0;
				buffer = null;
			}

			@Override
			public void flush() throws IOException {
			}

			@Override
			public void write(final int b) throws IOException {
				throw new IOException("Unsupported operation");
			}

			@Override
			public void write(final byte[] b) throws IOException {
				write(b, 0, b.length);
			}

			@Override
			public void write(final byte[] b, final int off, final int len)
				throws IOException {
				if (buffer.length - position < len) {
					byte[] newBuffer = new byte[2 * buffer.length];
					System.arraycopy(buffer, 0, newBuffer, 0, position);
					buffer = newBuffer;
				}

				System.arraycopy(b, off, buffer, position, len);
				position += len;
			}

			public byte[] toByteArray() {
				byte[] result = new byte[position];
				System.arraycopy(buffer, 0, result, 0, position);
				position = 0;
				return result;
			}
        }
	}
}
