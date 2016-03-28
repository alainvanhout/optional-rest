package renderering.web.html.quick;

import renderering.web.html.ElementRenderer;

public interface QuickElement extends ElementRenderer {

    default QuickSpanRenderer span(){
        QuickSpanRenderer element = new QuickSpanRenderer();
        this.add(element);
        return element;
    }

    default QuickSpanRenderer span(Object item){
        QuickSpanRenderer element = new QuickSpanRenderer(item);
        this.add(element);
        return element;
    }

    default QuickLinkRenderer a(){
        QuickLinkRenderer element = new QuickLinkRenderer();
        this.add(element);
        return element;
    }

    default QuickLinkRenderer a(Object item){
        QuickLinkRenderer element = new QuickLinkRenderer(item);
        this.add(element);
        return element;
    }

    default QuickParagraphRenderer p(){
        QuickParagraphRenderer element = new QuickParagraphRenderer();
        this.add(element);
        return element;
    }

    default QuickParagraphRenderer p(Object item){
        QuickParagraphRenderer element = new QuickParagraphRenderer(item);
        this.add(element);
        return element;
    }

    default QuickDivRenderer div(){
        QuickDivRenderer element = new QuickDivRenderer();
        this.add(element);
        return element;
    }

    default QuickDivRenderer div(Object item){
        QuickDivRenderer element = new QuickDivRenderer(item);
        this.add(element);
        return element;
    }


}
