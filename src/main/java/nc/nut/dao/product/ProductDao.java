package nc.nut.dao.product;

import nc.nut.dao.interfaces.Dao;

import java.util.List;
import java.util.Map;

/**
 * Created by Rysakova Anna , Alistratenko Nikita on 23.04.2017.
 */
public interface ProductDao extends Dao<Product> {

    List<Product> getByTypeId(int id);

    List<Product> getByCategoryId(int id);

    List<Product> getByProcessingStatus(int id);

    List<String> findProductTypes();

    List<ProductCategories> findProductCategories();

    List<Product> getAllServices();

    List<Product> getAllTariffs();

    List<Product> getAllServices(String categoryName);

    List<Product> getAllFreeTariffs();

    boolean fillTariff(int idTariff, int idService);

    boolean addCategory(ProductCategories categories);

    int findIdCategory(ProductCategories categories);

    /**
     * Method returns all services that are available in place.
     * created by Yuliya Pedash
     * @param placeId id of place
     * @return all available services
     */
    List<Product> getAllAvailableServicesByPlace(Integer placeId);

    /**
     * Gets category of product by id
     * @param categoryId id of category
     * @return ProductCategory
     */

    ProductCategories getProductCategoryById(Integer categoryId);

    /**
     * Method returns current user`s tariff. If user hasn`t got tariff, method returns null.
     *
     * @param userId user Id.
     * @return current user`s tariff.
     */
    Product getCurrentUserTariff(Integer userId);

    /**
     * Method returns all tariffs are available in place with id from params. If there are no tariffs in this place, method returns empty list.
     *
     * @param placeId id of place.
     * @return list of tariffs.
     */
    List<Product> getAvailableTariffsByPlace(Integer placeId);

    /**
     * This method returns all services that are included in current user's Tariff.
     *
     * @param userId id of user
     * @return list of products with service type.
     */
    List<Product> getAllServicesByCurrentUserTarifff(Integer userId);


    Map<String, List<Product>> getAllServicesWithCategory();
//    List<Product> getAllServicesWithCategory();

    List<Product> getAllProducts();

    List<Product> getServicesByTariff(Product product);

    List<Product> getServicesNotInTariff(Product product);

    boolean deleteServiceFromTariff(int idTariff, int idService);

    boolean deleteById(int id);

    List<Product> getProductsByUserId(int id);

    List<Product> getActiveProductByUserId(Integer id);

}
