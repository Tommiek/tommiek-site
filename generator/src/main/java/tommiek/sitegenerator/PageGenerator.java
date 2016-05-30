package tommiek.sitegenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import tommiek.sitegenerator.model.Category;
import tommiek.sitegenerator.model.Product;

public class PageGenerator {

	private final Properties myProperties;

	public PageGenerator(final File templatesDir, final Properties properties) throws GenerateException {
		myProperties = properties;
		final Properties p = new Properties();
		p.setProperty("resource.loader", "file");
		p.setProperty("file.resource.loader.path", templatesDir.getAbsolutePath());
		p.setProperty("runtime.log.logsystem.class", org.apache.velocity.runtime.log.NullLogChute.class.getName());
		p.setProperty("runtime.log.logsystem.root", "");
		try {
			Velocity.init(p);
		} catch (final Exception e) {
			throw new GenerateException("Failed to initialize the page generator", e);
		}
	}

	public void generateStandardPage(final String page, final String template, final File file)
			throws GenerateException {
		SiteGenerator.LOG.log(Level.FINE, "\tgenerating " + file.getAbsolutePath());
		final Context context = new VelocityContext();
		context.put("htmlTitle", getProperty("htmlTitle", page, "", "", ""));
		context.put("metaDescription", getProperty("metaDescription", page, "", "", ""));
		context.put("metaKeywords", getProperty("metaKeywords", page, "", "", ""));
		writePage(file, template, context);
	}

	public void generatePortfolioPage(final List<Category> categories, final File file) throws GenerateException {
		SiteGenerator.LOG.log(Level.INFO, "\tgenerating " + file.getAbsolutePath());
		final List<Product> specials = new LinkedList<>();
		for (final Category category : categories) {
		    for(final Product product : category.getProducts()){
		        if(product.getType() != null && product.getType().equals("special") && !specials.contains(product)){
		            specials.add(product);
		        }
		    }
		}
		final Context context = new VelocityContext();
		context.put("htmlTitle", getProperty("htmlTitle", "portfolio", "", "", ""));
		context.put("metaDescription", getProperty("metaDescription", "portfolio", "", "", ""));
		context.put("metaKeywords", getProperty("metaKeywords", "portfolio", "", "", ""));
		context.put("specials", specials);
		context.put("categories", categories);
		writePage(file, "_portfolio.html", context);
	}

	public void generateCategoryPage(final List<Category> categories, final Category category, final File file)
			throws GenerateException {
		SiteGenerator.LOG.log(Level.FINE, "\tgenerating " + file.getAbsolutePath());
		final List<Category> linkCategories = new LinkedList<>();
		for (final Category linkCategory : categories) {
			if (linkCategory.getType().equals(category.getType())) {
				linkCategories.add(linkCategory);
			}
		}
		final Context context = new VelocityContext();
		context.put("htmlTitle", getProperty("htmlTitle", "category", category.getHtmlTitle(), "", category.getName()));
		context.put("metaDescription",
				getProperty("metaDescription", "category", category.getMetaDescription(), "", category.getName()));
		context.put("metaKeywords",
				getProperty("metaKeywords", "category", category.getMetaKeywords(), "", category.getName()));
		context.put("linkCategories", linkCategories);
		context.put("category", category);
		writePage(file, "_category.html", context);
	}

	public void generateProductPage(final Product product, final Category category, final File file)
			throws GenerateException {
		SiteGenerator.LOG.log(Level.FINE, "\tgenerating " + file.getAbsolutePath());
		final Context context = new VelocityContext();
		context.put("htmlTitle",
				getProperty("htmlTitle", "product", product.getHtmlTitle(), category.getName(), product.getName()));
		context.put(
				"metaDescription",
				getProperty("metaDescription", "product", product.getMetaDescription(), category.getName(),
						product.getName()));
		context.put(
				"metaKeywords",
				getProperty("metaKeywords", "product", product.getMetaKeywords(), category.getName(), product.getName()));
		context.put("category", category);
		context.put("product", product);
		for (int i = 0; i < category.getProducts().size(); i++) {
			if (category.getProducts().get(i).getId().equals(product.getId())) {
				if (i > 0) {
					context.put("prevproduct", category.getProducts().get(i - 1));
				}
				if (i < category.getProducts().size() - 1) {
					context.put("nextproduct", category.getProducts().get(i + 1));
				}
			}
		}
		writePage(file, "_product.html", context);
	}

	private void writePage(final File file, final String template, final Context context) throws GenerateException {
		try (final Writer fileWriter = new FileWriter(file);) {
			final Template velocityTemplate = Velocity.getTemplate(template);
			velocityTemplate.merge(context, fileWriter);
		} catch (Exception e) {
			throw new GenerateException("Failed to generate page: " + file.getAbsolutePath(), e);
		}
	}

	private String getProperty(final String key, final String page, final String override, final String categoryValue,
			final String productValue) {
		String property = override;
		if (property == null || property.equals("")) {
			property = myProperties.getProperty(page + "." + key);
			if (property == null || property.equals("")) {
				property = myProperties.getProperty(key);
			}
		}
		if (property != null && !property.equals("")) {
			property = property.replace("@category@", categoryValue == null ? "" : categoryValue);
			property = property.replace("@product@", productValue == null ? "" : productValue);
		}
		return property;
	}
}
