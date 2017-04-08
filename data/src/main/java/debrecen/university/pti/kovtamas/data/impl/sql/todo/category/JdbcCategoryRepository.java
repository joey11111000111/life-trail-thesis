package debrecen.university.pti.kovtamas.data.impl.sql.todo.category;

import debrecen.university.pti.kovtamas.data.entity.todo.CategoryEntity;
import debrecen.university.pti.kovtamas.data.impl.sql.datasource.DataSourceManager;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategoryNotFoundException;
import debrecen.university.pti.kovtamas.data.impl.todo.exceptions.CategorySaveFailureException;
import debrecen.university.pti.kovtamas.data.interfaces.todo.CategoryRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcCategoryRepository implements CategoryRepository {

    static private final JdbcCategoryRepository INSTANCE;

    static {
        INSTANCE = new JdbcCategoryRepository();
    }

    static public JdbcCategoryRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public List<CategoryEntity> findAll() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            return findAll(conn);
        } catch (SQLException sqle) {
            log.warn("Failed to find all categories!", sqle);
            return Collections.EMPTY_LIST;
        }
    }

    private List<CategoryEntity> findAll(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(CategoryQueries.FIND_ALL);
        return extractResults(results);
    }

    private List<CategoryEntity> extractResults(ResultSet results) throws SQLException {
        List<CategoryEntity> extractedEntities = new ArrayList<>();
        while (results.next()) {
            CategoryEntity entity = extractResult(results);
            extractedEntities.add(entity);
        }

        return extractedEntities;
    }

    private CategoryEntity extractResult(ResultSet result) throws SQLException {
        Integer id = result.getInt("ID");
        String name = result.getString("NAME");
        return new CategoryEntity(id, name);
    }

    @Override
    public CategoryEntity findById(final int id) throws CategoryNotFoundException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Optional<CategoryEntity> entityOpt = findById(conn, id);
            if (entityOpt.isPresent()) {
                return entityOpt.get();
            }

            throw new CategoryNotFoundException("Category with id: " + id + " was not found");
        } catch (SQLException sqle) {
            throw new CategoryNotFoundException("SQLException happened while trying to find category by ID", sqle);
        }
    }

    private Optional<CategoryEntity> findById(Connection conn, int id) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(CategoryQueries.FIND_BY_ID);
        statement.setInt(1, id);

        ResultSet result = statement.executeQuery();
        if (result.next()) {
            CategoryEntity loadedEntity = extractResult(result);
            return Optional.of(loadedEntity);
        }
        return Optional.empty();
    }

    @Override
    public int idOf(@NonNull final String categoryName) throws CategoryNotFoundException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Optional<Integer> entityOpt = idOf(conn, categoryName);
            if (entityOpt.isPresent()) {
                return entityOpt.get();
            }

            throw new CategoryNotFoundException("Failed to find category, cannot return its ID");
        } catch (SQLException sqle) {
            throw new CategoryNotFoundException("SQLException happened while trying to find category ID", sqle);
        }
    }

    private Optional<Integer> idOf(Connection conn, String categoryName) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(CategoryQueries.FIND_BY_NAME);
        statement.setString(1, categoryName);
        ResultSet result = statement.executeQuery();

        if (result.next()) {
            CategoryEntity entity = extractResult(result);
            return Optional.of(entity.getId());
        }
        return Optional.empty();
    }

    @Override
    public CategoryEntity saveOrUpdate(@NonNull final CategoryEntity entity) throws CategorySaveFailureException {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            if (entity.hasId()) {
                return update(conn, entity);
            } else {
                return save(conn, entity.getName());
            }
        } catch (SQLException sqle) {
            throw new CategorySaveFailureException("Failed to persist new category: " + entity.getName(), sqle);
        }
    }

    private CategoryEntity save(Connection conn, String newCategoryName) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(CategoryQueries.INSERT, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, newCategoryName);
        statement.execute();

        Integer generatedId = extractGeneratedKey(statement);
        return new CategoryEntity(generatedId, newCategoryName);
    }

    private Integer extractGeneratedKey(PreparedStatement statement) throws SQLException {
        ResultSet result = statement.getGeneratedKeys();
        if (result.next()) {
            return result.getInt(1);
        }

        throw new SQLException("Generated ID could not be retrived!");
    }

    private CategoryEntity update(Connection conn, CategoryEntity entity) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(CategoryQueries.UPDATE);
        statement.setString(1, entity.getName());
        statement.setInt(2, entity.getId());
        statement.execute();

        return new CategoryEntity(entity.getId(), entity.getName());
    }

    @Override
    public void remove(@NonNull final Integer id) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(CategoryQueries.REMOVE_BY_ID);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException sqle) {
            log.warn("Failed to remove category with id: " + id, sqle);
        }
    }

    @Override
    public void remove(@NonNull final String categoryName) {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            PreparedStatement statement = conn.prepareStatement(CategoryQueries.REMOVE_BY_NAME);
            statement.setString(1, categoryName);
            statement.executeUpdate();
        } catch (SQLException sqle) {
            log.warn("Failed to remove category with name: " + categoryName, sqle);
        }
    }

    @Override
    public void clearTable() {
        try (Connection conn = DataSourceManager.getDataSource().getConnection()) {
            Statement clearStatement = conn.createStatement();
            clearStatement.executeUpdate(CategoryQueries.CLEAR_TABLE);
        } catch (SQLException sqle) {
            log.error("Failed to clear category table!", sqle);
        }
    }

}
