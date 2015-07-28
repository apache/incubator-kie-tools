/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.widget;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;

@Dependent
public class TagWidgetViewImpl
        extends Composite implements TagWidgetView {

    private TagWidget presenter;

    @UiField
    HorizontalPanel tags;

    @UiField
    TextBox newTags;

    @UiField
    Button addTags;

    interface TagWidgetBinder
            extends
            UiBinder<Widget, TagWidgetViewImpl> {

    }

    private static TagWidgetBinder uiBinder = GWT.create( TagWidgetBinder.class );

    public TagWidgetViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setPresenter( TagWidget presenter ) {
        this.presenter = presenter;
    }

    @UiHandler("addTags")
    public void addNewTags( ClickEvent event ) {
        presenter.onAddTags( newTags.getText() );
        newTags.setText( "" );
    }

    @Override
    public void removeTag( String tag ) {
        presenter.onRemoveTag( tag );
    }

    @Override
    public void clear() {
        tags.clear();
        newTags.setValue( "" );
    }

    @Override
    public void setReadOnly( boolean readOnly ) {
        newTags.setVisible( !readOnly );
        addTags.setVisible( !readOnly );
    }

    /**
     * Appy the change (selected tag to be added).
     */
    @Override
    public void addTag( final String tag,
                        final boolean readOnly ) {
        if ( tag.isEmpty() ) {
            return;
        }
        tags.add( new TagButton( tag, readOnly, new ClickHandler() {
            public void onClick( final ClickEvent event ) {
                if ( readOnly ) {
                    return;
                }
                removeTag( tag );
            }
        } ) );
    }
}
