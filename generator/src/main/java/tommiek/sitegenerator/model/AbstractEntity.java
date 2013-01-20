package tommiek.sitegenerator.model;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.PROPERTY)
public abstract class AbstractEntity {

	protected File sourceFile;

	private String myId;
	private String myName;
	private String myDescription;

	private String myHtmlTitle;
	private String myMetaDescription;
	private String myMetaKeywords;

	@XmlTransient
	public final File getSourceFile() {
		return sourceFile;
	}

	public final void setSourceFile(final File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public String getId() {
		return myId;
	}

	public void setId(final String id) {
		myId = id;
	}

	public String getName() {
		return myName;
	}

	public void setName(final String name) {
		myName = name;
	}

	public String getDescription() {
		return myDescription;
	}

	public void setDescription(final String description) {
		myDescription = description;
	}

	public String getMetaDescription() {
		return myMetaDescription;
	}

	public void setMetaDescription(final String metaDescription) {
		myMetaDescription = metaDescription;
	}

	public String getMetaKeywords() {
		return myMetaKeywords;
	}

	public void setMetaKeywords(final String metaKeywords) {
		myMetaKeywords = metaKeywords;
	}

	public String getHtmlTitle() {
		return myHtmlTitle;
	}

	public void setHtmlTitle(final String htmlTitle) {
		myHtmlTitle = htmlTitle;
	}
}
