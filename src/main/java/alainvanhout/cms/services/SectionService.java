package alainvanhout.cms.services;

import alainvanhout.demo.services.RendererService;
import renderering.core.Renderer;
import alainvanhout.cms.repositories.SectionRepository;
import alainvanhout.routing.path.Path;
import alainvanhout.cms.dtos.custom.CustomSection;
import alainvanhout.sections.Section;
import alainvanhout.cms.dtos.stored.StoredSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private Map<String, CustomSection> sectionMap;

    @Autowired
    private Collection<CustomSection> sections;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private RendererService rendererService;

    @Autowired
    private TemplateService templateService;

    @PostConstruct
    private void setup() {
        sectionMap = sections.stream().collect(Collectors.toMap(CustomSection::getId, s -> s));
    }

    public Section findSection(String sectionId) {
        // first try custom sections
        if (sectionMap.containsKey(sectionId)){
            return sectionMap.get(sectionId);
        }

        // fallback to repository
        Section section = sectionRepository.findOne(sectionId);
        if (section == null) {
            throw new RuntimeException("Section does not exist: " + sectionId);
        }
        return section;
    }

    public Renderer arrive(String sectionId, Path path) {
        Section section = findSection(sectionId);
        return arrive(section, path);
    }

    public Renderer arrive(Section section, Path path) {
        if (section instanceof CustomSection) {
            return section.getRenderer(path);
        } else if (section instanceof StoredSection) {
            return templateService.findBodyAsRenderer(((StoredSection)section).getTemplateId());
        }
        throw new RuntimeException("No renderer found for section");
    }
}
