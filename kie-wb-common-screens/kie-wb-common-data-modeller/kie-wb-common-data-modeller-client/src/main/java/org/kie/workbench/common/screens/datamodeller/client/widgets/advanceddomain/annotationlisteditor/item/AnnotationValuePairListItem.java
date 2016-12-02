/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.item;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.AdvancedAnnotationListEditorView;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource;

@Dependent
public class AnnotationValuePairListItem
        implements AnnotationValuePairListItemView.Presenter, IsWidget {

    private AnnotationValuePairListItemView view;

    private AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler;

    private AdvancedAnnotationListEditorView.EditValuePairHandler editValuePairHandler;

    private Annotation annotation;

    private AnnotationValuePairDefinition valuePairDefinition;

    public AnnotationValuePairListItem( ) {
    }

    @Inject
    public AnnotationValuePairListItem( AnnotationValuePairListItemView view ) {
        this.view = view;
        view.init( this );
    }

    @Override
    public Widget asWidget( ) {
        return view.asWidget( );
    }

    public void loadValuePair( final Annotation annotation,
                               final AnnotationValuePairDefinition valuePairDefinition,
                               final AnnotationSource annotationSource ) {
        this.annotation = annotation;
        this.valuePairDefinition = valuePairDefinition;
        view.setValuePairName( valuePairDefinition.getName( ) );
        view.setValuePairStringValue( getValuePairStringValue( annotation, valuePairDefinition, annotationSource ) );
        if ( valuePairDefinition.getDefaultValue( ) == null ) {
            view.showRequiredIndicator( true );
        }
    }

    public void setReadonly( boolean readonly ) {
        view.setReadonly( readonly );
    }

    public void setClearValuePairHandler( AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler ) {
        this.clearValuePairHandler = clearValuePairHandler;
    }

    public void setEditValuePairHandler( AdvancedAnnotationListEditorView.EditValuePairHandler editValuePairHandler ) {
        this.editValuePairHandler = editValuePairHandler;
    }

    @Override
    public void onEdit( ) {
        if ( editValuePairHandler != null ) {
            editValuePairHandler.onEditValuePair( annotation, valuePairDefinition.getName( ) );
        }
    }

    @Override
    public void onClear( ) {
        if ( clearValuePairHandler != null ) {
            clearValuePairHandler.onClearValuePair( annotation, valuePairDefinition.getName( ) );
        }
    }

    private String getValuePairStringValue( Annotation annotation,
                                            AnnotationValuePairDefinition valuePairDefinition,
                                            AnnotationSource annotationSource ) {

        Object value = annotation.getValue( valuePairDefinition.getName( ) );
        String strValue;

        if ( value == null ) {
            strValue = Constants.INSTANCE.advanced_domain_annotation_list_editor_message_value_not_set( );
        } else {
            strValue = annotationSource != null ? annotationSource.getValuePairSource( valuePairDefinition.getName( ) ) : null;
            if ( strValue == null ) {
                strValue = Constants.INSTANCE.advanced_domain_annotation_list_editor_message_source_code_not_available( );
            }
        }

        return strValue;
    }
}