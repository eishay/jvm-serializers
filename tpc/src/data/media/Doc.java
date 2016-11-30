package data.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Doc implements java.io.Serializable
{
    private static final long serialVersionUID = 2L;

	public String title;
	public int price;
	public String image;
	public int itemId;

	public Doc() {}

	public Doc(String title, int price, String image, int itemId) {
		this.title = title;
		this.price = price;
		this.image = image;
		this.itemId = itemId;

	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Doc doc = (Doc) o;

		if (price != doc.price)
			return false;
		if (itemId != doc.itemId)
			return false;
		if (!getTitle().equals(doc.getTitle()))
			return false;
		return image.equals(doc.image);

	}

	public int getPrice() {
		return price;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPrice(int price) {

		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public String toString() {

		return "Doc{" +
			"title='" + title + '\'' +
			", price=" + price +
			", image='" + image + '\'' +
			", itemId=" + itemId +
			'}';
	}

	@Override
	public int hashCode() {
		int result = getTitle().hashCode();
		result = 31 * result + price;
		result = 31 * result + image.hashCode();
		result = 31 * result + itemId;
		return result;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
}
