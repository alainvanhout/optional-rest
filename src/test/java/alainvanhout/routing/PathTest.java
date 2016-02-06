package alainvanhout.routing;

import alainvanhout.routing.path.Path;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PathTest {

    @Test
    public void typicalParse(){
        String query = "example/hello/world?foo1=bar1&foo2=bar2a,bar1b&foo3=bar3";
        Path path = Path.fromQuery(query);
        assertEquals( "example/hello/world", path.getQueryPath());

        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "example");
        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "hello");
        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "world");
        assertFalse(path.hasNextStep());

        assertTrue(path.hasParameter("foo1"));
        assertEquals(path.getParameter("foo1"), "bar1");
        assertTrue(path.hasParameter("foo2"));
        assertEquals(path.getParameter("foo2"), "bar2a,bar1b");
        assertTrue(path.hasParameter("foo3"));
        assertEquals(path.getParameter("foo3"), "bar3");
        assertFalse(path.hasParameter("foo4"));
    }

    @Test
    public void typicalParseFromRoot(){
        String query = "/example/hello/world?foo1=bar1&foo2=bar2a,bar1b&foo3=bar3";
        Path path = Path.fromQuery(query);
        assertEquals( "/example/hello/world", path.getQueryPath());

        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "/");
        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "example");
        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "hello");
        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "world");
        assertFalse(path.hasNextStep());

        assertTrue(path.hasParameter("foo1"));
        assertEquals(path.getParameter("foo1"), "bar1");
        assertTrue(path.hasParameter("foo2"));
        assertEquals(path.getParameter("foo2"), "bar2a,bar1b");
        assertTrue(path.hasParameter("foo3"));
        assertEquals(path.getParameter("foo3"), "bar3");
        assertFalse(path.hasParameter("foo4"));
    }

    @Test
    public void withoutParameters(){
        String query = "example/hello/world";
        Path path = Path.fromQuery(query);
        assertEquals( "example/hello/world", path.getQueryPath());

        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "example");
        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "hello");
        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "world");
        assertFalse(path.hasNextStep());

        assertFalse(path.hasParameter("foo1"));
        assertFalse(path.hasParameter("foo2"));
        assertFalse(path.hasParameter("foo3"));
        assertFalse(path.hasParameter("foo4"));
    }

    @Test
    public void withEmptyParameters(){
        String query = "example/hello/world?";
        Path path = Path.fromQuery(query);
        assertEquals( "example/hello/world", path.getQueryPath());

        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "example");
        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "hello");
        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "world");
        assertFalse(path.hasNextStep());

        assertFalse(path.hasParameter("foo1"));
        assertFalse(path.hasParameter("foo2"));
        assertFalse(path.hasParameter("foo3"));
        assertFalse(path.hasParameter("foo4"));
    }

    @Test
    public void withoutPath(){
        String query = "?foo1=bar1&foo2=bar2a,bar1b&foo3=bar3";
        Path path = Path.fromQuery(query);
        assertEquals( "", path.getQueryPath());

        assertFalse(path.hasNextStep());

        assertTrue(path.hasParameter("foo1"));
        assertEquals(path.getParameter("foo1"), "bar1");
        assertTrue(path.hasParameter("foo2"));
        assertEquals(path.getParameter("foo2"), "bar2a,bar1b");
        assertTrue(path.hasParameter("foo3"));
        assertEquals(path.getParameter("foo3"), "bar3");
        assertFalse(path.hasParameter("foo4"));
    }


    @Test
    public void withOnlyRootPathAndParameters(){
        String query = "/?foo1=bar1&foo2=bar2a,bar1b&foo3=bar3";
        Path path = Path.fromQuery(query);
        assertEquals( "/", path.getQueryPath());

        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "/");
        assertFalse(path.hasNextStep());

        assertTrue(path.hasParameter("foo1"));
        assertEquals(path.getParameter("foo1"), "bar1");
        assertTrue(path.hasParameter("foo2"));
        assertEquals(path.getParameter("foo2"), "bar2a,bar1b");
        assertTrue(path.hasParameter("foo3"));
        assertEquals(path.getParameter("foo3"), "bar3");
        assertFalse(path.hasParameter("foo4"));
    }

    @Test
    public void withoutPathOrParameters(){
        String query = "";
        Path path = Path.fromQuery(query);
        assertEquals( "", path.getQueryPath());

        assertFalse(path.hasNextStep());

        assertFalse(path.hasParameter("foo1"));
        assertFalse(path.hasParameter("foo2"));
        assertFalse(path.hasParameter("foo3"));
        assertFalse(path.hasParameter("foo4"));
    }

    @Test
    public void withOnlyRootPathAndWithoutParameters(){
        String query = "/";
        Path path = Path.fromQuery(query);
        assertEquals( "/", path.getQueryPath());

        assertTrue(path.hasNextStep());
        assertEquals(path.nextStep(), "/");
        assertFalse(path.hasNextStep());

        assertFalse(path.hasParameter("foo1"));
        assertFalse(path.hasParameter("foo2"));
        assertFalse(path.hasParameter("foo3"));
        assertFalse(path.hasParameter("foo4"));
    }
}
