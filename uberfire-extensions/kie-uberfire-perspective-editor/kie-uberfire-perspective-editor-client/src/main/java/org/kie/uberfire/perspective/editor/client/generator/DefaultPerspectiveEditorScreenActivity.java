package org.kie.uberfire.perspective.editor.client.generator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.Container;
import com.github.gwtbootstrap.client.ui.Row;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfire.perspective.editor.model.ColumnEditor;
import org.kie.uberfire.perspective.editor.model.HTMLEditor;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditor;
import org.kie.uberfire.perspective.editor.model.RowEditor;
import org.kie.uberfire.perspective.editor.model.ScreenEditor;
import org.kie.uberfire.perspective.editor.model.ScreenParameter;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.NamedPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class DefaultPerspectiveEditorScreenActivity implements WorkbenchScreenActivity {

    private PerspectiveEditor editor;

    private final PlaceManager placeManager;

    private PlaceRequest place;

    private static final Collection<String> ROLES = Collections.emptyList();

    private static final Collection<String> TRAITS = Collections.emptyList();

    private Container mainPanel;

    private Map<String, Target> screensToLoad = new HashMap<String, Target>();

    public DefaultPerspectiveEditorScreenActivity( PerspectiveEditor editor,
                                                   final PlaceManager placeManager ) {
        this.placeManager = placeManager;
        build(editor);
    }

    public void build( PerspectiveEditor editor ) {
        this.editor = editor;
        mainPanel = new Container();
        mainPanel.getElement().setId( "mainContainer" );
        List<RowEditor> rows = this.editor.getRows();
        for ( RowEditor rowEditor : rows ) {
            Row row = new Row();
            for ( ColumnEditor columnEditor : rowEditor.getColumnEditors() ) {
                Column column = new Column( new Integer( columnEditor.getSpan() ) );
                // ederign should also get nested rows in columns
                generateScreens( columnEditor, column );
                generateHTML( columnEditor, column );
                row.add( column );
            }
            mainPanel.add( row );
        }
    }

    private void generateHTML( ColumnEditor columnEditor,
                                  Column column ) {
        for ( HTMLEditor htmlEditor : columnEditor.getHtmls() ) {
            HTMLPanel panel = new HTMLPanel(htmlEditor.getHtmlCode());
            column.add( panel );
        }
    }


    private void generateScreens( ColumnEditor columnEditor,
                                  Column column ) {
        for ( ScreenEditor screenEditor : columnEditor.getScreens() ) {
            FlowPanel panel = new FlowPanel();
            panel.getElement().setId( screenEditor.getPlaceName() );
            column.add( panel );
            Map<String, String> parameters  = new HashMap<String, String>(  );
            for ( ScreenParameter screenParameter : screenEditor.getParameters() ) {
                parameters.put( screenParameter.getKey(), screenParameter.getValue() );
            }
            screensToLoad.put( screenEditor.getPlaceName(), new Target( column, panel, parameters  ) );
        }
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
        for ( String key : screensToLoad.keySet() ) {
            final Target target = screensToLoad.get( key );
            final Column column = target.getColumn();
            final FlowPanel panel = target.getPanel();
            final int height = 400;
            panel.setPixelSize( column.getElement().getClientWidth(), height );
            placeManager.goTo( new DefaultPlaceRequest( key, target.getParameters() ), target.getPanel() );
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

        private final Column column;
        private final FlowPanel panel;
        private final Map<String, String> parameters;

        public Target( Column column,
                       FlowPanel panel,
                       Map<String, String> parameters ) {

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
    }
}
