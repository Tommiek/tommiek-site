package tommiek.sitegenerator.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "product")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Product extends AbstractEntity {

    private Category myCategory;
    private String myType;
	private String myDetails;
    private String myLeadText;
	private Image myLeadImage;
	private List<String> myVariants;
	private List<Image> myImages;

    @XmlTransient
	public Category getCategory(){
	    return myCategory;
	}

	public void setCategory(final Category category){
	    myCategory = category;
	}

	public String getType() {
        return myType;
    }

    public void setType(final String type) {
        myType = type;
    }

    public String getDetails() {
        return myDetails;
    }

    public void setDetails(final String details) {
        myDetails = details;
    }

	public String getLeadText() {
		return myLeadText;
	}

	public void setLeadText(final String leadText) {
		myLeadText = leadText;
	}

	public Image getLeadImage() {
		return myLeadImage;
	}

	public void setLeadImage(final Image leadImage) {
		myLeadImage = leadImage;
	}

	@XmlElementWrapper(name = "variants")
	@XmlElement(name = "variant")
	public List<String> getVariants() {
		return myVariants;
	}

	public void setVariants(final List<String> variants) {
		myVariants = variants;
	}

	@XmlElementWrapper(name = "images")
	@XmlElement(name = "image")
	public List<Image> getImages() {
		return myImages;
	}

	public void setImages(final List<Image> images) {
		myImages = images;
	}

	@XmlTransient
	public Image getFirstImage() {
		return myImages.get(0);
	}
}
