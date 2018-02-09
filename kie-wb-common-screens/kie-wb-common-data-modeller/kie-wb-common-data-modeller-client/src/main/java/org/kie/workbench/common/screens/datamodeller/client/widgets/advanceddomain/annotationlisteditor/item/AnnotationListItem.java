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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationlisteditor.AdvancedAnnotationListEditorView;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.driver.model.AnnotationSource;

@Dependent
public class AnnotationListItem
        implements AnnotationListItemView.Presenter, IsWidget {

    private AnnotationListItemView view;

    private Annotation annotation;

    private AdvancedAnnotationListEditorView.DeleteAnnotationHandler deleteAnnotationHandler;

    private AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler;

    private AdvancedAnnotationListEditorView.EditValuePairHandler editValuePairHandler;

    private AdvancedAnnotationListEditorView.CollapseChangeHandler collapseChangeHandler;

    private List<AnnotationValuePairListItem> items = new ArrayList<AnnotationValuePairListItem>( );

    private Instance<AnnotationValuePairListItem> itemInstance;

    public AnnotationListItem( ) {
    }

    @Inject
    public AnnotationListItem( AnnotationListItemView view, Instance<AnnotationValuePairListItem> itemInstance ) {
        this.view = view;
        view.init( this );
        this.itemInstance = itemInstance;
    }

    @Override
    public Widget asWidget( ) {
        return view.asWidget( );
    }

    public void loadAnnotation( Annotation annotation, AnnotationSource annotationSource ) {
        this.annotation = annotation;
        view.setHeadingTitle( annotation.getClassName( ) );

        if ( annotation.getAnnotationDefinition( ) != null &&
                annotation.getAnnotationDefinition( ).getValuePairs( ) != null ) {
            for ( AnnotationValuePairDefinition valuePairDefinition : annotation.getAnnotationDefinition( ).getValuePairs( ) ) {
                AnnotationValuePairListItem valuePairListItem = createListItem();
                valuePairListItem.loadValuePair( annotation, valuePairDefinition, annotationSource );
                valuePairListItem.setClearValuePairHandler( new AdvancedAnnotationListEditorView.ClearValuePairHandler( ) {
                    @Override
                    public void onClearValuePair( Annotation annotation, String valuePair ) {
                        if ( clearValuePairHandler != null ) {
                            clearValuePairHandler.onClearValuePair( annotation, valuePair );
                        }
                    }
                } );
                valuePairListItem.setEditValuePairHandler( new AdvancedAnnotationListEditorView.EditValuePairHandler( ) {
                    @Override
                    public void onEditValuePair( Annotation annotation, String valuePair ) {
                        if ( editValuePairHandler != null ) {
                            editValuePairHandler.onEditValuePair( annotation, valuePair );
                        }
                    }
                } );
                view.addItem( valuePairListItem );
                items.add( valuePairListItem );
            }
        }
    }

    public void setDeleteAnnotationHandler( AdvancedAnnotationListEditorView.DeleteAnnotationHandler deleteAnnotationHandler ) {
        this.deleteAnnotationHandler = deleteAnnotationHandler;
    }

    public void setClearValuePairHandler( AdvancedAnnotationListEditorView.ClearValuePairHandler clearValuePairHandler ) {
        this.clearValuePairHandler = clearValuePairHandler;
    }

    public void setEditValuePairHandler( AdvancedAnnotationListEditorView.EditValuePairHandler editValuePairHandler ) {
        this.editValuePairHandler = editValuePairHandler;
    }

    public void setCollapseChangeHandler( AdvancedAnnotationListEditorView.CollapseChangeHandler collapseChangeHandler ) {
        this.collapseChangeHandler = collapseChangeHandler;
    }

    public boolean isCollapsed( ) {
        return view.isCollapsed( );
    }

    public void setCollapsed( boolean collapsed ) {
        view.setCollapsed( collapsed );
    }

    public void setReadonly( boolean readonly ) {
        view.setReadonly( readonly );
        for ( AnnotationValuePairListItem item : items ) {
            item.setReadonly( readonly );
        }
    }

    @Override
    public void onDelete( ) {
        if ( deleteAnnotationHandler != null ) {
            deleteAnnotationHandler.onDeleteAnnotation( annotation );
        }
    }

    @Override
    public void onCollapseChange( ) {
        if ( collapseChangeHandler != null ) {
            collapseChangeHandler.onCollapseChange( );
        }
    }

    @PreDestroy
    protected void destroy() {
        for ( AnnotationValuePairListItem item : items ) {
            dispose( item );
        }
        items.clear();
    }

    protected AnnotationValuePairListItem createListItem() {
        return itemInstance.get();
    }

    protected void dispose( AnnotationValuePairListItem listItem ) {
        itemInstance.destroy( listItem );
    }
}