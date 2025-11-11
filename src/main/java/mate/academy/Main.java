package mate.academy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import mate.academy.entity.Book;
import mate.academy.lib.Injector;
import mate.academy.repository.BookDao;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.academy");

    public static void main(String[] args) {
        BookDao bookDao = (BookDao) injector.getInstance(BookDao.class);

        Book book = new Book();
        book.setTitle("Clean Code");
        book.setPrice(new BigDecimal("39.99"));
        Book createdBook = bookDao.create(book);
        System.out.println("Created: " + createdBook);

        Long id = createdBook.getId();

        Optional<Book> foundBook = bookDao.findById(id);
        System.out.println("Find by id " + id + ": " + foundBook.orElse(null));

        System.out.println("All books before update:");
        List<Book> booksBeforeUpdate = bookDao.findAll();
        booksBeforeUpdate.forEach(System.out::println);

        createdBook.setTitle("Clean Code — Updated Edition");
        createdBook.setPrice(new BigDecimal("44.99"));
        Book updatedBook = bookDao.update(createdBook);
        System.out.println("Updated: " + updatedBook);

        System.out.println("Find by id after update: " + bookDao.findById(id).orElse(null));

        boolean deleted = bookDao.deleteById(id);
        System.out.println("Deleted book id " + id + ": " + deleted);

        Optional<Book> afterDelete = bookDao.findById(id);
        System.out.println("Find by id after delete: " + afterDelete.orElse(null));

        System.out.println("All books after delete:");
        List<Book> booksAfterDelete = bookDao.findAll();
        booksAfterDelete.forEach(System.out::println);
    }
}
