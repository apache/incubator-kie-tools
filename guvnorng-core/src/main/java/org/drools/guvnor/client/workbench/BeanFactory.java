/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.workbench;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.Widget;

/**
 * A convenience class to create new instances of managed beans.
 */
@ApplicationScoped
public class BeanFactory {

    @Inject
    private IOCBeanManager iocManager;

    private Set<Object>    beans = new HashSet<Object>();

    public WorkbenchPart newWorkbenchPart(final Widget w,
                                          final String title) {
        final WorkbenchPart part = (WorkbenchPart) iocManager.lookupBean( WorkbenchPart.class ).getInstance();
        part.setPartWidget( w );
        part.setPartTitle( title );
        beans.add( part );
        return part;
    }

    //TODO {manstis} We don't release any objects we create
    public void release(final Object o) {
        iocManager.destroyBean( o );
    }

}
