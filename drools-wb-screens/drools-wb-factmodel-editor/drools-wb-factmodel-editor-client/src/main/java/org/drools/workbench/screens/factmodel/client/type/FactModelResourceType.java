package org.drools.workbench.screens.factmodel.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.factmodel.client.resources.FactModelResources;
import org.drools.workbench.screens.factmodel.type.FactModelResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class FactModelResourceType
        extends FactModelResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( FactModelResources.INSTANCE.images().factModelIcon() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

}
