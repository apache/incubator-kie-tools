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
package org.uberfire.client.workbench;

import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.client.workbench.widgets.panels.HorizontalSplitterPanel;
import org.uberfire.client.workbench.widgets.panels.VerticalSplitterPanel;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * A Factory definition to create new instances of managed beans.
 */
public interface BeanFactory {

    public WorkbenchPartPresenter newWorkbenchPart(final IsWidget titleWidget,
                                                   final PartDefinition definition);

    public WorkbenchPanelPresenter newWorkbenchPanel(final PanelDefinition definition);

    public HorizontalSplitterPanel newHorizontalSplitterPanel(final WorkbenchPanelPresenter.View eastPanel,
                                                              final WorkbenchPanelPresenter.View westPanel,
                                                              final Position position,
                                                              final Integer preferredSize,
                                                              final Integer preferredMinSize);

    public VerticalSplitterPanel newVerticalSplitterPanel(final WorkbenchPanelPresenter.View northPanel,
                                                          final WorkbenchPanelPresenter.View southPanel,
                                                          final Position position,
                                                          final Integer preferredSize,
                                                          final Integer preferredMinSize);

    public CompassDropController newDropController(final WorkbenchPanelPresenter.View view);

    public void destroy(final Object o);

}
