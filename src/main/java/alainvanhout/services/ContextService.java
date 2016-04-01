package alainvanhout.services;

import alainvanhout.cms.repositories.ContextRepository;
import context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContextService {

    @Autowired
    private ContextRegistry contextRegistry;

    @Autowired
    private ContextRepository contextRepository;

    public Context get(String id){
        if (contextRegistry.contains(id)){
            return contextRegistry.get(id);
        }
        if (contextRepository.exists(id)){
            return contextRepository.findOne(id);
        }
        return null;
    }
}
