/*
 * Copyright 2012 JBoss Inc
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
package org.drools.guvnor.client.assets.editors.factmodel;

import java.util.Collection;
import java.util.List;

import org.drools.guvnor.client.i18n.Constants;
import org.drools.guvnor.shared.common.vo.assets.factmodel.AnnotationMetaModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * A line on the Declarative Modelling screen representing a Fact annotation
 */
public class AnnotationEditor extends Composite {

    interface AnnotationEditorBinder
        extends
        UiBinder<Widget, AnnotationEditor> {
    }

    private static AnnotationEditorBinder uiBinder = GWT.create( AnnotationEditorBinder.class );

    @UiField
    Label                                 annotationName;

    @UiField
    Label                                 annotationKey;

    @UiField
    Label                                 annotationValue;

    @UiField
    Image                                 editAnnotationIcon;

    @UiField
    Image                                 deleteAnnotationIcon;

    private AnnotationMetaModel           annotation;
    private List<AnnotationMetaModel>     annotations;

    private Command                       deleteCommand;

    public AnnotationEditor(final AnnotationMetaModel annotation,
                            final List<AnnotationMetaModel> annotations) {

        this.annotation = annotation;
        this.annotations = annotations;

        initWidget( uiBinder.createAndBindUi( this ) );

        annotationName.setStyleName( "guvnor-bold-label" );

        setControlValues( annotation );

        editAnnotationIcon.setTitle( Constants.INSTANCE.Rename() );
        deleteAnnotationIcon.setTitle( Constants.INSTANCE.Delete() );
    }

    //This is a simplified annotation editor and thus we only
    //allow creation of a single key-value pair. The underlying
    //parser implementation allows for multiple key-value pairs
    //for the same annotation but it's implementation is incomplete.
    private void setControlValues(AnnotationMetaModel annotation) {
        String name = annotation.name;
        String key = getFirstEntry( annotation.getValues().keySet() );
        String value = getFirstEntry( annotation.getValues().values() );
        annotationName.setText( "@" + name );
        annotationKey.setText( key );
        annotationValue.setText( value );
    }

    private String getFirstEntry(Collection<String> values) {
        if ( values.isEmpty() ) {
            return "";
        }
        return values.iterator().next();
    }

    @UiHandler("editAnnotationIcon")
    void editAnnotationIconClick(ClickEvent event) {
        final AnnotationEditorPopup popup = new AnnotationEditorPopup( annotation,
                                                                       annotations );
        popup.setOkCommand( new Command() {
            public void execute() {
                setControlValues( annotation );
            }
        } );

        popup.show();
    }

    @UiHandler("deleteAnnotationIcon")
    void deleteAnnotationIconClick(ClickEvent event) {
        deleteCommand.execute();
    }

    public void setDeleteCommand(Command deleteCommand) {
        this.deleteCommand = deleteCommand;
    }
}
