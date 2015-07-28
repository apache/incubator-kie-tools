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
package org.kie.workbench.common.widgets.client.handlers.workbench.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuFactory.CustomMenuBuilder;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

@ApplicationScoped
public class WorkbenchConfigurationMenu {

    @Inject
    private SyncBeanManager iocBeanManager;

    @Inject
    private WorkbenchConfigurationPresenter newResourcePresenter;

    private final List<MenuItem> items = new ArrayList<MenuItem>();
    private final Map<WorkbenchConfigurationHandler, MenuItem> workbenchConfigurationHandler = new HashMap<WorkbenchConfigurationHandler, MenuItem>();

    @PostConstruct
    public void setup() {
        final Collection<IOCBeanDef<WorkbenchConfigurationHandler>> handlerBeans = iocBeanManager.lookupBeans( WorkbenchConfigurationHandler.class );
        if ( handlerBeans.size() > 0 ) {
            for ( IOCBeanDef<WorkbenchConfigurationHandler> handlerBean : handlerBeans ) {
                final WorkbenchConfigurationHandler activeHandler = handlerBean.getInstance();

                final String description = activeHandler.getDescription();
                final MenuItem menuItem = MenuFactory.newSimpleItem( description ).respondsWith( new Command() {

                    @Override
                    public void execute() {
                        newResourcePresenter.show( activeHandler );
                    }
                } ).endMenu().build().getItems().get( 0 );
                workbenchConfigurationHandler.put( activeHandler, menuItem );
                items.add( menuItem );
            }
        }

        Collections.sort( items,
                          new Comparator<MenuItem>() {

                              @Override
                              public int compare( final MenuItem o1,
                                                  final MenuItem o2 ) {
                                  return o1.getCaption().compareToIgnoreCase( o2.getCaption() );
                              }
                          } );

    }

    public List<MenuItem> getMenuItems() {
        return items;
    }

    public MenuItem getToplevelMenu() {
        return MenuFactory.newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {

            @Override
            public void push( CustomMenuBuilder element ) {

            }

            @Override
            public MenuItem build() {
                return new BaseMenuCustom<IsWidget>() {

                    @Override
                    public IsWidget build() {
                        AnchorListItem link = new AnchorListItem();
                        link.setIcon( IconType.COG );
                        return link;
                    }

                    @Override
                    public MenuPosition getPosition() {
                        return MenuPosition.RIGHT;
                    }
                };
            }

        } ).endMenu().build().getItems().get( 0 );
    }
}
