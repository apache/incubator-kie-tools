// .ui.xml template last modified: 1607096093334
package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class FilterPagedTable_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.common.client.tables.FilterPagedTable>, org.uberfire.ext.widgets.common.client.tables.FilterPagedTable.Binder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.common.client.tables.FilterPagedTable owner) {


    return new Widgets(owner).get_f_TabPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.common.client.tables.FilterPagedTable owner;


    public Widgets(final org.uberfire.ext.widgets.common.client.tables.FilterPagedTable owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.tables.FilterPagedTable_BinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.common.client.tables.FilterPagedTable_BinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.tables.FilterPagedTable_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.common.client.tables.FilterPagedTable_BinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.common.client.tables.FilterPagedTable_BinderImpl_GenBundle.class);
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
      f_TabPanel1.add(get_navTabs());
      f_TabPanel1.add(get_tabContent());

      return f_TabPanel1;
    }

    /**
     * Getter for navTabs called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.NavTabs get_navTabs() {
      return build_navTabs();
    }
    private org.gwtbootstrap3.client.ui.NavTabs build_navTabs() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.NavTabs navTabs = (org.gwtbootstrap3.client.ui.NavTabs) GWT.create(org.gwtbootstrap3.client.ui.NavTabs.class);
      // Setup section.

      this.owner.navTabs = navTabs;

      return navTabs;
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
