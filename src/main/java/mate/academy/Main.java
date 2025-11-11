package mate.academy;

import mate.academy.entity.Book;
import mate.academy.lib.Injector;
import mate.academy.repository.BookDao;

import java.math.BigDecimal;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.academy");

    public static void main(String[] args) {
        BookDao bookDao = (BookDao) injector.getInstance(BookDao.class);
        Book book = new Book();
        book.setTitle("Clean Code");
        book.setPrice(new BigDecimal("39.99"));

        bookDao.create(book);
        System.out.println("Inserted book id: " + book.getId());
        System.out.println("All books: " + bookDao.findAll());
    }
}
