// .ui.xml template last modified: 1607097769903
package org.uberfire.client.views.pfly.listbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import org.uberfire.client.workbench.widgets.listbar.ResizeFocusPanel;

public class ListBarWidgetImpl_ListBarWidgetBinderImpl implements UiBinder<org.uberfire.client.workbench.widgets.listbar.ResizeFocusPanel, org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl>, org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl.ListBarWidgetBinder {


  public org.uberfire.client.workbench.widgets.listbar.ResizeFocusPanel createAndBindUi(final org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl owner) {


    return new Widgets(owner).get_container();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl owner;


    public Widgets(final org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl_ListBarWidgetBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl_ListBarWidgetBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl_ListBarWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl_ListBarWidgetBinderImpl_GenBundle) GWT.create(org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl_ListBarWidgetBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for res called 0 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.client.resources.WorkbenchResources get_res() {
      return build_res();
    }
    private org.uberfire.client.resources.WorkbenchResources build_res() {
      // Creation section.
      final org.uberfire.client.resources.WorkbenchResources res = (org.uberfire.client.resources.WorkbenchResources) GWT.create(org.uberfire.client.resources.WorkbenchResources.class);
      // Setup section.

      return res;
    }

    /**
     * Getter for container called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.uberfire.client.workbench.widgets.listbar.ResizeFocusPanel get_container() {
      return build_container();
    }
    private org.uberfire.client.workbench.widgets.listbar.ResizeFocusPanel build_container() {
      // Creation section.
      final org.uberfire.client.workbench.widgets.listbar.ResizeFocusPanel container = (org.uberfire.client.workbench.widgets.listbar.ResizeFocusPanel) GWT.create(org.uberfire.client.workbench.widgets.listbar.ResizeFocusPanel.class);
      // Setup section.
      container.add(get_panel());

      this.owner.container = container;

      return container;
    }

    /**
     * Getter for panel called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Panel get_panel() {
      return build_panel();
    }
    private org.gwtbootstrap3.client.ui.Panel build_panel() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Panel panel = (org.gwtbootstrap3.client.ui.Panel) GWT.create(org.gwtbootstrap3.client.ui.Panel.class);
      // Setup section.
      panel.add(get_header());
      panel.add(get_content());
      panel.addStyleName("uf-listbar-panel");

      this.owner.panel = panel;

      return panel;
    }

    /**
     * Getter for header called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.PanelHeader get_header() {
      return build_header();
    }
    private org.gwtbootstrap3.client.ui.PanelHeader build_header() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.PanelHeader header = (org.gwtbootstrap3.client.ui.PanelHeader) GWT.create(org.gwtbootstrap3.client.ui.PanelHeader.class);
      // Setup section.
      header.add(get_titleDropDown());
      header.add(get_toolbarHeader());
      header.addStyleName("uf-listbar-panel-header");
      header.setVisible(false);

      this.owner.header = header;

      return header;
    }

    /**
     * Getter for titleDropDown called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.uberfire.client.views.pfly.listbar.PartListDropdown get_titleDropDown() {
      return build_titleDropDown();
    }
    private org.uberfire.client.views.pfly.listbar.PartListDropdown build_titleDropDown() {
      // Creation section.
      final org.uberfire.client.views.pfly.listbar.PartListDropdown titleDropDown = (org.uberfire.client.views.pfly.listbar.PartListDropdown) GWT.create(org.uberfire.client.views.pfly.listbar.PartListDropdown.class);
      // Setup section.
      titleDropDown.addStyleName("uf-listbar-panel-header-title");

      this.owner.titleDropDown = titleDropDown;

      return titleDropDown;
    }

    /**
     * Getter for toolbarHeader called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_toolbarHeader() {
      return build_toolbarHeader();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_toolbarHeader() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel toolbarHeader = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      toolbarHeader.add(get_f_ButtonToolBar1());
      toolbarHeader.addStyleName("uf-listbar-panel-header-toolbar");

      return toolbarHeader;
    }

    /**
     * Getter for f_ButtonToolBar1 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.ButtonToolBar get_f_ButtonToolBar1() {
      return build_f_ButtonToolBar1();
    }
    private org.gwtbootstrap3.client.ui.ButtonToolBar build_f_ButtonToolBar1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.ButtonToolBar f_ButtonToolBar1 = (org.gwtbootstrap3.client.ui.ButtonToolBar) GWT.create(org.gwtbootstrap3.client.ui.ButtonToolBar.class);
      // Setup section.
      f_ButtonToolBar1.add(get_contextMenu());
      f_ButtonToolBar1.add(get_toolBar());

      return f_ButtonToolBar1;
    }

    /**
     * Getter for contextMenu called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private org.gwtbootstrap3.client.ui.ButtonGroup get_contextMenu() {
      return build_contextMenu();
    }
    private org.gwtbootstrap3.client.ui.ButtonGroup build_contextMenu() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.ButtonGroup contextMenu = (org.gwtbootstrap3.client.ui.ButtonGroup) GWT.create(org.gwtbootstrap3.client.ui.ButtonGroup.class);
      // Setup section.

      this.owner.contextMenu = contextMenu;

      return contextMenu;
    }

    /**
     * Getter for toolBar called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private org.gwtbootstrap3.client.ui.ButtonGroup get_toolBar() {
      return build_toolBar();
    }
    private org.gwtbootstrap3.client.ui.ButtonGroup build_toolBar() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.ButtonGroup toolBar = (org.gwtbootstrap3.client.ui.ButtonGroup) GWT.create(org.gwtbootstrap3.client.ui.ButtonGroup.class);
      // Setup section.
      toolBar.add(get_maximizeButton());
      toolBar.add(get_closeButton());

      this.owner.toolBar = toolBar;

      return toolBar;
    }

    /**
     * Getter for maximizeButton called 1 times. Type: DEFAULT. Build precedence: 7.
     */
    private org.uberfire.client.views.pfly.maximize.MaximizeToggleButton get_maximizeButton() {
      return build_maximizeButton();
    }
    private org.uberfire.client.views.pfly.maximize.MaximizeToggleButton build_maximizeButton() {
      // Creation section.
      final org.uberfire.client.views.pfly.maximize.MaximizeToggleButton maximizeButton = (org.uberfire.client.views.pfly.maximize.MaximizeToggleButton) GWT.create(org.uberfire.client.views.pfly.maximize.MaximizeToggleButton.class);
      // Setup section.

      this.owner.maximizeButton = maximizeButton;

      return maximizeButton;
    }

    /**
     * Getter for closeButton called 1 times. Type: DEFAULT. Build precedence: 7.
     */
    private org.gwtbootstrap3.client.ui.Button get_closeButton() {
      return build_closeButton();
    }
    private org.gwtbootstrap3.client.ui.Button build_closeButton() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button closeButton = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      closeButton.setSize(org.gwtbootstrap3.client.ui.constants.ButtonSize.SMALL);
      closeButton.setIcon(org.gwtbootstrap3.client.ui.constants.IconType.TIMES);

      this.owner.closeButton = closeButton;

      return closeButton;
    }

    /**
     * Getter for content called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.PanelBody get_content() {
      return build_content();
    }
    private org.gwtbootstrap3.client.ui.PanelBody build_content() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.PanelBody content = (org.gwtbootstrap3.client.ui.PanelBody) GWT.create(org.gwtbootstrap3.client.ui.PanelBody.class);
      // Setup section.

      this.owner.content = content;

      return content;
    }
  }
}
