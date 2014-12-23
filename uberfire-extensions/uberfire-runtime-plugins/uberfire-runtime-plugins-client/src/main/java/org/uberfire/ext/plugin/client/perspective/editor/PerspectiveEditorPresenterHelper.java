package org.uberfire.ext.plugin.client.perspective.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.plugin.client.perspective.editor.api.ExternalPerspectiveEditorComponent;

@Dependent
public class PerspectiveEditorPresenterHelper {

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
