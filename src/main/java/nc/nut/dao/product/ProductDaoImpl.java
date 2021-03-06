package nc.nut.dao.product;

import nc.nut.dao.user.UserDAO;
import nc.nut.mail.Mailer;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rysakova Anna on 23.04.2017.
 */
@Service
public class ProductDaoImpl implements ProductDao {

    private final static String FIND_ALL_CATEGORIES = "SELECT * FROM PRODUCT_CATEGORIES";
    private final static String FIND_CATEGORY = "SELECT ID FROM PRODUCT_CATEGORIES WHERE NAME=:name";
    private final static String FIND_TYPES = "SELECT NAME FROM PRODUCT_TYPES";
    private final static String FIND_SERVICES = "SELECT * FROM PRODUCTS WHERE TYPE_ID=2 ORDER BY ID";
    private final static String FIND_TARIFFS = "SELECT * FROM PRODUCTS WHERE TYPE_ID=1 ORDER BY ID";
    private final static String SELECT_BY_ID_SQL = "SELECT * FROM PRODUCTS WHERE ID = :id";
    private final static String FIND_PRODUCT_BY_ID = "SELECT * FROM PRODUCTS WHERE ID=:id";

    private final static String FIND_ALL_PRODUCTS = "SELECT ID,TYPE_ID,CATEGORY_ID,NAME,DURATION," +
            "NEED_PROCESSING,DESCRIPTION,STATUS FROM PRODUCTS ORDER BY ID";

    private final static String FIND_SERVICES_BY_TARIFF = "SELECT p.ID,p.CATEGORY_ID,p.NAME,p.DURATION,p.NEED_PROCESSING,p.DESCRIPTION,p.STATUS " +
            "FROM PRODUCTS p JOIN TARIFF_SERVICES ts ON (p.ID=ts.SERVICE_ID) WHERE ts.TARIFF_ID=:tariffId";

    private final static String FIND_ALL_SERVICES = "SELECT prod.ID, prod.NAME, prod.DESCRIPTION,prod.DURATION,prod.NEED_PROCESSING,\n" +
            "prod.TYPE_ID,prod.CATEGORY_ID\n" +
            "FROM PRODUCTS prod JOIN PRODUCT_TYPES pTypes ON (prod.TYPE_ID=pTypes.ID)\n" +
            "JOIN PRODUCT_CATEGORIES pCategories ON (prod.CATEGORY_ID=pCategories.ID)\n" +
            "WHERE prod.STATUS=1 AND pTypes.name='Service' AND pCategories.name=:categoryName";

    private final static String FIND_SERVICES_NOT_IN_TARIFF = "SELECT p.ID,p.CATEGORY_ID,p.NAME,p.DURATION,p.NEED_PROCESSING,p.DESCRIPTION,p.STATUS " +
            "FROM PRODUCTS p" +
            " WHERE p.ID NOT IN " +
            "                  (SELECT ts.SERVICE_ID FROM TARIFF_SERVICES ts  " +
            "                  WHERE ts.TARIFF_ID=:tariffId) AND p.TYPE_ID=2";

    private final static String FIND_ALL_SERVICES_WITH_CATEGORY = "SELECT prod.ID, prod.NAME, prod.DESCRIPTION,prod.DURATION,prod.NEED_PROCESSING,\n" +
            "prod.TYPE_ID,prod.CATEGORY_ID, pCategories.NAME as Category\n" +
            "FROM PRODUCTS prod JOIN PRODUCT_TYPES pTypes ON (prod.TYPE_ID=pTypes.ID)\n" +
            "JOIN PRODUCT_CATEGORIES pCategories ON (prod.CATEGORY_ID=pCategories.ID)\n" +
            "WHERE prod.STATUS=1 AND pTypes.name='Service'";

    private final static String FIND_ALL_FREE_TARIFFS = "SELECT * FROM PRODUCTS p join PRODUCT_TYPES ptype ON(p.TYPE_ID=ptype.ID)\n" +
            "LEFT JOIN TARIFF_SERVICES ts ON(p.ID=ts.TARIFF_ID)\n" +
            "WHERE ptype.name='Tariff' AND ts.TARIFF_ID IS NULL";

    private final static String UPDATE_SERVICE = "UPDATE PRODUCTS SET NAME=:name," +
            "DURATION=:duration,NEED_PROCESSING=:needProcessing," +
            "DESCRIPTION=:description,STATUS=:status WHERE ID=:id";

    private final static String ADD_TARIFF_SERVICE = "INSERT INTO TARIFF_SERVICES VALUES(:tariff_id,:service_id)";
    private final static String ADD_CATEGORY = "INSERT INTO PRODUCT_CATEGORIES(NAME,DESCRIPTION) VALUES(:name,:description)";
    private final static String ADD_PRODUCT = "INSERT INTO PRODUCTS(TYPE_ID,CATEGORY_ID,NAME,DURATION," +
            "NEED_PROCESSING,DESCRIPTION,STATUS) VALUES(:typeId,:categoryId,:nameProduct,:duration," +
            ":needProcessing,:description,:status)";
    private final static String SELECT_SERVICES_BY_PLACE_SQL = "SELECT\n" +
            "  PRODUCTS.ID,\n" +
            "  PRODUCTS.TYPE_ID,\n" +
            "  PRODUCTS.CATEGORY_ID,\n" +
            "  PRODUCTS.NAME,\n" +
            "  PRODUCTS.DURATION,\n" +
            "  PRODUCTS.NEED_PROCESSING,\n" +
            "  PRODUCTS.STATUS,\n" +
            "PRODUCTS.DESCRIPTION, " +
            "  PRODUCTS.BASE_PRICE,\n" +
            "  PRODUCTS.CUSTOMER_TYPE_ID " +
            "FROM PRODUCTS\n" +
            "  JOIN PRICES ON PRICES.PRODUCT_ID =  products.ID\n" +
            "WHERE PRICES.PLACE_ID = :place_id AND PRODUCTS.STATUS = 1 /*active status id*/ AND PRODUCTS.TYPE_ID = 2 /*service id*/";
    private final static String SELECT_PRODUCT_CATEGORY_BY_ID_SQL = "SELECT ID, NAME, DESCRIPTION FROM PRODUCT_CATEGORIES\n" +
            "WHERE ID = :id";
    private final static String GET_CURRENT_USER_TARIFF_BY_USER_ID_SQL = "SELECT " +
            "id, " +
            "category_id, " +
            "duration, " +
            "type_id, " +
            "need_processing, " +
            "name, " +
            "description, " +
            "status FROM Products " +
            "WHERE id = (SELECT product_id FROM Orders WHERE user_id = :userId AND current_status_id = 1/* id = 1 - active status */)";
    private final static String GET_TARIFFS_BY_PLACE_SQL = "SELECT " +
            "id, " +
            "category_id, " +
            "duration, " +
            "type_id, " +
            "need_processing, " +
            "name, " +
            "description, " +
            "status FROM Products " +
            "WHERE id IN (SELECT place_id FROM Prices WHERE product_id = :placeId) AND type_id = 1/*id = 1 - Tariff*/";
    private final static String SELECT_ALL_SERVICES_OF_USER_CURRENT_TERIFF_SQL = "SELECT\n" +
            "  p2.ID,\n" +
            "  p2.NAME,\n" +
            "  p2.DESCRIPTION,\n" +
            "  p2.TYPE_ID,\n" +
            "  p2.CATEGORY_ID,\n" +
            "  p2.DURATION,\n" +
            "  p2.NEED_PROCESSING,\n" +
            "  p2.DESCRIPTION,\n" +
            "  p2.STATUS,\n" +
            "  p2.BASE_PRICE\n " +
            "FROM PRODUCTS p1\n" +
            "  JOIN ORDERS ON ORDERS.PRODUCT_ID = p1.ID\n" +
            "                 AND ORDERS.USER_ID = :id\n" +
            "                 AND p1.TYPE_ID = 1\n" +
            "  JOIN TARIFF_SERVICES\n" +
            "    ON p1.ID = TARIFF_SERVICES.TARIFF_ID\n" +
            "  JOIN PRODUCTS p2 ON p2.ID = TARIFF_SERVICES.SERVICE_ID";

    private final static String DELETE_SERVICE_FROM_TARIFF = "DELETE FROM TARIFF_SERVICES " +
            "WHERE TARIFF_ID=:idTariff AND SERVICE_ID=:idService ";

    private final static String DELETE_BY_ID = "DELETE FROM  Products where id=:id";

    private final static String FIND_PRODUCT_FOR_USER = "SELECT prod.ID as ID, prod.NAME as NAME," +
            "prod.description as DESCRIPTION, prod.DURATION as duration " +
            "FROM PRODUCTS prod JOIN ORDERS ord ON (prod.ID=ord.PRODUCT_ID) JOIN OPERATION_STATUS" +
            " status ON(ord.CURRENT_STATUS_ID = status.ID)" +
            "WHERE ord.USER_ID = :id AND status.NAME != 'Deactivated'";
    private final static String FIND_ACTIVE_PRODUCTS_FOR_USER = "SELECT prod.ID as ID, prod.NAME as NAME " +
            "FROM PRODUCTS prod JOIN ORDERS ord ON (prod.ID=ord.PRODUCT_ID) JOIN OPERATION_STATUS" +
            " status ON(ord.CURRENT_STATUS_ID = status.ID)" +
            "WHERE ord.USER_ID = :id AND status.NAME = 'Active'";

    @Resource
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Resource
    private ProductCategoriesRowMapper categoriesRowMapper;
    @Resource
    private ProductRowMapper productRowMapper;
    @Resource
    private UserDAO userDAO;
    @Resource
    private Mailer mailer;

    @Override
    public boolean save(Product product) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        ProductType type = product.getProductType();
        switch (type) {
            case Tariff:
                params.addValue("typeId", 1);
                break;
            case Service:
                params.addValue("typeId", 2);
                break;
        }
        params.addValue("categoryId", product.getCategoryId());
        params.addValue("nameProduct", product.getName());
        params.addValue("duration", product.getDurationInDays());
        params.addValue("needProcessing", product.getNeedProcessing());
        params.addValue("description", product.getDescription());
        params.addValue("status", product.getStatus());

        int isUpdate = jdbcTemplate.update(ADD_PRODUCT, params);
        return isUpdate > 0;

    }

    @Override
    public List<ProductCategories> findProductCategories() {
        return jdbcTemplate.query(FIND_ALL_CATEGORIES, categoriesRowMapper);
    }

    @Override
    public List<Product> getAllServices() {
        return jdbcTemplate.query(FIND_SERVICES, productRowMapper);
    }

    @Override
    public List<Product> getAllTariffs() {
        return jdbcTemplate.query(FIND_TARIFFS, productRowMapper);
    }

    @Override
    public boolean fillTariff(int idTariff, int idService) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tariff_id", idTariff);
        params.addValue("service_id", idService);
        int isUpdate = jdbcTemplate.update(ADD_TARIFF_SERVICE, params);

        return isUpdate > 0;
    }

    @Override
    public boolean addCategory(ProductCategories categories) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", categories.getName());
        params.addValue("description", categories.getDescription());
        int isUpdate = jdbcTemplate.update(ADD_CATEGORY, params);
        return isUpdate > 0;
    }

    @Override
    public int findIdCategory(ProductCategories categories) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", categories.getName());
        ProductCategories categoriesList = jdbcTemplate.queryForObject(FIND_CATEGORY, params, (rs, rowNum) -> {
            ProductCategories productCategories = new ProductCategories();
            productCategories.setId(rs.getInt("ID"));
            return productCategories;
        });
        return categoriesList.getId();
    }

    @Override
    public List<String> findProductTypes() {
        List<String> tariffs = jdbcTemplate.query(FIND_TYPES, (rs, rowNum) -> {
            String type = rs.getString("NAME");
            return type;
        });
        return tariffs;
    }

    //TODO почему бы не использовать внешний класс ProductRowMapper?
    @Override
    public List<Product> getAllServices(String categoryName) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("categoryName", categoryName);
        List<Product> services = jdbcTemplate.query(FIND_ALL_SERVICES, params, (rs, rowNum) -> {
            Product product = new Product();
            product.setCategoryId(rs.getInt("CATEGORY_ID"));
            product.setId(rs.getInt("ID"));
            Integer productType = rs.getInt("type_id");
            product.setProductType(ProductType.getProductTypeByID(rs.getInt("type_id")));
            product.setNeedProcessing(rs.getInt("NEED_PROCESSING"));
            product.setDurationInDays(rs.getInt("DURATION"));
            product.setName(rs.getString("NAME"));
            product.setDescription(rs.getString("DESCRIPTION"));
            return product;
        });

        return services;
    }

    @Override
    public List<Product> getAllFreeTariffs() {
        List<Product> tariffs = jdbcTemplate.query(FIND_ALL_FREE_TARIFFS, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getInt("ID"));
            Integer productType = rs.getInt("type_id");
            product.setProductType(ProductType.getProductTypeByID(rs.getInt("type_id")));
            product.setNeedProcessing(rs.getInt("NEED_PROCESSING"));
            product.setDurationInDays(rs.getInt("DURATION"));
            product.setName(rs.getString("NAME"));
            product.setDescription(rs.getString("DESCRIPTION"));
            return product;
        });
        return tariffs;
    }

    @Override
    public boolean delete(Product product) {
        return false;
    }

    @Override
    public Product getById(int id) {

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", id);
        return jdbcTemplate.queryForObject(SELECT_BY_ID_SQL, parameterSource, productRowMapper);
    }

    @Override
    public List<Product> getByTypeId(int id) {
        return null;
    }

    @Override
    public List<Product> getByCategoryId(int id) {
        return null;
    }

    @Override
    public List<Product> getByProcessingStatus(int id) {
        return null;
    }

    @Override
    public boolean update(Product product) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", product.getName());
        params.addValue("duration", product.getDurationInDays());
        params.addValue("needProcessing", product.getNeedProcessing());
        params.addValue("description", product.getDescription());
        params.addValue("status", product.getStatus());
        params.addValue("id", product.getId());
        int isUpdate = jdbcTemplate.update(UPDATE_SERVICE, params);
        return isUpdate > 0;
    }

    @Override
    public List<Product> getAllAvailableServicesByPlace(Integer placeId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("place_id", placeId);
        return jdbcTemplate.query(SELECT_SERVICES_BY_PLACE_SQL, params, new ProductRowMapper());
    }

    @Override
    public ProductCategories getProductCategoryById(Integer categoryId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", categoryId);
        return jdbcTemplate.queryForObject(SELECT_PRODUCT_CATEGORY_BY_ID_SQL, params, new ProductCategoriesRowMapper());
    }

    /**
     * Method returns current user`s tariff. If user hasn`t got tariff, method returns null.
     *
     * @param userId user Id.
     * @return current user`s tariff.
     */
    @Override
    public Product getCurrentUserTariff(Integer userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        return jdbcTemplate.queryForObject(GET_CURRENT_USER_TARIFF_BY_USER_ID_SQL, params, new ProductRowMapper());
    }

    /**
     * Method returns all tariffs are available in place with id from params. If there are no tariffs in this place, method returns empty list.
     *
     * @param placeId id of place.
     * @return list of tariffs.
     */
    @Override
    public List<Product> getAvailableTariffsByPlace(Integer placeId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("placeId", placeId);
        return jdbcTemplate.query(GET_TARIFFS_BY_PLACE_SQL, params, new ProductRowMapper());
    }

    @Override
    public Map<String, List<Product>> getAllServicesWithCategory() {
        Map<String, List<Product>> serviceMap = new HashMap<>();
        List<Product> services = jdbcTemplate.query(FIND_ALL_SERVICES_WITH_CATEGORY, (rs, rowNum) -> {
            Product product = new Product();
            product.setCategoryId(rs.getInt("CATEGORY_ID"));
            product.setId(rs.getInt("ID"));
            Integer typeId = rs.getInt("TYPE_ID");
            switch (typeId) {
                case 1:
                    product.setProductType(ProductType.Tariff);
                    break;
                case 2:
                    product.setProductType(ProductType.Service);
                    break;
            }
            product.setNeedProcessing(rs.getInt("NEED_PROCESSING"));
            product.setDurationInDays(rs.getInt("DURATION"));
            product.setName(rs.getString("NAME"));
            product.setDescription(rs.getString("DESCRIPTION"));
            String category = rs.getString("Category");
            if (serviceMap.containsKey(category)) {
                List<Product> serv = serviceMap.get(category);
                serv.add(product);
            } else {
                List<Product> serv = new ArrayList<>();
                serv.add(product);
                serviceMap.put(category, serv);
            }

            return product;
        });

        return serviceMap;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = jdbcTemplate.query(FIND_ALL_PRODUCTS, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getInt("ID"));
            Integer typeId = rs.getInt("TYPE_ID");
            switch (typeId) {
                case 1:
                    product.setProductType(ProductType.Tariff);
                    break;
                case 2:
                    product.setProductType(ProductType.Service);
                    break;
            }
            product.setCategoryId(rs.getInt("CATEGORY_ID"));
            product.setNeedProcessing(rs.getInt("NEED_PROCESSING"));
            product.setDurationInDays(rs.getInt("DURATION"));
            product.setName(rs.getString("NAME"));
            product.setDescription(rs.getString("DESCRIPTION"));
            product.setStatus(rs.getInt("STATUS"));
            return product;
        });
        return productList;
    }

    @Override
    public List<Product> getServicesByTariff(Product product) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tariffId", product.getId());
        List<Product> productList = jdbcTemplate.query(FIND_SERVICES_BY_TARIFF, params, (rs, rowNum) -> {
            Product p = new Product();
            p.setId(rs.getInt("ID"));
            p.setProductType(ProductType.Service);
            p.setCategoryId(rs.getInt("CATEGORY_ID"));
            p.setNeedProcessing(rs.getInt("NEED_PROCESSING"));
            p.setDurationInDays(rs.getInt("DURATION"));
            p.setName(rs.getString("NAME"));
            p.setDescription(rs.getString("DESCRIPTION"));
            p.setStatus(rs.getInt("STATUS"));
            return p;
        });
        return productList;
    }

    @Override
    public List<Product> getServicesNotInTariff(Product product) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tariffId", product.getId());
        List<Product> productList = jdbcTemplate.query(FIND_SERVICES_NOT_IN_TARIFF, params, (rs, rowNum) -> {
            Product p = new Product();
            p.setId(rs.getInt("ID"));
            p.setProductType(ProductType.Service);
            p.setCategoryId(rs.getInt("CATEGORY_ID"));
            p.setNeedProcessing(rs.getInt("NEED_PROCESSING"));
            p.setDurationInDays(rs.getInt("DURATION"));
            p.setName(rs.getString("NAME"));
            p.setDescription(rs.getString("DESCRIPTION"));
            p.setStatus(rs.getInt("STATUS"));
            return p;
        });
        return productList;
    }

    @Override
    public boolean deleteServiceFromTariff(int idTariff, int idService) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idTariff", idTariff);
        params.addValue("idService", idService);
        int isDelete = jdbcTemplate.update(DELETE_SERVICE_FROM_TARIFF, params);
        return isDelete > 0;
    }

    @Override
    public boolean deleteById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        int isDelete = jdbcTemplate.update(DELETE_BY_ID, params);
        return isDelete > 0;
    }

    @Override
    public List<Product> getProductsByUserId(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        List<Product> products = jdbcTemplate.query(FIND_PRODUCT_FOR_USER, params, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getInt("ID"));
            product.setName(rs.getString("NAME"));
            product.setDescription(rs.getString("DESCRIPTION"));
            product.setDurationInDays(rs.getInt("DURATION"));
            return product;
        });
        return products;
    }

    @Override
    public List<Product> getActiveProductByUserId(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        List<Product> products = jdbcTemplate.query(FIND_ACTIVE_PRODUCTS_FOR_USER, params, (rs, rowNum) -> {
            Product product = new Product();
            product.setId(rs.getInt("ID"));
            product.setName(rs.getString("NAME"));
            return product;
        });
        return products;
    }

    public List<Product> getAllServicesByCurrentUserTarifff(Integer userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", userId);
        return jdbcTemplate.query(SELECT_ALL_SERVICES_OF_USER_CURRENT_TERIFF_SQL, params, new ProductRowMapper());

    }
}
