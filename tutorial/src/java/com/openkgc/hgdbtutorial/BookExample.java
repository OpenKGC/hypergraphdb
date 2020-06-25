package com.openkgc.hgdbtutorial;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.HyperGraph;

import java.util.List;

public class BookExample {
    public static void main(String[] args) {
        String databaseLocation = Configurations.DBPATH;
        HyperGraph graph = null;
        try {
            graph = new HyperGraph(databaseLocation);
            Book mybook = new Book("Critique of Pure Reason", "me");
            HGHandle bookHandle = graph.add(mybook);

            List<Book> books = hg.getAll(graph, hg.and(hg.type(Book.class), hg.eq("author", "me")));
            for (Book book : books)
                System.out.println(book.getTitle());
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            graph.close();
        }
    }
}
