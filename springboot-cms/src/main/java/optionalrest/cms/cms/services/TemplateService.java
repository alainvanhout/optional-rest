package optionalrest.cms.cms.services;

import optionalrest.cms.entities.Template;
import optionalrest.cms.cms.exceptions.TemplateException;
import optionalrest.cms.installers.BasicDataInstaller;
import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;
import optionalrest.cms.cms.repositories.TemplateRepository;
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
            throw new TemplateException("Could not find template with id " + templateId);
        }
        return template.getBody();
    }

    public Renderer findBodyAsRenderer(String templateId){
        return new StringRenderer(findBody(templateId));
    }
}
