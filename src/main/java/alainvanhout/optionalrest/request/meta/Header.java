package alainvanhout.optionalrest.request.meta;

public class Header {
    public static class Accept {
        public static final String ALL = "*/*";
        public static class Text {
            public static final String ALL = "text/*";
            public static final String HTML = "text/html";
        }
        public static class Application {
            public static final String ALL = "application/*";
            public static final String JSON = "application/json";
        }
    }
}
