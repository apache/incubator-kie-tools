package org.drools.workbench.jcr2vfsmigration.vfs;

import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Singleton
public class RefactoringQueryServiceFactory {

    @Produces
    public RefactoringQueryService getRefactoringQueryService() {
        return new RefactoringQueryServiceImpl();
    }
}