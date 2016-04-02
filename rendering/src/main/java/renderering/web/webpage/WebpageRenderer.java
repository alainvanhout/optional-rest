package renderering.web.webpage;

import renderering.Situation;
import renderering.core.Renderer;
import renderering.web.html.basic.documenthead.ScriptLinkRenderer;
import renderering.web.html.basic.documenthead.StylesheetLinkRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WebpageRenderer implements RendererWithResources {

    private String title = "";
    private String description = "";
    private String language = "en";
    private String charset = "utf-8";
    private String author = "";
    private Renderer body;
    private List<Resource> resources;
    private List<StylesheetLinkRenderer> stylesheets = new ArrayList<>();
    private List<ScriptLinkRenderer> scripts = new ArrayList<>();

    @Override
    public String render() {

        String rendering = "<!doctype html>" +
                "<html lang=\"" + language + "\">\n" +
                "<head>\n" +
                "<meta charset=\"" + charset + "\">\n" +
                "<title>" + title + "</title>\n" +
                "<meta name=\"description\" content=\"" + description + "\">\n" +
                "<meta name=\"author\" content=\"" + author + "\">\n" +
                stylesheets.stream().map(Renderer::render).collect(Collectors.joining("\n")) + "\n" +
                scripts.stream().map(Renderer::render).collect(Collectors.joining("\n")) + "\n" +
                "</head>\n" +
                "<body>\n" +
                body.render() +
                "</body>\n" +
                "</html>\n";

        return rendering;
    }

    @Override
    public String renderWithResources(Situation situation) {
        return null;
    }


    public String getTitle() {
        return this.title;
    }

    public WebpageRenderer title(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public WebpageRenderer description(String description) {
        this.description = description;
        return this;
    }

    public String getLanguage() {
        return this.language;
    }

    public WebpageRenderer language(String language) {
        this.language = language;
        return this;
    }

    public String getCharset() {
        return this.charset;
    }

    public WebpageRenderer charset(String charset) {
        this.charset = charset;
        return this;
    }

    public String getAuthor() {
        return this.author;
    }

    public WebpageRenderer author(String author) {
        this.author = author;
        return this;
    }

    public Renderer getBody() {
        return this.body;
    }

    public WebpageRenderer body(Renderer body) {
        this.body = body;
        return this;
    }

    public List<Resource> getResources() {
        return this.resources;
    }

    public WebpageRenderer resources(List<Resource> resources) {
        this.resources = resources;
        return this;
    }

    public List<StylesheetLinkRenderer> getStylesheets() {
        return this.stylesheets;
    }

    public WebpageRenderer stylesheets(List<StylesheetLinkRenderer> stylesheets) {
        this.stylesheets = stylesheets;
        return this;
    }

    public List<ScriptLinkRenderer> getScripts() {
        return this.scripts;
    }

    public WebpageRenderer scripts(List<ScriptLinkRenderer> scripts) {
        this.scripts = scripts;
        return this;
    }

    public WebpageRenderer stylesheet(String href){
        stylesheets.add(new StylesheetLinkRenderer(href));
        return this;
    }

    public WebpageRenderer script(String src){
        scripts.add(new ScriptLinkRenderer(src));
        return this;
    }
}
