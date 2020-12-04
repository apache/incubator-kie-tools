// .ui.xml template last modified: 1607096093334
package org.uberfire.ext.widgets.common.client.tables;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PagedTable_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.common.client.tables.PagedTable>, org.uberfire.ext.widgets.common.client.tables.PagedTable.Binder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.common.client.tables.PagedTable owner) {


    return new Widgets(owner).get_f_FlowPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.common.client.tables.PagedTable owner;


    public Widgets(final org.uberfire.ext.widgets.common.client.tables.PagedTable owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
      build_i18n();  // more than one getter call detected. Type: IMPORTED, precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.tables.PagedTable_BinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.common.client.tables.PagedTable_BinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.tables.PagedTable_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.common.client.tables.PagedTable_BinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.common.client.tables.PagedTable_BinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 4 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.table.client.resources.i18n.CommonConstants i18n;
    private org.uberfire.ext.widgets.table.client.resources.i18n.CommonConstants get_i18n() {
      return i18n;
    }
    private org.uberfire.ext.widgets.table.client.resources.i18n.CommonConstants build_i18n() {
      // Creation section.
      i18n = (org.uberfire.ext.widgets.table.client.resources.i18n.CommonConstants) GWT.create(org.uberfire.ext.widgets.table.client.resources.i18n.CommonConstants.class);
      // Setup section.

      return i18n;
    }

    /**
     * Getter for style called 7 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.tables.PagedTable_BinderImpl_GenCss_style style;
    private org.uberfire.ext.widgets.common.client.tables.PagedTable_BinderImpl_GenCss_style get_style() {
      return style;
    }
    private org.uberfire.ext.widgets.common.client.tables.PagedTable_BinderImpl_GenCss_style build_style() {
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
      f_FlowPanel1.add(get_f_Row2());
      f_FlowPanel1.add(get_toolbarContainer());
      f_FlowPanel1.add(get_f_Row6());
      f_FlowPanel1.add(get_f_Row7());
      f_FlowPanel1.addStyleName("" + get_style().pagedTableContainer() + "");

      return f_FlowPanel1;
    }

    /**
     * Getter for f_Row2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Row get_f_Row2() {
      return build_f_Row2();
    }
    private org.gwtbootstrap3.client.ui.Row build_f_Row2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Row f_Row2 = (org.gwtbootstrap3.client.ui.Row) GWT.create(org.gwtbootstrap3.client.ui.Row.class);
      // Setup section.
      f_Row2.add(get_topToolbar());

      return f_Row2;
    }

    /**
     * Getter for topToolbar called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_topToolbar() {
      return build_topToolbar();
    }
    private org.gwtbootstrap3.client.ui.Column build_topToolbar() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column topToolbar = new org.gwtbootstrap3.client.ui.Column("MD_12");
      // Setup section.

      this.owner.topToolbar = topToolbar;

      return topToolbar;
    }

    /**
     * Getter for toolbarContainer called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Row get_toolbarContainer() {
      return build_toolbarContainer();
    }
    private org.gwtbootstrap3.client.ui.Row build_toolbarContainer() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Row toolbarContainer = (org.gwtbootstrap3.client.ui.Row) GWT.create(org.gwtbootstrap3.client.ui.Row.class);
      // Setup section.
      toolbarContainer.add(get_f_Column3());
      toolbarContainer.add(get_f_Column4());
      toolbarContainer.add(get_f_Column5());

      this.owner.toolbarContainer = toolbarContainer;

      return toolbarContainer;
    }

    /**
     * Getter for f_Column3 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column3() {
      return build_f_Column3();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column3() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column3 = new org.gwtbootstrap3.client.ui.Column("MD_4");
      // Setup section.
      f_Column3.add(get_leftToolbar());

      return f_Column3;
    }

    /**
     * Getter for leftToolbar called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_leftToolbar() {
      return build_leftToolbar();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_leftToolbar() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel leftToolbar = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      leftToolbar.addStyleName("" + get_style().leftToolBar() + "");
      leftToolbar.setWidth("100%");

      this.owner.leftToolbar = leftToolbar;

      return leftToolbar;
    }

    /**
     * Getter for f_Column4 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column4() {
      return build_f_Column4();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column4() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column4 = new org.gwtbootstrap3.client.ui.Column("MD_4");
      // Setup section.
      f_Column4.add(get_centerToolbar());

      return f_Column4;
    }

    /**
     * Getter for centerToolbar called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_centerToolbar() {
      return build_centerToolbar();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_centerToolbar() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel centerToolbar = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      centerToolbar.addStyleName("" + get_style().centerToolBar() + "");
      centerToolbar.setWidth("100%");

      this.owner.centerToolbar = centerToolbar;

      return centerToolbar;
    }

    /**
     * Getter for f_Column5 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column5() {
      return build_f_Column5();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column5() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column5 = new org.gwtbootstrap3.client.ui.Column("MD_4");
      // Setup section.
      f_Column5.add(get_rightToolbar());

      return f_Column5;
    }

    /**
     * Getter for rightToolbar called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.html.Div get_rightToolbar() {
      return build_rightToolbar();
    }
    private org.gwtbootstrap3.client.ui.html.Div build_rightToolbar() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.html.Div rightToolbar = (org.gwtbootstrap3.client.ui.html.Div) GWT.create(org.gwtbootstrap3.client.ui.html.Div.class);
      // Setup section.
      rightToolbar.add(get_rightActionsToolbar());
      rightToolbar.add(get_columnPickerButton());
      rightToolbar.setPull(org.gwtbootstrap3.client.ui.constants.Pull.RIGHT);

      this.owner.rightToolbar = rightToolbar;

      return rightToolbar;
    }

    /**
     * Getter for rightActionsToolbar called 1 times. Type: DEFAULT. Build precedence: 5.
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
     * Getter for columnPickerButton called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.Button get_columnPickerButton() {
      return build_columnPickerButton();
    }
    private org.gwtbootstrap3.client.ui.Button build_columnPickerButton() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button columnPickerButton = owner.columnPickerButton;
      assert columnPickerButton != null : "UiField columnPickerButton with 'provided = true' was null";
      // Setup section.
      columnPickerButton.setIcon(org.gwtbootstrap3.client.ui.constants.IconType.COLUMNS);
      columnPickerButton.setDataToggle(org.gwtbootstrap3.client.ui.constants.Toggle.BUTTON);

      return columnPickerButton;
    }

    /**
     * Getter for f_Row6 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Row get_f_Row6() {
      return build_f_Row6();
    }
    private org.gwtbootstrap3.client.ui.Row build_f_Row6() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Row f_Row6 = (org.gwtbootstrap3.client.ui.Row) GWT.create(org.gwtbootstrap3.client.ui.Row.class);
      // Setup section.
      f_Row6.add(get_dataGridContainer());
      f_Row6.addStyleName("" + get_style().dataGridContainer() + "");

      return f_Row6;
    }

    /**
     * Getter for dataGridContainer called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_dataGridContainer() {
      return build_dataGridContainer();
    }
    private org.gwtbootstrap3.client.ui.Column build_dataGridContainer() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column dataGridContainer = new org.gwtbootstrap3.client.ui.Column("MD_12");
      // Setup section.
      dataGridContainer.add(get_dataGrid());

      this.owner.dataGridContainer = dataGridContainer;

      return dataGridContainer;
    }

    /**
     * Getter for dataGrid called 1 times. Type: DEFAULT. Build precedence: 4.
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

    /**
     * Getter for f_Row7 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Row get_f_Row7() {
      return build_f_Row7();
    }
    private org.gwtbootstrap3.client.ui.Row build_f_Row7() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Row f_Row7 = (org.gwtbootstrap3.client.ui.Row) GWT.create(org.gwtbootstrap3.client.ui.Row.class);
      // Setup section.
      f_Row7.add(get_f_Column8());
      f_Row7.addStyleName("" + get_style().pagerRow() + "");

      return f_Row7;
    }

    /**
     * Getter for f_Column8 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column8() {
      return build_f_Column8();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column8() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column8 = new org.gwtbootstrap3.client.ui.Column("MD_12");
      // Setup section.
      f_Column8.add(get_pager());
      f_Column8.add(get_pageSizesSelector());

      return f_Column8;
    }

    /**
     * Getter for pager called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.uberfire.ext.widgets.table.client.UberfireSimplePager get_pager() {
      return build_pager();
    }
    private org.uberfire.ext.widgets.table.client.UberfireSimplePager build_pager() {
      // Creation section.
      final org.uberfire.ext.widgets.table.client.UberfireSimplePager pager = (org.uberfire.ext.widgets.table.client.UberfireSimplePager) GWT.create(org.uberfire.ext.widgets.table.client.UberfireSimplePager.class);
      // Setup section.
      pager.addStyleName("pagination");
      pager.addStyleName("pagination-right");
      pager.addStyleName("pull-right");
      pager.addStyleName("" + get_style().pager() + "");

      this.owner.pager = pager;

      return pager;
    }

    /**
     * Getter for pageSizesSelector called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.extras.select.client.ui.Select get_pageSizesSelector() {
      return build_pageSizesSelector();
    }
    private org.gwtbootstrap3.extras.select.client.ui.Select build_pageSizesSelector() {
      // Creation section.
      final org.gwtbootstrap3.extras.select.client.ui.Select pageSizesSelector = (org.gwtbootstrap3.extras.select.client.ui.Select) GWT.create(org.gwtbootstrap3.extras.select.client.ui.Select.class);
      // Setup section.
      pageSizesSelector.add(get_f_Option9());
      pageSizesSelector.add(get_f_Option10());
      pageSizesSelector.add(get_f_Option11());
      pageSizesSelector.add(get_f_Option12());
      pageSizesSelector.setWidth("100px");

      this.owner.pageSizesSelector = pageSizesSelector;

      return pageSizesSelector;
    }

    /**
     * Getter for f_Option9 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.extras.select.client.ui.Option get_f_Option9() {
      return build_f_Option9();
    }
    private org.gwtbootstrap3.extras.select.client.ui.Option build_f_Option9() {
      // Creation section.
      final org.gwtbootstrap3.extras.select.client.ui.Option f_Option9 = (org.gwtbootstrap3.extras.select.client.ui.Option) GWT.create(org.gwtbootstrap3.extras.select.client.ui.Option.class);
      // Setup section.
      f_Option9.setText("10 " + get_i18n().Items() + "");
      f_Option9.setValue("10");

      return f_Option9;
    }

    /**
     * Getter for f_Option10 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.extras.select.client.ui.Option get_f_Option10() {
      return build_f_Option10();
    }
    private org.gwtbootstrap3.extras.select.client.ui.Option build_f_Option10() {
      // Creation section.
      final org.gwtbootstrap3.extras.select.client.ui.Option f_Option10 = (org.gwtbootstrap3.extras.select.client.ui.Option) GWT.create(org.gwtbootstrap3.extras.select.client.ui.Option.class);
      // Setup section.
      f_Option10.setText("20 " + get_i18n().Items() + "");
      f_Option10.setValue("20");

      return f_Option10;
    }

    /**
     * Getter for f_Option11 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.extras.select.client.ui.Option get_f_Option11() {
      return build_f_Option11();
    }
    private org.gwtbootstrap3.extras.select.client.ui.Option build_f_Option11() {
      // Creation section.
      final org.gwtbootstrap3.extras.select.client.ui.Option f_Option11 = (org.gwtbootstrap3.extras.select.client.ui.Option) GWT.create(org.gwtbootstrap3.extras.select.client.ui.Option.class);
      // Setup section.
      f_Option11.setText("50 " + get_i18n().Items() + "");
      f_Option11.setValue("50");

      return f_Option11;
    }

    /**
     * Getter for f_Option12 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.extras.select.client.ui.Option get_f_Option12() {
      return build_f_Option12();
    }
    private org.gwtbootstrap3.extras.select.client.ui.Option build_f_Option12() {
      // Creation section.
      final org.gwtbootstrap3.extras.select.client.ui.Option f_Option12 = (org.gwtbootstrap3.extras.select.client.ui.Option) GWT.create(org.gwtbootstrap3.extras.select.client.ui.Option.class);
      // Setup section.
      f_Option12.setText("100 " + get_i18n().Items() + "");
      f_Option12.setValue("100");

      return f_Option12;
    }
  }
}
