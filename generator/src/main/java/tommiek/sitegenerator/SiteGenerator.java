package tommiek.sitegenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import tommiek.sitegenerator.model.Category;
import tommiek.sitegenerator.model.Image;
import tommiek.sitegenerator.model.Product;

public final class SiteGenerator {

	public final static Logger LOG = Logger.getLogger("Tommiek SiteGenerator");

	public static void main(String[] args) {

		LOG.setLevel(Level.FINE);

		File workDir = null;
		final String workDirProperty = System.getProperty("directory");
		if (workDirProperty != null && !workDirProperty.equals("")) {
			workDir = new File(workDirProperty);
		} else {
			workDir = new File(System.getProperty("user.dir"));
		}
		if (!workDir.exists() || !workDir.isDirectory() || !workDir.canRead() || !workDir.canWrite()) {
			System.err.println("Configured sitedir is not accesible: " + workDirProperty);
			return;
		}

		final Properties properties = new Properties();
		try (final InputStream is = SiteGenerator.class.getClassLoader().getResourceAsStream("tommiek.properties");) {
			if (is != null)
				properties.load(is);
		} catch (final IOException e) {
			System.err.println("Failed to load default properties");
			return;
		}

		String propertiesProperty = System.getProperty("properties");
		if (propertiesProperty == null || propertiesProperty.equals("")) {
			propertiesProperty = "tommiek.properties";
		}
		try (final FileInputStream fis = new FileInputStream(propertiesProperty);) {
			final Properties customProperties = new Properties();
			customProperties.load(fis);
			properties.putAll(customProperties);
		} catch (final IOException e) {
			System.err.println("Failed to load properties: " + propertiesProperty);
			return;
		}

		final SiteGenerator siteGenerator = new SiteGenerator(properties);
		siteGenerator.generateSite(workDir);
	}

	private Properties myProperties;

	public SiteGenerator(final Properties properties) {
		myProperties = properties;
	}

	public void generateSite(final File directory) {

		final long startTime = System.currentTimeMillis();
		final File targetDir = new File(directory, "target");
		final File staticDir = new File(directory, "static");
		final File templatesDir = new File(directory, "templates");
		final File productsDir = new File(directory, "products");
		final File categoriesDir = new File(directory, "categories");

		LOG.log(Level.INFO,
				new StringBuilder()
						.append("\n----------------------------------------------------------------------------------------")
						.append("\n >> Tommiek SiteGenerator starting!")
						.append("\n----------------------------------------------------------------------------------------")
						.toString());

		try {
			final SitemapBuilder sitemapBuilder = new SitemapBuilder();
			final PageGenerator pageGenerator = new PageGenerator(directory, myProperties);
			final ModelBuilder modelBuilder = new ModelBuilder().build(productsDir, categoriesDir);

			LOG.log(Level.INFO, "Cleaning target directory " + targetDir.getAbsolutePath());
			ensureCLeanTargetDir(targetDir);

			LOG.log(Level.INFO, "Copying static site from " + staticDir.getAbsolutePath());
			copyStaticDirToTargetDir(staticDir, targetDir);

			LOG.log(Level.INFO, "Generating home page in " + targetDir.getAbsolutePath());
			pageGenerator.generateStandardPage("home", "_index.html", new File(targetDir, "index.html"));
			sitemapBuilder.addUrl("http://www.tommiek.nl/index.html", 1.0f);

			LOG.log(Level.INFO, "Generating profiel page in " + targetDir.getAbsolutePath());
			pageGenerator.generateStandardPage("profiel", "_profiel.html", new File(targetDir, "profiel.html"));
			sitemapBuilder.addUrl("http://www.tommiek.nl/profiel.html", 0.8f);

			LOG.log(Level.INFO, "Generating werkwijze page in " + targetDir.getAbsolutePath());
			pageGenerator.generateStandardPage("werkwijze", "_werkwijze.html", new File(targetDir, "werkwijze.html"));
			sitemapBuilder.addUrl("http://www.tommiek.nl/werkwijze.html", 0.8f);

			LOG.log(Level.INFO, "Generating contact page in " + targetDir.getAbsolutePath());
			pageGenerator.generateStandardPage("contact", "_contact.html", new File(targetDir, "contact.html"));
			sitemapBuilder.addUrl("http://www.tommiek.nl/context.html", 0.8f);

			LOG.log(Level.INFO, "Generating bedankt page in " + targetDir.getAbsolutePath());
			pageGenerator.generateStandardPage("bedankt", "_bedankt.html", new File(targetDir, "bedankt.html"));
			sitemapBuilder.addUrl("http://www.tommiek.nl/bedankt.html", 0.2f);

			LOG.log(Level.INFO, "Generating portfolio page in " + targetDir.getAbsolutePath());
			generatePortFolio(pageGenerator, sitemapBuilder, modelBuilder.getCategories(), targetDir);

			LOG.log(Level.INFO, "Generating category pages in " + targetDir.getAbsolutePath());
			generateCategories(pageGenerator, sitemapBuilder, modelBuilder.getCategories(), targetDir);

			LOG.log(Level.INFO, "Generating sitemap in " + targetDir.getAbsolutePath());
			sitemapBuilder.build(new File(targetDir, "sitemap.xml"));
			LOG.log(Level.INFO,
					new StringBuilder()
							.append("\n----------------------------------------------------------------------------------------")
							.append("\nSuccessfully generated " + sitemapBuilder.getCount() + " pages in "
									+ (System.currentTimeMillis() - startTime) + " ms")
							.append("\nSite deployed at " + targetDir.getAbsolutePath())
							.append("\n----------------------------------------------------------------------------------------")
							.toString());
		} catch (final GenerateException e) {
			LOG.log(Level.SEVERE, "Foutje!!!! \n" + e.getMessage());
		}
	}

	private void ensureCLeanTargetDir(final File targetDir) throws GenerateException {
		if (targetDir.exists()) {
			try {
				FileUtils.cleanDirectory(targetDir);
			} catch (final IOException e) {
				throw new GenerateException("Failed to clean target directory at " + targetDir.getAbsolutePath(), e);
			}
		} else {
			if (!targetDir.mkdirs()) {
				throw new GenerateException("Failed to create target directory at " + targetDir.getAbsolutePath());
			}
		}
	}

	private void copyStaticDirToTargetDir(final File staticDir, final File targetDir) throws GenerateException {
		try {
			FileUtils.copyDirectory(staticDir, targetDir);
		} catch (final IOException e) {
			throw new GenerateException("Failed to copy static files to target directory", e);
		}
	}

	public void generatePortFolio(final PageGenerator pageGenerator, final SitemapBuilder sitemapBuilder,
			final List<Category> categories, final File targetDir) throws GenerateException {
		final File categoryfile = new File(targetDir, "portfolio.html");
		pageGenerator.generatPortfolioPage(categories, categoryfile);
		sitemapBuilder.addUrl("http://www.tommiek.nl/portfolio.html", 0.8f);
	}

	public void generateCategories(final PageGenerator pageGenerator, final SitemapBuilder sitemapBuilder,
			final List<Category> categories, final File targetDir) throws GenerateException {

		for (final Category category : categories) {

			final File categoryDir = new File(targetDir, category.getId());
			categoryDir.mkdirs();

			final File categoryfile = new File(categoryDir, "index.html");
			pageGenerator.generateCategoryPage(categories, category, categoryfile);
			sitemapBuilder.addUrl("http://www.tommiek.nl/" + category.getId() + "/index.html", 0.8f);
			copyImage(categoryDir, category.getLeadImage());

			for (final Product product : category.getProducts()) {

				copyImage(categoryDir, product.getLeadImage());

				final File file = new File(categoryDir, product.getId() + ".html");
				pageGenerator.generateProductPage(product, category, file);
				sitemapBuilder.addUrl("http://www.tommiek.nl/" + category.getId() + "/" + product.getId() + ".html",
						0.8f);
				for (final Image image : product.getImages()) {
					copyImage(categoryDir, image);
				}
			}
		}
	}

	private void copyImage(final File targetDir, final Image image) throws GenerateException {
		try {
			if (image.getSrcFile() != null) {
				FileUtils.copyFileToDirectory(image.getSrcFile(), targetDir);
			}
		} catch (IOException e) {
			throw new GenerateException("Failed to copy product image " + image.getSrcFile().getAbsolutePath(), e);
		}
		try {
			if (image.getThumbFile() != null) {
				FileUtils.copyFileToDirectory(image.getThumbFile(), targetDir);
			}
		} catch (IOException e) {
			throw new GenerateException("Failed to copy product image " + image.getThumbFile().getAbsolutePath(), e);
		}
	}
}
