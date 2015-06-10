/*
 * Copyright 2015 JBoss Inc
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

@ApplicationScoped
public class DomainEditorRegistry {

    @Inject
    private SyncBeanManager iocBeanManager;

    private List<IOCBeanDef<DomainEditor>> domainEditors = new ArrayList<IOCBeanDef<DomainEditor>>(  );

    @PostConstruct
    public void setup() {
        final Collection<IOCBeanDef<DomainEditor>> _domainEditors = iocBeanManager.lookupBeans( DomainEditor.class );
        if ( _domainEditors != null && _domainEditors.size() > 0 ) {
            domainEditors.addAll( _domainEditors );
        }
    }

    public List<DomainEditor> getNewDomainEditorInstances() {
        List<DomainEditor> sortedEditors = new ArrayList<DomainEditor>(  );
        for ( IOCBeanDef<DomainEditor> editorDef : domainEditors ) {
            sortedEditors.add( editorDef.newInstance() );
        }
        Collections.sort( sortedEditors, new Comparator<DomainEditor>() {
            @Override
            public int compare( DomainEditor o1, DomainEditor o2 ) {
                Integer key1 = o1.getPriority();
                Integer key2 = o2.getPriority();
                return key1.compareTo( key2 );
            }
        } );
        return sortedEditors;
    }
}
