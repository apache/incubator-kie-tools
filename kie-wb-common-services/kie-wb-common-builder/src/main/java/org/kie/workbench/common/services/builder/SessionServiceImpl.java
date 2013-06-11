package org.kie.workbench.common.services.builder;

import javax.inject.Inject;

import org.guvnor.m2repo.service.M2RepoService;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.backend.session.SessionService;
import org.kie.workbench.common.services.shared.project.Project;
import org.kie.workbench.common.services.shared.exceptions.GenericPortableException;

public class SessionServiceImpl
        implements SessionService {

    private LRUBuilderCache cache;
    private M2RepoService m2RepoService;

    public SessionServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public SessionServiceImpl( final LRUBuilderCache cache,
                               final M2RepoService m2RepoService ) {
        this.cache = cache;
        this.m2RepoService = m2RepoService;
    }

    @Override
    public KieSession newKieSession( final Project project,
                                     final String sessionName ) {

        final Builder builder = cache.assertBuilder( project );

        KieContainer kieContainer = null;

        try {
            kieContainer = builder.getKieContainer();

        } catch ( RuntimeException e ) {
            throw new GenericPortableException( e.getMessage() );
        }

        return kieContainer.newKieSession( sessionName );
    }

}
