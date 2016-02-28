package alainvanhout.cms.services;

import alainvanhout.demo.Template;
import alainvanhout.cms.exceptions.TemplateException;
import alainvanhout.demo.installers.BasicDataInstaller;
import alainvanhout.renderering.renderer.Renderer;
import alainvanhout.renderering.renderer.basic.StringRenderer;
import alainvanhout.cms.repositories.TemplateRepository;
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
