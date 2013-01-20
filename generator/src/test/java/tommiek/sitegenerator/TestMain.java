package tommiek.sitegenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public final class TestMain {

	@Test
	public void testMain() throws Exception {

		final Properties properties = new Properties();
		try (final InputStream is = SiteGenerator.class.getClassLoader().getResourceAsStream("tommiek.properties");) {
			if (is != null)
				properties.load(is);
		} catch (final IOException e) {
			System.err.println("Failed to load default properties");
			return;
		}

		SiteGenerator sg = new SiteGenerator(properties);
		sg.generateSite(new File("E:/tommiek/repository/tommiek-site"));
	}
}
