package org.uberfire.ext.plugin.client.perspective.editor.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.base.DivWidget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.layout.editor.api.editor.ColumnEditor;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutEditor;
import org.uberfire.ext.layout.editor.api.editor.RowEditor;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.HTMLLayoutDragComponent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.ScreenLayoutDragComponent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.NamedPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class DefaultPerspectiveEditorScreenActivity implements WorkbenchScreenActivity {

    private LayoutEditor editor;

    private final PlaceManager placeManager;

    private PlaceRequest place;

    private static final Collection<String> ROLES = Collections.emptyList();

    private static final Collection<String> TRAITS = Collections.emptyList();

    private FluidContainer mainPanel;

    private List<Target> screensToLoad = new ArrayList<Target>();

    public DefaultPerspectiveEditorScreenActivity( LayoutEditor editor,
                                                   final PlaceManager placeManager ) {
        this.placeManager = placeManager;
        build( editor );
    }

    public void build( LayoutEditor editor ) {
        this.editor = editor;
        this.screensToLoad.clear();
        mainPanel = new FluidContainer();
        mainPanel.getElement().setId( "mainContainer" );
        List<RowEditor> rows = this.editor.getRows();
        extractRows( rows, mainPanel );
    }

    private void extractRows( List<RowEditor> rows,
                              DivWidget parentWidget ) {
        for ( RowEditor rowEditor : rows ) {
            FluidRow row = new FluidRow();
            for ( ColumnEditor columnEditor : rowEditor.getColumnEditors() ) {
                Column column = new Column( new Integer( columnEditor.getSpan() ) );
                if ( columnHasNestedRows( columnEditor ) ) {
                    extractRows( columnEditor.getRows(), column );
                } else {
                    generateComponents( columnEditor, column );
                }
                row.add( column );
            }
            parentWidget.add( row );
        }
    }

    private void generateComponents( ColumnEditor columnEditor,
                                     Column column ) {
        for ( LayoutComponent layoutComponent : columnEditor.getLayoutComponents() ) {
            if ( isAnHTMLComponent( layoutComponent ) ) {
                generateHtmlComponent( column, layoutComponent );
            } else if ( isAndScreenComponent( layoutComponent ) ) {
                generateScreenComponent( column, layoutComponent );
            }
        }
    }

    private void generateScreenComponent( Column column,
                                          LayoutComponent layoutComponent ) {
        Random r = new Random();
        Map<String, String> properties = layoutComponent.getProperties();
        String placeName = properties.get( ScreenLayoutDragComponent.PLACE_NAME_PARAMETER );
        if ( placeName != null ) {
            FlowPanel panel = new FlowPanel();
            panel.getElement().setId( placeName + r.nextInt() );
            column.add( panel );
            screensToLoad.add( new Target( placeName, column, panel, properties ) );
        }
    }

    private void generateHtmlComponent( Column column,
                                        LayoutComponent layoutComponent ) {
        Map<String, String> properties = layoutComponent.getProperties();
        String html = properties.get( HTMLLayoutDragComponent.HTML_CODE_PARAMETER );
        if ( html != null ) {
            HTMLPanel panel = new HTMLPanel( html );
            column.add( panel );
        }
    }

    private boolean isAndScreenComponent( LayoutComponent layoutComponent ) {
        return layoutComponent.isFromMyDragTypeType( ScreenLayoutDragComponent.class );
    }

    private boolean isAnHTMLComponent( LayoutComponent layoutComponent ) {
        return layoutComponent.isFromMyDragTypeType( HTMLLayoutDragComponent.class );
    }

    private boolean columnHasNestedRows( ColumnEditor columnEditor ) {
        return columnEditor.getRows() != null && !columnEditor.getRows().isEmpty();
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        this.place = place;
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public boolean onMayClose() {
        return true;
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public Position getDefaultPosition() {
        return new NamedPosition( "mainContainer" );
    }

    @Override
    public PlaceRequest getOwningPlace() {
        return null;
    }

    @Override
    public void onFocus() {

    }

    @Override
    public void onLostFocus() {

    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }

    @Override
    public IsWidget getWidget() {
        return mainPanel;
    }

    @Override
    public Menus getMenus() {
        return null;
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }

    @Override
    public void onOpen() {
        for ( Target target : screensToLoad ) {
            final Column column = target.getColumn();
            final FlowPanel panel = target.getPanel();
            final int height = 400;
            panel.setPixelSize( column.getElement().getClientWidth(), height );
            placeManager.goTo( new DefaultPlaceRequest( target.getPlaceName(), target.getParameters() ), target.getPanel() );
        }

    }

    @Override
    public String getSignatureId() {
        return getName();
    }

    public String getName() {
        return editor.getName() + screenSufix();
    }

    public static String screenSufix() {
        return "Screen";
    }

    @Override
    public Collection<String> getRoles() {
        return ROLES;
    }

    @Override
    public Collection<String> getTraits() {
        return TRAITS;
    }

    @Override
    public String contextId() {
        return getName();
    }

    @Override
    public Integer preferredHeight() {
        return null;
    }

    @Override
    public Integer preferredWidth() {
        return null;
    }

    private class Target {

        private final String placeName;
        private final Column column;
        private final FlowPanel panel;
        private final Map<String, String> parameters;

        public Target( String placeName,
                       Column column,
                       FlowPanel panel,
                       Map<String, String> parameters ) {
            this.placeName = placeName;
            this.column = column;
            this.panel = panel;
            this.parameters = parameters;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public FlowPanel getPanel() {
            return panel;
        }

        public Column getColumn() {
            return column;
        }

        public String getPlaceName() {
            return placeName;
        }
    }
}
