package org.kie.workbench.common.services.datamodel.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;

/**
 * Portable representation of an annotation
 */
@Portable
public class Annotation {

    private String qualifiedTypeName;
    private Map<String, String> attributes = new HashMap<String, String>();

    public Annotation() {
        //Needed for Errai marshalling
    }

    public Annotation( final String qualifiedTypeName ) {
        PortablePreconditions.checkNotNull( "qualifiedTypeName",
                                            qualifiedTypeName );
        this.qualifiedTypeName = qualifiedTypeName;
    }

    public String getQualifiedTypeName() {
        return qualifiedTypeName;
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap( attributes );
    }

    public void addAttribute( final String name,
                              final String value ) {
        PortablePreconditions.checkNotNull( "name",
                                            name );
        this.attributes.put( name,
                             value );
    }
}
