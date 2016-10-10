/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.bs3;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;
import org.uberfire.client.mvp.UberView;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

@Dependent
public class BS3PaletteCategory extends AbstractPalette<DefinitionPaletteCategory> implements IsWidget {

    private static Logger LOGGER = Logger.getLogger( BS3PaletteCategory.class.getName() );
    private static final int VERT_SEPARATOR_H = 20;

    public interface View extends UberView<BS3PaletteCategory> {

        View setWidth( final int px );

        View setHeight( final int px );

        View addTitle( String title );

        View addHeader( String text );

        View addSeparator( final double height );

        View setBackgroundColor( String color );

        View addItem( String id, String text, String description, String glyphDefId, IsWidget view );

        View clear();

    }

    ShapeManager shapeManager;
    DefinitionUtils definitionUtils;
    View view;

    BS3PaletteWidgetImpl bs3PaletteWidget;
    private final List<String> itemIds = new LinkedList<>();

    @Inject
    public BS3PaletteCategory( final ShapeManager shapeManager,
                               final DefinitionUtils definitionUtils,
                               final View view ) {
        this.shapeManager = shapeManager;
        this.definitionUtils = definitionUtils;
        this.definitionUtils = definitionUtils;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public BS3PaletteCategory setWidth( final int px ) {
        view.setWidth( px );
        return this;
    }

    public BS3PaletteCategory setHeight( final int px ) {
        view.setHeight( px );
        return this;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public BS3PaletteCategory setBackgroundColor( final String color ) {
        view.setBackgroundColor( color );
        return this;
    }

    @Override
    protected AbstractPalette<DefinitionPaletteCategory> bind() {
        final DefinitionPaletteCategory category = paletteDefinition;
        boolean doClear = true;
        if ( null != category ) {
            final List<DefinitionPaletteItem> items = category.getItems();
            if ( null != items && !items.isEmpty() ) {
                doClear = false;
                view.addTitle( category.getTitle() );
                addSeparator();
                for ( final DefinitionPaletteItem item : items ) {
                    if ( item instanceof DefinitionPaletteGroup ) {
                        showGroup( ( DefinitionPaletteGroup ) item );

                    } else {
                        showItem( item );

                    }
                    addSeparator();

                }

            }

        }
        if ( doClear ) {
            clear();

        }
        return this;

    }

    public void clear() {
        this.itemIds.clear();
        this.view.clear();

    }

    public View getView() {
        return view;
    }

    private void showGroup( final DefinitionPaletteGroup group ) {
        final String groupTitle = group.getTitle();
        view.addHeader( groupTitle );
        final List<DefinitionPaletteItem> items = group.getItems();
        if ( null != items && !items.isEmpty() ) {
            for ( final DefinitionPaletteItem item : items ) {
                showItem( item );

            }

        }

    }

    private void showItem( final DefinitionPaletteItem item ) {
        final String id = item.getId();
        final String title = item.getTitle();
        final String desc = item.getDescription();
        final String glyphId = item.getDefinitionId();
        view.addItem( id, title, desc, glyphId, bs3PaletteWidget.getDefinitionView( glyphId ) );
        itemIds.add( id );

    }

    private void addSeparator() {
        view.addSeparator( VERT_SEPARATOR_H );
        ;
    }

    @Override
    protected void doDestroy() {
        this.clear();
        this.shapeManager = null;
        this.definitionUtils = null;
        this.view = null;

    }

    @Override
    protected String getPaletteItemId( final int index ) {
        return itemIds.get( index );
    }

    void onMouseDown( final String id,
                      final int mouseX,
                      final int mouseY,
                      final int itemX,
                      final int itemY ) {
        if ( null != itemMouseDownCallback ) {
            itemMouseDownCallback.onItemMouseDown( id, mouseX, mouseY, itemX, itemY );

        }

    }

    void onMouseClick( final String id,
                       final int mouseX,
                       final int mouseY,
                       final int itemX,
                       final int itemY ) {
        if ( null != itemClickCallback ) {
            itemClickCallback.onItemClick( id, mouseX, mouseY, itemX, itemY );

        }

    }

    private int getIndex( final String id ) {
        final DefinitionPaletteCategory category = paletteDefinition;
        if ( null != category ) {
            final List<DefinitionPaletteItem> items = category.getItems();
            if ( null != items && !items.isEmpty() ) {
                int x = 0;
                for ( final DefinitionPaletteItem item : items ) {
                    if ( item.getId().equals( id ) ) {
                        return x;
                    }
                    x++;
                }

            }

        }
        return -1;
    }

}
