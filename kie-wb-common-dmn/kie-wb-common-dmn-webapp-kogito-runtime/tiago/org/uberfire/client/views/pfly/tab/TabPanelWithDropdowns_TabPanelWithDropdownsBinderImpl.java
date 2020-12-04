// .ui.xml template last modified: 1607096090194
package org.uberfire.client.views.pfly.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import org.gwtbootstrap3.client.ui.TabPanel;

public class TabPanelWithDropdowns_TabPanelWithDropdownsBinderImpl implements UiBinder<org.gwtbootstrap3.client.ui.TabPanel, org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns>, org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns.TabPanelWithDropdownsBinder {


  public org.gwtbootstrap3.client.ui.TabPanel createAndBindUi(final org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns owner) {


    return new Widgets(owner).get_f_TabPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns owner;


    public Widgets(final org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns owner) {
      this.owner = owner;
      build_tabBar();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_widgetsPanel();  // more than one getter call detected. Type: DEFAULT, precedence: 3
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns_TabPanelWithDropdownsBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns_TabPanelWithDropdownsBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns_TabPanelWithDropdownsBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns_TabPanelWithDropdownsBinderImpl_GenBundle) GWT.create(org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns_TabPanelWithDropdownsBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for f_TabPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.TabPanel get_f_TabPanel1() {
      return build_f_TabPanel1();
    }
    private org.gwtbootstrap3.client.ui.TabPanel build_f_TabPanel1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TabPanel f_TabPanel1 = (org.gwtbootstrap3.client.ui.TabPanel) GWT.create(org.gwtbootstrap3.client.ui.TabPanel.class);
      // Setup section.
      f_TabPanel1.add(get_tabBarPanel());
      f_TabPanel1.add(get_tabContent());
      f_TabPanel1.addStyleName("uf-tabbar-panel");

      return f_TabPanel1;
    }

    /**
     * Getter for tabBarPanel called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.HorizontalPanel get_tabBarPanel() {
      return build_tabBarPanel();
    }
    private com.google.gwt.user.client.ui.HorizontalPanel build_tabBarPanel() {
      // Creation section.
      final com.google.gwt.user.client.ui.HorizontalPanel tabBarPanel = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
      // Setup section.
      tabBarPanel.add(get_tabBar());
      tabBarPanel.setCellHorizontalAlignment(get_tabBar(), com.google.gwt.user.client.ui.HasHorizontalAlignment.ALIGN_LEFT);
      tabBarPanel.add(get_widgetsPanel());
      tabBarPanel.setCellHorizontalAlignment(get_widgetsPanel(), com.google.gwt.user.client.ui.HasHorizontalAlignment.ALIGN_RIGHT);
      tabBarPanel.setWidth("100%");

      this.owner.tabBarPanel = tabBarPanel;

      return tabBarPanel;
    }

    /**
     * Getter for tabBar called 2 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.NavTabs tabBar;
    private org.gwtbootstrap3.client.ui.NavTabs get_tabBar() {
      return tabBar;
    }
    private org.gwtbootstrap3.client.ui.NavTabs build_tabBar() {
      // Creation section.
      tabBar = (org.gwtbootstrap3.client.ui.NavTabs) GWT.create(org.gwtbootstrap3.client.ui.NavTabs.class);
      // Setup section.
      tabBar.addStyleName("uf-tabbar-panel-nav-tabs");

      this.owner.tabBar = tabBar;

      return tabBar;
    }

    /**
     * Getter for widgetsPanel called 2 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.HorizontalPanel widgetsPanel;
    private com.google.gwt.user.client.ui.HorizontalPanel get_widgetsPanel() {
      return widgetsPanel;
    }
    private com.google.gwt.user.client.ui.HorizontalPanel build_widgetsPanel() {
      // Creation section.
      widgetsPanel = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
      // Setup section.
      widgetsPanel.setHorizontalAlignment(com.google.gwt.user.client.ui.HasHorizontalAlignment.ALIGN_RIGHT);
      widgetsPanel.addStyleName("uf-tabbar-panel-widgets");
      widgetsPanel.setWidth("0px");

      this.owner.widgetsPanel = widgetsPanel;

      return widgetsPanel;
    }

    /**
     * Getter for tabContent called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.TabContent get_tabContent() {
      return build_tabContent();
    }
    private org.gwtbootstrap3.client.ui.TabContent build_tabContent() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TabContent tabContent = (org.gwtbootstrap3.client.ui.TabContent) GWT.create(org.gwtbootstrap3.client.ui.TabContent.class);
      // Setup section.

      this.owner.tabContent = tabContent;

      return tabContent;
    }
  }
}
