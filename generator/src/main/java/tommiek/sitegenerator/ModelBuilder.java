package tommiek.sitegenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;

import tommiek.sitegenerator.model.AbstractEntity;
import tommiek.sitegenerator.model.Category;
import tommiek.sitegenerator.model.Image;
import tommiek.sitegenerator.model.Product;

public final class ModelBuilder {

	private List<Product> products;
	private List<Category> categories;

	public ModelBuilder build(final File productsDir, final File categoriesDir) throws GenerateException {
		products = buildProducts(productsDir);
		categories = buildCategories(categoriesDir);
		attach();
		return this;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public List<Product> getProducts() {
		return products;
	}

	private void attach() throws GenerateException {
		final Map<String, Product> productMap = buildProductMap(products);
		for (final Category category : categories) {
			attachProducts(category, productMap);
		}
	}

	private List<Product> buildProducts(final File productsBaseDir) throws GenerateException {
		SiteGenerator.LOG.log(Level.INFO, "Building product model in " + productsBaseDir.getAbsolutePath());
		final ProductsBuilder productsBuilder = new ProductsBuilder();
		final List<Product> products = productsBuilder.buildEntities(productsBaseDir);
		SiteGenerator.LOG.log(Level.INFO, "Successfully processed " + products.size() + " products");
		return products;
	}

	private List<Category> buildCategories(final File categoriesBaseDir) throws GenerateException {
		SiteGenerator.LOG.log(Level.INFO, "Building category model in " + categoriesBaseDir.getAbsolutePath());
		final CategoriesBuilder categoriesBuilder = new CategoriesBuilder();
		final List<Category> categories = categoriesBuilder.buildEntities(categoriesBaseDir);
		SiteGenerator.LOG.log(Level.INFO, "Successfully processed " + categories.size() + " categories");
		return categories;
	}

	private Map<String, Product> buildProductMap(final List<Product> products) throws GenerateException {
		final Map<String, Product> productMap = new HashMap<>();
		for (final Product product : products) {
			if (productMap.containsKey(product.getId())) {
				throw new GenerateException("Duplicate productid detected : " + product.getId());
			}
			productMap.put(product.getId(), product);
		}
		return productMap;
	}

	private void attachProducts(final Category category, final Map<String, Product> productMap)
			throws GenerateException {
		for (final String productid : category.getProductIds()) {
			final Product product = productMap.get(productid);
			if (product == null)
				throw new GenerateException("Category lists unknown product " + productid + " at "
						+ category.getSourceFile());
			category.addProduct(product);
			product.setCategory(category);
		}
	}

	static abstract class EntityBuilder<T extends AbstractEntity> {

		public List<T> buildEntities(final File directory) throws GenerateException {
			final List<T> entities = new LinkedList<>();
			final Collection<File> files = FileUtils.listFiles(directory, new String[] { "xml" }, true);
			for (final File file : files) {
				if (file.getName().startsWith("_")) {
					continue;
				}
				T entity = buildEntity(file);
				if (entity != null) {
					entities.add(entity);
				}
			}
			return entities;
		}

		private T buildEntity(final File file) throws GenerateException {
			SiteGenerator.LOG.log(Level.FINE, "\tprocessing " + file.getAbsolutePath());
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				@SuppressWarnings("unchecked")
				final T entity = (T) getUnmarshaller().unmarshal(fis);
				entity.setSourceFile(file.getParentFile());
				postProcess(entity);
				return entity;
			} catch (IOException | JAXBException e) {
				throw new GenerateException("Failed to build entity for file : " + file.getAbsolutePath(), e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
		}

		abstract Unmarshaller getUnmarshaller();

		abstract void postProcess(T entity) throws GenerateException;

	}

	static class CategoriesBuilder extends EntityBuilder<Category> {

		private Unmarshaller unmarshaller;

		public CategoriesBuilder() throws GenerateException {
			try {
				JAXBContext jc = JAXBContext.newInstance(new Class[] { Category.class, Image.class });
				unmarshaller = jc.createUnmarshaller();
			} catch (JAXBException e) {
				throw new GenerateException("Failed to create products unmarshaller", e);
			}
		}

		@Override
		Unmarshaller getUnmarshaller() {
			return unmarshaller;
		}

		@Override
		void postProcess(final Category category) throws GenerateException {

			if (category.getId() == null || category.getId().equals(""))
				throw new GenerateException("Category has no id at " + category.getSourceFile().getAbsolutePath());
			if (category.getName() == null || category.getName().equals(""))
				throw new GenerateException("Category has no name at " + category.getSourceFile().getAbsolutePath());
			if (category.getDescription() == null || category.getDescription().equals(""))
				throw new GenerateException("Category has no description at "
						+ category.getSourceFile().getAbsolutePath());
			if (category.getLeadImage() == null || category.getLeadImage().equals(""))
				throw new GenerateException("Category has no leadimage at "
						+ category.getSourceFile().getAbsolutePath());
			if (category.getLeadImage().getSrc() == null || category.getLeadImage().getSrc().equals(""))
				throw new GenerateException("Category has no leadimage.src at "
						+ category.getSourceFile().getAbsolutePath());

			category.getLeadImage().setSrcFile(new File(category.getSourceFile(), category.getLeadImage().getSrc()));
			if (!category.getLeadImage().getSrcFile().exists())
				throw new GenerateException("Leadimage does not exist at " + category.getSourceFile().getAbsolutePath());
		}
	}

	static class ProductsBuilder extends EntityBuilder<Product> {

		private Unmarshaller unmarshaller;

		public ProductsBuilder() throws GenerateException {
			try {
				JAXBContext jc = JAXBContext.newInstance(new Class[] { Product.class, Image.class });
				unmarshaller = jc.createUnmarshaller();
			} catch (JAXBException e) {
				throw new GenerateException("Failed to create products unmarshaller", e);
			}
		}

		@Override
		Unmarshaller getUnmarshaller() {
			return unmarshaller;
		}

		@Override
		void postProcess(final Product product) throws GenerateException {

			if (product.getId() == null || product.getId().equals(""))
				throw new GenerateException("Product has no id at " + product.getSourceFile().getAbsolutePath());
			if (product.getName() == null || product.getName().equals(""))
				throw new GenerateException("Product has no name at " + product.getSourceFile().getAbsolutePath());
			if (product.getDescription() == null || product.getDescription().equals(""))
				throw new GenerateException("Product has no description at "
						+ product.getSourceFile().getAbsolutePath());
			if (product.getDetails() == null || product.getDetails().equals(""))
				throw new GenerateException("Product has no details at " + product.getSourceFile().getAbsolutePath());
			if (product.getLeadText() == null || product.getLeadText().equals(""))
				throw new GenerateException("Product has no leadText at " + product.getSourceFile().getAbsolutePath());

			if (product.getLeadImage() == null || product.getLeadImage().equals(""))
				throw new GenerateException("Product has no leadimage at " + product.getSourceFile().getAbsolutePath());
			if (product.getLeadImage().getSrc() == null || product.getLeadImage().getSrc().equals(""))
				throw new GenerateException("Product has no leadimage.src at "
						+ product.getSourceFile().getAbsolutePath());
			if (product.getLeadImage().getAlt() == null || product.getLeadImage().getAlt().equals(""))
				throw new GenerateException("Product has no leadimage.alt at "
						+ product.getSourceFile().getAbsolutePath());
			product.getLeadImage().setSrcFile(new File(product.getSourceFile(), product.getLeadImage().getSrc()));
			if (!product.getLeadImage().getSrcFile().exists())
				throw new GenerateException("Leadimage does not exist at " + product.getSourceFile().getAbsolutePath());

			if (product.getImages() == null)
				throw new GenerateException("Product has no images at " + product.getSourceFile().getAbsolutePath());
			for (final Image image : product.getImages()) {
				if (image.getSrc() == null || image.getSrc().equals(""))
					throw new GenerateException("Image has no src at " + product.getSourceFile().getAbsolutePath());
				if (image.getThumb() == null || image.getThumb().equals(""))
					throw new GenerateException("Image has no thumb at " + product.getSourceFile().getAbsolutePath());
				if (image.getAlt() == null || image.getAlt().equals(""))
					throw new GenerateException("Image has no alt at " + product.getSourceFile().getAbsolutePath());

				image.setSrcFile(new File(product.getSourceFile(), image.getSrc()));
				if (!image.getSrcFile().exists())
					throw new GenerateException("Image does not exist at " + image.getSrcFile().getAbsolutePath());
				image.setThumbFile(new File(product.getSourceFile(), image.getThumb()));
				if (!image.getThumbFile().exists())
					throw new GenerateException("Image does not exist at " + image.getThumbFile().getAbsolutePath());
			}
		}
	}
}
