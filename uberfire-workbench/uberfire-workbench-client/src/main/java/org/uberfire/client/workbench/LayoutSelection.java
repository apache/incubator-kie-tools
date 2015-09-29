/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
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

        Collection<SyncBeanDef<WorkbenchLayout>> beanDefs = iocManager.lookupBeans( WorkbenchLayout.class, altLayout );
        if ( beanDefs.size() > 0 ) {
            SyncBeanDef<WorkbenchLayout> alt = beanDefs.iterator().next();
            layout = alt.getInstance();
        } else {
            layout = iocManager.lookupBean( WorkbenchLayout.class ).getInstance();
        }
        return layout;
    }

}
