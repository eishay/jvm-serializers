package serializers.xml;

import data.media.*;
import serializers.Transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JaxbMediaTransformer extends Transformer<MediaContent, JaxbMediaContent> {

	public JaxbMediaContent[] resultArray(int size) {
		return new JaxbMediaContent[size];
	}

	public JaxbMediaContent forward(MediaContent mc) {
		return copy(mc);
	}

	@Override
	public MediaContent[] sourceArray(int size) {
		return new MediaContent[0];
	}

	public MediaContent reverse(JaxbMediaContent mc) {
		ArrayList<Image> images = new ArrayList<Image>(mc.images.size());
		for (JaxbImage i : mc.images) {
			images.add(new Image(i.uri, i.title, i.width, i.height, i.size == null ? null : i.size.equals(JaxbImage.Size.LARGE) ? Image.Size.LARGE : Image.Size.SMALL));
		}
		return new MediaContent(convert2Media(mc.media), images);
	}

	private JaxbMediaContent copy(MediaContent mc) {
		List<JaxbImage> images = new ArrayList<JaxbImage>(mc.images.size());
		for (Image i : mc.images) {
			images.add(new JaxbImage(i.uri, i.title, i.width, i.height, i.size == null ? null : i.size.equals(Image.Size.LARGE) ? JaxbImage.Size.LARGE : JaxbImage.Size.SMALL));
		}
		return new JaxbMediaContent(convert2JaxbMedia(mc.media), images);
	}

	private Media convert2Media(JaxbMedia m) {
		return new Media(m.uri, m.title, m.width, m.height, m.format, m.duration, m.size, m.bitrate, m.hasBitrate, new ArrayList<String>(m.persons), Media.Player.JAVA, m.copyright);
	}

	private JaxbMedia convert2JaxbMedia(Media m) {
		return new JaxbMedia(m.uri, m.title, m.width, m.height, m.format, m.duration, m.size, m.bitrate, m.hasBitrate, new ArrayList<String>(m.persons), JaxbMedia.Player.JAVA, m.copyright);
	}

	public MediaContent shallowReverse(JaxbMediaContent mc) {
		return new MediaContent(convert2Media(mc.media), Collections.<Image>emptyList());
	}
}
