package org.uberfire.client.workbench;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * @author Heiko Braun
 * @date 06/06/14
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

    // select workbench layout
    public WorkbenchLayout get() {

        WorkbenchLayout layout = null;

        Collection<IOCBeanDef<WorkbenchLayout>> beanDefs = iocManager.lookupBeans(WorkbenchLayout.class, altLayout);
        if(beanDefs.size()>0)
        {
            IOCBeanDef<WorkbenchLayout> alt = beanDefs.iterator().next();
            System.out.println("Using alternative workbench layout: "+alt.getBeanClass().getName());
            layout = alt.getInstance();
        }
        else
        {
            layout = iocManager.lookupBean(WorkbenchLayoutImpl.class).getInstance();
        }

        return layout;
    }

}
