package alainvanhout.renderering.renderer.html.quick;

public class DOM {

    public static QuickSpanRenderer span(){
        return new QuickSpanRenderer();
    }

    public static QuickSpanRenderer span(Object item){
        return new QuickSpanRenderer(item);
    }

    public static QuickLinkRenderer a(){
        return new QuickLinkRenderer();
    }

    public static QuickLinkRenderer a(Object item){
        return new QuickLinkRenderer(item);
    }

    public static QuickParagraphRenderer p(){
        return new QuickParagraphRenderer();
    }

    public static QuickParagraphRenderer p(Object item){
        return new QuickParagraphRenderer(item);
    }

    public static QuickDivRenderer div(){
        return new QuickDivRenderer();
    }

    public static QuickDivRenderer div(Object item){
        return new QuickDivRenderer(item);
    }
}
