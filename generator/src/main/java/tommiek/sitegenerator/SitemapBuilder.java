package tommiek.sitegenerator;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import tommiek.sitegenerator.model.sitemap.UrlSet;

public final class SitemapBuilder {

	private UrlSet urlSet = new UrlSet();

	public void addUrl(final String loc, final float priority) {
		urlSet.addUrl(loc, priority);
	}
	
	public int getCount(){
		return urlSet.getSize();
	}

	public void build(final File file) throws GenerateException {
		try {
			JAXBContext jc = JAXBContext.newInstance(new Class[] { UrlSet.class });
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(urlSet, file);
		} catch (JAXBException e) {
			throw new GenerateException("Failed to create products unmarshaller", e);
		}

	}
}
