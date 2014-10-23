package org.kie.uberfire.perspective.editor.client.side;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.uberfire.perspective.editor.client.api.ExternalPerspectiveEditorComponent;

@Dependent
public class PerspectiveEditorSidePresenterHelper {

    @Inject
    SyncBeanManager manager;

    public List<ExternalPerspectiveEditorComponent> lookupExternalComponents() {
        final Collection<IOCBeanDef<ExternalPerspectiveEditorComponent>> iocBeanDefs = manager.lookupBeans( ExternalPerspectiveEditorComponent.class );
        List<ExternalPerspectiveEditorComponent> components = new ArrayList<ExternalPerspectiveEditorComponent>();
        for ( IOCBeanDef<ExternalPerspectiveEditorComponent> iocBeanDef : iocBeanDefs ) {
            components.add( iocBeanDef.getInstance() );
        }
        return components;
    }
}
