package org.drools.workbench.screens.drltext.model;

import java.util.List;

import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class DrlModelContent {

    private String drl;
    private List<String> fullyQualifiedClassNames;
    private List<DSLSentence> dslConditions;
    private List<DSLSentence> dslActions;
    private Overview overview;

    public DrlModelContent() {
    }

    public DrlModelContent( final String drl,
                            final Overview overview,
                            final List<String> fullyQualifiedClassNames,
                            final List<DSLSentence> dslConditions,
                            final List<DSLSentence> dslActions ) {
        this.overview = PortablePreconditions.checkNotNull( "overview",
                                                            overview);
        this.drl = PortablePreconditions.checkNotNull( "drl",
                                                       drl );
        this.fullyQualifiedClassNames = PortablePreconditions.checkNotNull( "fullyQualifiedClassNames",
                                                                            fullyQualifiedClassNames );
        this.dslConditions = PortablePreconditions.checkNotNull( "dslConditions",
                                                                 dslConditions );
        this.dslActions = PortablePreconditions.checkNotNull( "dslActions",
                                                              dslActions );
    }

    public String getDrl() {
        return this.drl;
    }

    public List<String> getFullyQualifiedClassNames() {
        return this.fullyQualifiedClassNames;
    }

    public List<DSLSentence> getDslConditions() {
        return dslConditions;
    }

    public List<DSLSentence> getDslActions() {
        return dslActions;
    }

    public Overview getOverview() {
        return overview;
    }
}
