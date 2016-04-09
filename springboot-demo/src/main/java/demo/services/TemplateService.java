package demo.services;

import demo.entities.Template;
import demo.installers.BasicDataInstaller;
import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;
import demo.repositories.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private BasicDataInstaller basicDataInstaller;

    public String findBody(String templateId){
        Template template = templateRepository.findOne(templateId);
        if (template == null){
            throw new RuntimeException("Could not find template with id " + templateId);
        }
        return template.getBody();
    }

    public Renderer findBodyAsRenderer(String templateId){
        return new StringRenderer(findBody(templateId));
    }
}
