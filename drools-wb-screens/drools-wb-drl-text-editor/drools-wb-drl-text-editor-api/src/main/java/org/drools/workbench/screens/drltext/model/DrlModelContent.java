package org.drools.workbench.screens.drltext.model;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class DrlModelContent {

    private String drl;
    private List<String> fullyQualifiedClassNames;

    public DrlModelContent() {
    }

    public DrlModelContent( final String drl,
                            final List<String> fullyQualifiedClassNames ) {
        this.drl = PortablePreconditions.checkNotNull( "drl",
                                                       drl );
        this.fullyQualifiedClassNames = PortablePreconditions.checkNotNull( "fullyQualifiedClassNames",
                                                                            fullyQualifiedClassNames );
    }

    public String getDrl() {
        return this.drl;
    }

    public List<String> getFullyQualifiedClassNames() {
        return this.fullyQualifiedClassNames;
    }

}
