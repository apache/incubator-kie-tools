package org.kie.uberfire.properties.editor.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.uberfire.properties.editor.model.PropertyEditorCategory;

@Remote
public interface BeanPropertyEditorBuilderService {

    PropertyEditorCategory extract( String fqcn );

    PropertyEditorCategory extract( String fqcn,
                                    Object instance );
}
