package alainvanhout.cms.dtos.stored;

import alainvanhout.routing.Route;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "routes")
public class StoredRoute implements Route {

    @Id
    private String id;

    private String sectionId;
    private String templateId;

    // each subroute maps to another route.id
    private Map<String, String> routes = new HashMap<>();
    private Map<String, String> sections = new HashMap<>();
    private Map<String, String> templates = new HashMap<>();
    private List<String> contexts = new ArrayList<>();

    //private Map<String, List<String>> contexts = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public Map<String, String> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, String> routes) {
        this.routes = routes;
    }

    public Map<String, String> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, String> templates) {
        this.templates = templates;
    }

    public StoredRoute id(String id) {
        this.id = id;
        return this;
    }

    public StoredRoute templateId(String templateId) {
        this.templateId = templateId;
        return this;
    }

    public StoredRoute addRoute(String key, String routeId) {
        routes.put(key, routeId);
        return this;
    }

    public StoredRoute addRoute(String key, StoredRoute route) {
        return addRoute(key, route.getId());
    }

    public Map<String, String> getSections() {
        return sections;
    }

    public void setSections(Map<String, String> sections) {
        this.sections = sections;
    }

    public String getSectionForStep() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public boolean hasSubroute(String step){
        return routes.containsKey(step);
    }

    public String getRouteForStep(String step){
        return routes.get(step);
    }

    public boolean hasSection(String step){
        return routes.containsKey(step);
    }

    public String getSectionForStep(String step){
        return routes.get(step);
    }

    public boolean hasTemplate(String step){
        return templates.containsKey(step);
    }

    public String getTemplateForStep(String step){
        return routes.get(step);
    }

    public String getSectionId() {
        return sectionId;
    }

    public List<String> getContexts() {
        return contexts;
    }

    public void setContexts(List<String> contexts) {
        this.contexts = contexts;
    }
}
