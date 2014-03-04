package org.drools.workbench.screens.dsltext.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.dsltext.client.resources.DSLTextEditorResources;
import org.drools.workbench.screens.dsltext.client.resources.i18n.DSLTextEditorConstants;
import org.drools.workbench.screens.dsltext.type.DSLResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class DSLResourceType
        extends DSLResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( DSLTextEditorResources.INSTANCE.images().typeDSL() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = DSLTextEditorConstants.INSTANCE.dslResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
