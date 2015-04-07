package org.uberfire.ext.layout.editor.client;

import java.util.Map;

import com.github.gwtbootstrap.client.ui.config.ColumnSizeConfigurator;
import com.github.gwtbootstrap.client.ui.config.DefaultColumnSizeConfigurator;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.fakes.FakeProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.api.editor.ColumnEditor;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;
import org.uberfire.ext.layout.editor.api.editor.RowEditor;
import org.uberfire.ext.layout.editor.client.components.LayoutComponentView;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorUI;
import org.uberfire.ext.layout.editor.client.structure.LayoutComponentWidgetUI;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorUI;
import org.uberfire.ext.layout.editor.client.structure.RowEditorWidgetUI;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class LayoutEditorViewTest {

    LayoutEditorUI layoutEditorUI;
    LayoutEditorView view;

    @Before
    public void setup() {
        //Bootstrap Column need this hack (it doesn' allow GWT.CREATE (no default constructor)
        // and need's to register correct column size provider configurator (instead of GWT Mockito MOCK)
        GwtMockito.useProviderForType( ColumnSizeConfigurator.class, new FakeProvider() {
            @Override
            public Object getFake( Class aClass ) {
                return new DefaultColumnSizeConfigurator();
            }
        } );

        layoutEditorUI = new LayoutEditorUI();
        view = new LayoutEditorView( layoutEditorUI );
        LayoutEditorPresenter presenter = new LayoutEditorPresenter( view );
        view.init( presenter );
    }

    @Test
    public void createAndExtractDefaultModel() throws Exception {

        view.loadDefaultContent( "layout" );
        LayoutEditor model = view.getModel();
        assertEquals( LayoutEditor.defaultContent( "layout" ), model );

        view.setupContent( LayoutEditor.defaultContent( "layout" ) );
        model = view.getModel();
        assertEquals( LayoutEditor.defaultContent( "layout" ), model );
    }

    @Test
    public void addAndGetLayoutProperty() throws Exception {
        view.addLayoutProperty( "key", "value" );
        String value = view.getLayoutProperty( "key" );
        assertEquals( "value", value );

        Map<String, String> layoutProperties = view.getModel().getLayoutProperties();
        value = layoutProperties.get( "key" );
        assertEquals( "value", value );
    }

    @Test
    public void addAndGetLayoutComponentProperty() throws Exception {
        view.setupContent( LayoutEditor.defaultContent( "layout" ) );

        RowEditorWidgetUI firstDefaultRow = (RowEditorWidgetUI) layoutEditorUI.getRowEditors().get( 0 );
        ColumnEditorUI firstDefaultColumn = (ColumnEditorUI) firstDefaultRow.getColumnEditors().get( 0 );

        LayoutComponentWidgetUI editorWidget = new LayoutComponentWidgetUI( firstDefaultColumn, new FlowPanel(), mock( LayoutDragComponent.class ) );
        firstDefaultColumn.addChild( editorWidget );

        view.addComponentProperty( editorWidget, "key", "value" );
        assertEquals( "value", view.getLayoutComponentProperties( editorWidget ).get( "key" ) );

        view.resetLayoutComponentProperties( editorWidget );
        assertEquals( null, view.getLayoutComponentProperties( editorWidget ).get( "key" ) );

        view.addComponentProperty( editorWidget, "key", "value" );
        assertEquals( "value", view.getLayoutComponentProperties( editorWidget ).get( "key" ) );

        view.removeLayoutComponentProperty( editorWidget, "key" );
        assertEquals( null, view.getLayoutComponentProperties( editorWidget ).get( "key" ) );

    }

    @Test
    public void propertyShouldBeOnLayoutModelAndOnLayoutUI() throws Exception {
        view.setupContent( LayoutEditor.defaultContent( "layout" ) );

        RowEditorWidgetUI firstDefaultRow = (RowEditorWidgetUI) layoutEditorUI.getRowEditors().get( 0 );
        ColumnEditorUI firstDefaultColumn = (ColumnEditorUI) firstDefaultRow.getColumnEditors().get( 0 );

        LayoutComponentWidgetUI editorWidget = new LayoutComponentWidgetUI( firstDefaultColumn, new FlowPanel(), mock( LayoutDragComponent.class ) );
        firstDefaultColumn.addChild( editorWidget );

        view.addComponentProperty( editorWidget, "key", "value" );

        LayoutComponent firstComponent = extractFirstLayoutComponent( view );

        Map<String, String> properties = firstComponent.getProperties();
        assertEquals( "value", properties.get( "key" ) );
    }

    @Test
    public void loadAnLayoutEditor() {
        final LayoutDragComponent layoutDragComponent = mock( LayoutDragComponent.class );

        view = createViewMock( layoutDragComponent );

        LayoutEditorPresenter presenter = new LayoutEditorPresenter( view );
        view.init( presenter );

        LayoutEditor layout = LayoutEditor.defaultContent( "layout" );
        RowEditor row = layout.getRows().get( 0 );
        ColumnEditor columnEditor = row.getColumnEditors().get( 0 );
        LayoutComponent layoutComponent = new LayoutComponent( LayoutDragComponent.class );
        layoutComponent.addProperty( "key", "value" );

        columnEditor.addLayoutComponent( layoutComponent );

        view.setupContent( layout );

        LayoutComponent layoutComponentExtracted = extractFirstLayoutComponent( view );

        assertEquals( "value", layoutComponentExtracted.getProperties().get( "key" ) );
    }

    private LayoutComponent extractFirstLayoutComponent( LayoutEditorView view ) {
        LayoutEditor model = view.getModel();
        RowEditor firstRow = model.getRows().get( 0 );
        ColumnEditor firstColumn = firstRow.getColumnEditors().get( 0 );
        return firstColumn.getLayoutComponents().get( 0 );
    }

    private LayoutEditorView createViewMock( final LayoutDragComponent layoutDragComponent ) {
        return new LayoutEditorView( layoutEditorUI ) {
            @Override
            RowView createRowView( RowEditor row ) {
                RowView rowView = new RowView( layoutEditorUI, row ) {
                    @Override
                    public LayoutDragComponent getLayoutDragComponent( LayoutComponent layoutComponent ) {
                        return layoutDragComponent;
                    }

                    @Override
                    protected LayoutComponentView createLayoutComponentView( ColumnEditorUI parent,
                                                                             LayoutComponent layoutComponent,
                                                                             LayoutDragComponent layoutDragComponent ) {
                        return new LayoutComponentView( parent, layoutComponent, layoutDragComponent ) {
                            @Override
                            protected LayoutEditorUI getLayoutEditor() {
                                return layoutEditorUI;
                            }
                        };
                    }
                };
                return rowView;
            }
        };
    }

}