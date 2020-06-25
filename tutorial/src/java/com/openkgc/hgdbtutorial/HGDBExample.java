package com.openkgc.hgdbtutorial;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGQuery.hg;

public class HGDBExample {
    public static void main(String[] args) {
        String databaseLocation = Configurations.DBPATH;
        HyperGraph graph = null;
        try {
            graph = new HyperGraph(databaseLocation);
            String hello = graph.get(graph.add("Hello World"));
            System.out.println(hello.toLowerCase());

            String someObject = "Lorem ipsum";
            HGHandle handle1 = graph.add(someObject);
            System.out.println(((String) graph.get(handle1)).toUpperCase());

            System.out.println(hg.getAll(graph, hg.type(String.class)));
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (graph != null) {
                graph.close();
            }
        }
    }
}