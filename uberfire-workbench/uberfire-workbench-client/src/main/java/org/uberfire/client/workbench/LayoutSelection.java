package org.uberfire.client.workbench;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

/**
 * Used to discover alternative {@link org.uberfire.client.workbench.WorkbenchLayout}'s.
 * If no alternatives can be found, the default {@link org.uberfire.client.workbench.WorkbenchLayoutImpl} is used.
 * If several implementations are found the first one will be chosen.
 */
@ApplicationScoped
public class LayoutSelection {

    @Inject
    private SyncBeanManager iocManager;

    static final AlternativeLayout altLayout = new AlternativeLayout() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return AlternativeLayout.class;
        }
    };

    public WorkbenchLayout get() {
        //FIXME: this alternatives process doesn't work
        WorkbenchLayout layout = null;

        Collection<IOCBeanDef<WorkbenchLayout>> beanDefs = iocManager.lookupBeans( WorkbenchLayout.class, altLayout );
        if ( beanDefs.size() > 0 ) {
            IOCBeanDef<WorkbenchLayout> alt = beanDefs.iterator().next();
            layout = alt.getInstance();
        } else {
            layout = iocManager.lookupBean( WorkbenchLayout.class ).getInstance();
        }
        return layout;
    }

}
