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
package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.client.Window;
import org.guvnor.common.services.project.model.Dependency;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;

/**
 * <p>Custom field updater for handling dependency GAV values.</p>
 * <p>If cell's value is invalid, this field updater restores the old value for the cell.</p>
 * <p/>
 * <p>Some handlers are provided:</p>
 * <ul>
 * <li><code>emptyHandler</code>: Handler for empty value (not allowed).</li>
 * <li><code>notValidValueHandler</code>: Handler for not valid value.</li>
 * <li><code>validHandler</code>: Handler for valid value.</li>
 * </ul>
 * <p/>
 */
public abstract class DependencyFieldUpdater
        implements FieldUpdater<Dependency, String> {

    private final WaterMarkEditTextCell cell;
    private final DependencyGridViewImpl.RedrawCommand redrawCommand;

    DependencyFieldUpdater( final WaterMarkEditTextCell cell,
                            final DependencyGridViewImpl.RedrawCommand redrawCommand ) {
        this.cell = cell;
        this.redrawCommand = redrawCommand;
    }

    @Override
    public void update( int index,
                        Dependency dependency,
                        String value ) {
        if ( validate( value ) ) {
            setValue( dependency,
                      value );
        } else {
            cell.clearViewData( dependency );
            redrawCommand.execute();
        }
    }

    private boolean validate( String sValue ) {
        if ( checkIsNotEmpty( sValue ) ) {
            reportEmpty();
            return false;
        } else if ( checkContainsXML( sValue ) ) {
            reportXML();
            return false;
        } else {
            return true;
        }
    }

    protected void reportXML() {
        Window.alert( ProjectEditorResources.CONSTANTS.XMLMarkIsNotAllowed() );
    }

    protected abstract void setValue( final Dependency dep,
                                      final String value );

    protected abstract void reportEmpty();

    boolean checkIsNotEmpty( final String content ) {
        if ( content != null && content.trim().length() > 0 ) {
            return false;
        }

        return true;
    }

    boolean checkContainsXML( final String content ) {
        if ( content != null && (content.contains( "<" ) || content.contains( ">" ) || content.contains( "&" )) ) {
            return true;
        }

        return false;
    }

}
