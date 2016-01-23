package tommiek.sitegenerator.model.sitemap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "urlset")
@XmlAccessorType(XmlAccessType.FIELD)
public class UrlSet {

	@XmlElement(name = "url")
	public List<Url> urls = new LinkedList<>();

	public void addUrl(final String loc, final float priority) {
		urls.add(new Url(loc, priority));
	}

	public int getSize() {
		return urls.size();
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Url {

		@XmlTransient
		private static final SimpleDateFormat sfd = new SimpleDateFormat("YYYY-MM-dd");

		public String loc;
		public String lastmod;
		public String changefreq;
		public float priority;

		public Url(final String loc, final float priority) {
			this.loc = loc;
			this.priority = priority;
			this.changefreq = "monthly";
			this.lastmod = sfd.format(new Date());
		}
	}
}
