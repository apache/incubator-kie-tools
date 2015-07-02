package org.uberfire.ext.layout.editor.client;

import java.util.Map;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.LayoutComponentView;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.ComponentEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.RowEditorWidget;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class LayoutTemplateViewTest {

    LayoutEditorWidget layoutEditorWidget;
    LayoutEditorView view;

    @Before
    public void setup() {
        layoutEditorWidget = new LayoutEditorWidget();
        view = new LayoutEditorView( layoutEditorWidget );
        LayoutEditorPresenter presenter = new LayoutEditorPresenter( view );
        view.init( presenter );
    }

    @Test
    public void createAndExtractDefaultModel() throws Exception {

        view.loadDefaultLayout( "layout" );
        LayoutTemplate model = view.getModel();
        assertEquals( LayoutTemplate.defaultLayout( "layout" ), model );

        view.setupContent( LayoutTemplate.defaultLayout( "layout" ) );
        model = view.getModel();
        assertEquals( LayoutTemplate.defaultLayout( "layout" ), model );
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
        view.setupContent( LayoutTemplate.defaultLayout( "layout" ) );

        RowEditorWidget firstDefaultRow = (RowEditorWidget) layoutEditorWidget.getRowEditors().get( 0 );
        ColumnEditorWidget firstDefaultColumn = (ColumnEditorWidget) firstDefaultRow.getColumnEditors().get( 0 );

        ComponentEditorWidget editorWidget = new ComponentEditorWidget( firstDefaultColumn, new FlowPanel(), mock( LayoutDragComponent.class ) );
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
        view.setupContent( LayoutTemplate.defaultLayout( "layout" ) );

        RowEditorWidget firstDefaultRow = (RowEditorWidget) layoutEditorWidget.getRowEditors().get( 0 );
        ColumnEditorWidget firstDefaultColumn = (ColumnEditorWidget) firstDefaultRow.getColumnEditors().get( 0 );

        ComponentEditorWidget editorWidget = new ComponentEditorWidget( firstDefaultColumn, new FlowPanel(), mock( LayoutDragComponent.class ) );
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

        LayoutTemplate layout = LayoutTemplate.defaultLayout( "layout" );
        LayoutRow row = layout.getRows().get( 0 );
        LayoutColumn layoutColumn = row.getLayoutColumns().get( 0 );
        LayoutComponent layoutComponent = new LayoutComponent( LayoutDragComponent.class );
        layoutComponent.addProperty( "key", "value" );

        layoutColumn.addLayoutComponent( layoutComponent );

        view.setupContent( layout );

        LayoutComponent layoutComponentExtracted = extractFirstLayoutComponent( view );

        assertEquals( "value", layoutComponentExtracted.getProperties().get( "key" ) );
    }

    private LayoutComponent extractFirstLayoutComponent( LayoutEditorView view ) {
        LayoutTemplate model = view.getModel();
        LayoutRow firstRow = model.getRows().get( 0 );
        LayoutColumn firstColumn = firstRow.getLayoutColumns().get( 0 );
        return firstColumn.getLayoutComponents().get( 0 );
    }

    private LayoutEditorView createViewMock( final LayoutDragComponent layoutDragComponent ) {
        return new LayoutEditorView( layoutEditorWidget ) {
            @Override
            RowView createRowView( LayoutRow row ) {
                RowView rowView = new RowView( layoutEditorWidget, row ) {
                    @Override
                    public LayoutDragComponent getLayoutDragComponent( LayoutComponent layoutComponent ) {
                        return layoutDragComponent;
                    }

                    @Override
                    protected LayoutComponentView createLayoutComponentView( ColumnEditorWidget parent,
                                                                             LayoutComponent layoutComponent,
                                                                             LayoutDragComponent layoutDragComponent ) {
                        return new LayoutComponentView( parent, layoutComponent, layoutDragComponent ) {
                            @Override
                            protected LayoutEditorWidget getLayoutEditorWidget() {
                                return layoutEditorWidget;
                            }
                        };
                    }
                };
                return rowView;
            }
        };
    }

}