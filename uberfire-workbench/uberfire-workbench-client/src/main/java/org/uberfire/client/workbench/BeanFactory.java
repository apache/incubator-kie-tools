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

import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * A Factory definition to create new instances of managed beans.
 */
public interface BeanFactory {

    /**
     * Creates a new part presenter/view pair with the given properties.
     *
     * @param menus
     *            The menus to associate with the new part. Null means no menus.
     * @param title
     *            The title to associate with the new part that the containing panel may display (for example, as the
     *            tab label in a {@link MultiTabWorkbenchPanelPresenter}.
     * @param titleDecoration
     *            The title decoration that a panel may choose to display beside the part's title. Null is permitted,
     *            and means no title decoration.
     *            <p>
     *            NOTE: presently, none of the built-in panel types display a part's title decoration.
     * @param definition
     *            Defines all other aspects of the part to create. Must not be null.
     * @return
     */
    public WorkbenchPartPresenter newWorkbenchPart( final Menus menus,
                                                    final String title,
                                                    final IsWidget titleDecoration,
                                                    final PartDefinition definition );

    /**
     * Creates a new perspective root panel for the given Perspective Activity and Root Panel Definition. The returned
     * object must be destroyed by a call to {@link #destroy(Object)} when it is no longer needed.
     *
     * @param activity
     *            the perspective that the root panel is being created for. Must not be null.
     * @param root
     *            description of the panel to create. Must not be null.
     * @return a new WorkbenchPanelPresenter configured as specified in the given panel definition. This bean must be
     *         passed to {@link #destroy(Object)} when no longer in use by the application.
     */
    public WorkbenchPanelPresenter newRootPanel( PerspectiveActivity activity,
                                                 PanelDefinition root );

    /**
     * Creates a new panel with the properties specified in the given definition.
     *
     * @param definition
     *            specification of the panel that should be created. Must not be null.
     * @return a new panel presenter/view pair that is ready for use in the workbench. This bean must be passed to
     *         {@link #destroy(Object)} when no longer in use by the application.
     */
    public WorkbenchPanelPresenter newWorkbenchPanel( final PanelDefinition definition );

    public CompassDropController newDropController( final WorkbenchPanelView<?> view );

    /**
     * Destroys the entire graph of beans that were created and returned via a call to any of the <tt>newXXX()</tt>
     * methods in this class. For example, passing a {@link WorkbenchPartPresenter} instance in will result in the
     * destruction of that presenter, its view, and all other dependent beans injected into that graph of objects.
     *
     * @param o
     *            a bean which was returned from one of the <tt>newXXX()</tt> methods in this class and which has not
     *            been destroyed yet.
     */
    public void destroy( final Object o );

}
