package optionalrest.cms.templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import renderering.core.Renderer;
import renderering.core.basic.StringRenderer;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateDataInstaller templateDataInstaller;

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
