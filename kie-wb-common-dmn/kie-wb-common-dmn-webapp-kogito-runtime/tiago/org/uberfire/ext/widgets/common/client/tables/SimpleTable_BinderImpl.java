// .ui.xml template last modified: 1607092934532
package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class SimpleTable_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.common.client.tables.SimpleTable>, org.uberfire.ext.widgets.common.client.tables.SimpleTable.Binder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.common.client.tables.SimpleTable owner) {


    return new Widgets(owner).get_f_FlowPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.common.client.tables.SimpleTable owner;


    public Widgets(final org.uberfire.ext.widgets.common.client.tables.SimpleTable owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
      build_leftToolbar();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_centerToolbar();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_rightToolbar();  // more than one getter call detected. Type: DEFAULT, precedence: 3
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.tables.SimpleTable_BinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.common.client.tables.SimpleTable_BinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.tables.SimpleTable_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.common.client.tables.SimpleTable_BinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.common.client.tables.SimpleTable_BinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for style called 4 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.tables.SimpleTable_BinderImpl_GenCss_style style;
    private org.uberfire.ext.widgets.common.client.tables.SimpleTable_BinderImpl_GenCss_style get_style() {
      return style;
    }
    private org.uberfire.ext.widgets.common.client.tables.SimpleTable_BinderImpl_GenCss_style build_style() {
      // Creation section.
      style = get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay().style();
      // Setup section.
      style.ensureInjected();

      return style;
    }

    /**
     * Getter for f_FlowPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_f_FlowPanel1() {
      return build_f_FlowPanel1();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_f_FlowPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel f_FlowPanel1 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      f_FlowPanel1.add(get_toolbarContainer());
      f_FlowPanel1.add(get_f_SimplePanel2());
      f_FlowPanel1.addStyleName("" + get_style().dataGridContainer() + "");

      return f_FlowPanel1;
    }

    /**
     * Getter for toolbarContainer called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.HorizontalPanel get_toolbarContainer() {
      return build_toolbarContainer();
    }
    private com.google.gwt.user.client.ui.HorizontalPanel build_toolbarContainer() {
      // Creation section.
      final com.google.gwt.user.client.ui.HorizontalPanel toolbarContainer = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
      // Setup section.
      toolbarContainer.add(get_leftToolbar());
      toolbarContainer.setCellWidth(get_leftToolbar(), "33%");
      toolbarContainer.add(get_centerToolbar());
      toolbarContainer.setCellWidth(get_centerToolbar(), "33%");
      toolbarContainer.add(get_rightToolbar());
      toolbarContainer.setCellHorizontalAlignment(get_rightToolbar(), com.google.gwt.user.client.ui.HasHorizontalAlignment.ALIGN_RIGHT);
      toolbarContainer.setCellWidth(get_rightToolbar(), "33%");
      toolbarContainer.addStyleName("" + get_style().horizontalContainer() + "");
      toolbarContainer.setWidth("100%");

      this.owner.toolbarContainer = toolbarContainer;

      return toolbarContainer;
    }

    /**
     * Getter for leftToolbar called 2 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.FlowPanel leftToolbar;
    private com.google.gwt.user.client.ui.FlowPanel get_leftToolbar() {
      return leftToolbar;
    }
    private com.google.gwt.user.client.ui.FlowPanel build_leftToolbar() {
      // Creation section.
      leftToolbar = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      leftToolbar.setWidth("100%");

      this.owner.leftToolbar = leftToolbar;

      return leftToolbar;
    }

    /**
     * Getter for centerToolbar called 2 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.FlowPanel centerToolbar;
    private com.google.gwt.user.client.ui.FlowPanel get_centerToolbar() {
      return centerToolbar;
    }
    private com.google.gwt.user.client.ui.FlowPanel build_centerToolbar() {
      // Creation section.
      centerToolbar = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      centerToolbar.addStyleName("" + get_style().centerToolBar() + "");
      centerToolbar.setWidth("100%");

      this.owner.centerToolbar = centerToolbar;

      return centerToolbar;
    }

    /**
     * Getter for rightToolbar called 3 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.HorizontalPanel rightToolbar;
    private com.google.gwt.user.client.ui.HorizontalPanel get_rightToolbar() {
      return rightToolbar;
    }
    private com.google.gwt.user.client.ui.HorizontalPanel build_rightToolbar() {
      // Creation section.
      rightToolbar = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
      // Setup section.
      rightToolbar.add(get_rightActionsToolbar());
      rightToolbar.add(get_columnPickerButton());

      this.owner.rightToolbar = rightToolbar;

      return rightToolbar;
    }

    /**
     * Getter for rightActionsToolbar called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_rightActionsToolbar() {
      return build_rightActionsToolbar();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_rightActionsToolbar() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel rightActionsToolbar = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      rightActionsToolbar.addStyleName("" + get_style().rightToolBar() + "");
      rightActionsToolbar.setWidth("100%");

      this.owner.rightActionsToolbar = rightActionsToolbar;

      return rightActionsToolbar;
    }

    /**
     * Getter for columnPickerButton called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Button get_columnPickerButton() {
      return build_columnPickerButton();
    }
    private org.gwtbootstrap3.client.ui.Button build_columnPickerButton() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button columnPickerButton = owner.columnPickerButton;
      assert columnPickerButton != null : "UiField columnPickerButton with 'provided = true' was null";
      // Setup section.

      return columnPickerButton;
    }

    /**
     * Getter for f_SimplePanel2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.SimplePanel get_f_SimplePanel2() {
      return build_f_SimplePanel2();
    }
    private com.google.gwt.user.client.ui.SimplePanel build_f_SimplePanel2() {
      // Creation section.
      final com.google.gwt.user.client.ui.SimplePanel f_SimplePanel2 = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
      // Setup section.
      f_SimplePanel2.add(get_dataGrid());

      return f_SimplePanel2;
    }

    /**
     * Getter for dataGrid called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.gwt.DataGrid get_dataGrid() {
      return build_dataGrid();
    }
    private org.gwtbootstrap3.client.ui.gwt.DataGrid build_dataGrid() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.gwt.DataGrid dataGrid = owner.dataGrid;
      assert dataGrid != null : "UiField dataGrid with 'provided = true' was null";
      // Setup section.

      return dataGrid;
    }
  }
}
