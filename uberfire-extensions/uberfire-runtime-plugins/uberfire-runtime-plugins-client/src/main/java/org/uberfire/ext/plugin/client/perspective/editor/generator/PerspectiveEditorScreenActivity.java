package org.uberfire.ext.plugin.client.perspective.editor.generator;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.NamedPosition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

import java.util.Collection;
import java.util.Collections;

public class PerspectiveEditorScreenActivity implements WorkbenchScreenActivity {

    private LayoutTemplate layoutTemplate;

    private final LayoutGenerator layoutGenerator;

    private PlaceRequest place;

    private static final Collection<String> ROLES = Collections.emptyList();

    private static final Collection<String> TRAITS = Collections.emptyList();

    private Panel mainPanel = new FlowPanel();

    public PerspectiveEditorScreenActivity(LayoutTemplate layoutTemplate,
                                           final LayoutGenerator layoutGenerator) {
        this.layoutTemplate = layoutTemplate;
        this.layoutGenerator = layoutGenerator;
    }

    public LayoutTemplate getLayoutTemplate() {
        return layoutTemplate;
    }

    public void setLayoutTemplate(LayoutTemplate layoutTemplate) {
        this.layoutTemplate = layoutTemplate;
    }

    @Override
    public void onStartup(PlaceRequest place) {
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
        return new NamedPosition("mainContainer");
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
        mainPanel.clear();
        mainPanel.add(layoutGenerator.build(layoutTemplate));
    }

    @Override
    public String getSignatureId() {
        return getName();
    }

    public String getName() {
        return layoutTemplate.getName() + screenSufix();
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
}
