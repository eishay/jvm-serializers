package data.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@SuppressWarnings("serial")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HermesResponse implements java.io.Serializable
{
	public int numFound;
	public List<Doc> docs;

	public HermesResponse() {}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		HermesResponse that = (HermesResponse) o;

		if (getNumFound() != that.getNumFound())
			return false;
		return getDocs().equals(that.getDocs());

	}

	@Override
	public int hashCode() {
		int result = getNumFound();
		result = 31 * result + getDocs().hashCode();
		return result;
	}

	public HermesResponse(int numFound, List<Doc> docs) {
		this.numFound = numFound;
		this.docs = docs;

	}

	public int getNumFound() {
		return numFound;
	}

	public void setNumFound(int numFound) {
		this.numFound = numFound;
	}

	public List<Doc> getDocs() {
		return docs;
	}

	public void setDocs(List<Doc> docs) {
		this.docs = docs;
	}
}
