package tommiek.sitegenerator.model;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "category")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Category extends AbstractEntity {

	private String myType;
	private String myLink;
	private Image myLeadImage;
	private List<String> myProductIds;
	private List<Product> myProducts;

	public String getType() {
		return myType;
	}

	public void setType(final String type) {
		myType = type;
	}

	public String getLink() {
		return myLink;
	}

	public void setLink(final String link) {
		myLink = link;
	}

	public Image getLeadImage() {
		return myLeadImage;
	}

	public void setLeadImage(final Image leadImage) {
		myLeadImage = leadImage;
	}

	@XmlElementWrapper(name = "products")
	@XmlElement(name = "product")
	public List<String> getProductIds() {
		return myProductIds;
	}

	public void setProductIds(final List<String> productIds) {
		myProductIds = productIds;
	}

	@XmlTransient
	public List<Product> getProducts() {
		return myProducts;
	}

	public void setProducts(final List<Product> products) {
		myProducts = products;
	}

	public void addProduct(final Product product) {
		if (myProducts == null)
			myProducts = new LinkedList<>();
		myProducts.add(product);
	}
}
