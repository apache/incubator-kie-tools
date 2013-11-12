package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.guvnor.m2repo.client.widgets.AbstractPagedJarTable;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;

public class DependencyPagedJarTable
        extends AbstractPagedJarTable {

    public DependencyPagedJarTable( final Caller<M2RepoService> m2RepoService ) {
        super( m2RepoService );
    }

    public DependencyPagedJarTable( final Caller<M2RepoService> m2RepoService,
                                    final String searchFilter ) {
        super( m2RepoService,
               searchFilter );
    }

}
