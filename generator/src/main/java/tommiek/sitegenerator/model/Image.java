package tommiek.sitegenerator.model;

import java.io.File;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class Image {

	private String myThumb;
	private String mySrc;
	private String myAlt;
	private File mySrcFile;
	private File myThumbFile;

	public String getThumb() {
		return myThumb;
	}

	public void setThumb(final String thumb) {
		myThumb = thumb;
	}

	public String getSrc() {
		return mySrc;
	}

	public void setSrc(final String src) {
		mySrc = src;
	}

	public String getAlt() {
		return myAlt;
	}

	public void setAlt(final String alt) {
		myAlt = alt;
	}

	@XmlTransient
	public File getSrcFile() {
		return mySrcFile;
	}

	public void setSrcFile(final File srcFile) {
		mySrcFile = srcFile;
	}

	@XmlTransient
	public File getThumbFile() {
		return myThumbFile;
	}

	public void setThumbFile(final File thumbFile) {
		myThumbFile = thumbFile;
	}
}
