package mate.academy.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.entity.Book;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.repository.BookDao;
import mate.academy.util.ConnectionUtil;

@Dao
public class BookDaoImpl implements BookDao {
    private static final String INSERT_QUERY =
            "INSERT INTO book (title, price) VALUES (?, ?)";
    private static final String SELECT_BY_ID_QUERY =
            "SELECT id, title, price FROM book WHERE id = ?";
    private static final String SELECT_ALL_QUERY =
            "SELECT id, title, price FROM book";
    private static final String UPDATE_QUERY =
            "UPDATE book SET title = ?, price = ? WHERE id = ?";
    private static final String DELETE_QUERY =
            "DELETE FROM book WHERE id = ?";

    public BookDaoImpl() {

    }

    @Override
    public Book create(Book book) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(
                        INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getTitle());
            stmt.setBigDecimal(2, book.getPrice());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getLong(1));
                }
            }

            return book;

        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert book into DB: " + book, e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapBook(rs));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DataProcessingException("Can't get book by id " + id, e);
        }
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_QUERY);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapBook(rs));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all books", e);
        }
        return books;
    }

    @Override
    public Book update(Book book) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(UPDATE_QUERY)) {
            stmt.setString(1, book.getTitle());
            stmt.setBigDecimal(2, book.getPrice());
            stmt.setLong(3, book.getId());

            int updatedRows = stmt.executeUpdate();
            if (updatedRows == 0) {
                throw new DataProcessingException("Book with id " + book.getId() + " not found");
            }

            return book;

        } catch (SQLException e) {
            throw new DataProcessingException("Can't update book: " + book, e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(DELETE_QUERY)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete book with id " + id, e);
        }
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getObject("id", Long.class));
        book.setTitle(rs.getString("title"));
        book.setPrice(rs.getBigDecimal("price"));
        return book;
    }
}
