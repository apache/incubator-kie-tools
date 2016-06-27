/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.client.infra;

import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.*;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LayoutDragComponentHelper {

    private DndDataJSONConverter converter = new DndDataJSONConverter();
    private List<Object> instances = new ArrayList<>();

    @PreDestroy
    public void destroy(){
        for ( Object instance : instances ) {
            destroy( instance );
        }
    }

    public LayoutDragComponent lookupDragTypeBean( String dragTypeClassName ) {
        return lookupBean( dragTypeClassName );
    }

    private LayoutDragComponent lookupBean( String dragTypeClassName ) {
        SyncBeanManagerImpl beanManager = ( SyncBeanManagerImpl ) IOC.getBeanManager();
        Collection<SyncBeanDef<LayoutDragComponent>> iocBeanDefs = beanManager.lookupBeans( LayoutDragComponent.class );
        for ( SyncBeanDef<LayoutDragComponent> iocBeanDef : iocBeanDefs ) {
            LayoutDragComponent instance = iocBeanDef.getInstance();
            instances.add( instance );
            if ( getRealBeanClass( instance ).equalsIgnoreCase( dragTypeClassName ) ) {
                return instance;
            }
        }
        return null;
    }

    public String getRealBeanClass( LayoutDragComponent instance ) {
        return Factory.maybeUnwrapProxy( instance ).getClass().getName();
    }

    public LayoutComponent getLayoutComponentFromDrop( DropEvent dropEvent ) {
        LayoutDragComponent component = extractComponent( dropEvent );
        LayoutComponent layoutComponent = getLayoutComponent( component );
        return layoutComponent;
    }

    public LayoutComponent getLayoutComponent( LayoutDragComponent dragComponent ) {

        LayoutComponent layoutComponent = new LayoutComponent( getRealBeanClass( dragComponent ) );

        if ( dragComponent instanceof HasDragAndDropSettings ) {
            Map<String, String> properties = ( ( HasDragAndDropSettings ) dragComponent ).getMapSettings();

            if ( properties != null ) {
                layoutComponent.addProperties( properties );
            }
        }

        return layoutComponent;
    }

    private LayoutDragComponent extractComponent( DropEvent dropEvent ) {
        return converter
                .readJSONDragComponent( dropEvent.getData( LayoutDragComponent.FORMAT ) );
    }



    private boolean hasComponent( LayoutComponent component ) {
        return component != null;
    }

    protected void destroy( Object o ) {
        BeanHelper.destroy( o );
    }

}
