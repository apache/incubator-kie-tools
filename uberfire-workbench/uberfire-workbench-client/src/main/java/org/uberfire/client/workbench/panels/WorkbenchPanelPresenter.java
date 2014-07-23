/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.workbench.panels;

import java.util.Map;

import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Top-level interface for all panel presenters within the UberFire MVP framework.
 */
public interface WorkbenchPanelPresenter {

    public PanelDefinition getDefinition();

    public void setDefinition( final PanelDefinition definition );

    public void addPart( final WorkbenchPartPresenter.View view );

    public void addPart( final WorkbenchPartPresenter.View view,
                         final String contextId );

    public boolean removePart( final PartDefinition part );

    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position );

    /**
     * Removes the given panel presenter and its view from this panel, freeing all resources associated with them.
     * 
     * @param child
     *            The child panel to remove. Must be a direct child of this panel, and must be empty (contain no parts
     *            or child panels). Null is not permitted.
     * @return true if the child was found and removed from this panel; false if the child panel could not be found.
     */
    public boolean removePanel( WorkbenchPanelPresenter child );

    /**
     * Returns the immediate child panels of this panel. Note that panels and parts are not the same thing; this method
     * only returns the panels.
     * 
     * @return an unmodifiable view of the immediate child panels nested within this one. Never null.
     */
    public Map<Position, WorkbenchPanelPresenter> getPanels();

    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration );

    public void setFocus( final boolean hasFocus );

    public boolean selectPart( final PartDefinition part );

    public void maximize();

    public WorkbenchPanelView getPanelView();

    public void onResize( final int width,
                          final int height );

    /**
     * Returns the panel type that should be used when adding child panels of type
     * {@link PanelDefinition#PARENT_CHOOSES_TYPE}.
     * 
     * @return the fully-qualified class name of a WorkbenchPanelPresenter implementation. Returns null if
     *         this panel presenter does not allow child panels.
     */
    public String getDefaultChildType();

}
