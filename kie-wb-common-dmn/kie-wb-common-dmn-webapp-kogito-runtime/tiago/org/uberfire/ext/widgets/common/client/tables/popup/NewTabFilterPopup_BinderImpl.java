// .ui.xml template last modified: 1607021623860
package org.uberfire.ext.widgets.common.client.tables.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class NewTabFilterPopup_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup>, org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup.Binder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup owner) {


    return new Widgets(owner).get_f_TabPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup owner;


    public Widgets(final org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup owner) {
      this.owner = owner;
      build_i18n();  // more than one getter call detected. Type: IMPORTED, precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup_BinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup_BinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup_BinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.common.client.tables.popup.NewTabFilterPopup_BinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 2 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants i18n;
    private org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants get_i18n() {
      return i18n;
    }
    private org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants build_i18n() {
      // Creation section.
      i18n = (org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants) GWT.create(org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.class);
      // Setup section.

      return i18n;
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
      f_TabPanel1.add(get_f_NavTabs2());
      f_TabPanel1.add(get_f_TabContent3());

      return f_TabPanel1;
    }

    /**
     * Getter for f_NavTabs2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.NavTabs get_f_NavTabs2() {
      return build_f_NavTabs2();
    }
    private org.gwtbootstrap3.client.ui.NavTabs build_f_NavTabs2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.NavTabs f_NavTabs2 = (org.gwtbootstrap3.client.ui.NavTabs) GWT.create(org.gwtbootstrap3.client.ui.NavTabs.class);
      // Setup section.
      f_NavTabs2.add(get_tabBasic());
      f_NavTabs2.add(get_tabFilter());

      return f_NavTabs2;
    }

    /**
     * Getter for tabBasic called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.TabListItem get_tabBasic() {
      return build_tabBasic();
    }
    private org.gwtbootstrap3.client.ui.TabListItem build_tabBasic() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TabListItem tabBasic = (org.gwtbootstrap3.client.ui.TabListItem) GWT.create(org.gwtbootstrap3.client.ui.TabListItem.class);
      // Setup section.
      tabBasic.setActive(true);
      tabBasic.setText("" + get_i18n().Basic_Properties() + "");

      this.owner.tabBasic = tabBasic;

      return tabBasic;
    }

    /**
     * Getter for tabFilter called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.TabListItem get_tabFilter() {
      return build_tabFilter();
    }
    private org.gwtbootstrap3.client.ui.TabListItem build_tabFilter() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TabListItem tabFilter = (org.gwtbootstrap3.client.ui.TabListItem) GWT.create(org.gwtbootstrap3.client.ui.TabListItem.class);
      // Setup section.
      tabFilter.setText("" + get_i18n().Filter_parameters() + "");

      this.owner.tabFilter = tabFilter;

      return tabFilter;
    }

    /**
     * Getter for f_TabContent3 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.TabContent get_f_TabContent3() {
      return build_f_TabContent3();
    }
    private org.gwtbootstrap3.client.ui.TabContent build_f_TabContent3() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TabContent f_TabContent3 = (org.gwtbootstrap3.client.ui.TabContent) GWT.create(org.gwtbootstrap3.client.ui.TabContent.class);
      // Setup section.
      f_TabContent3.add(get_tab1());
      f_TabContent3.add(get_tab2());

      return f_TabContent3;
    }

    /**
     * Getter for tab1 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.TabPane get_tab1() {
      return build_tab1();
    }
    private org.gwtbootstrap3.client.ui.TabPane build_tab1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TabPane tab1 = (org.gwtbootstrap3.client.ui.TabPane) GWT.create(org.gwtbootstrap3.client.ui.TabPane.class);
      // Setup section.
      tab1.add(get_f_Well4());
      tab1.setActive(true);

      this.owner.tab1 = tab1;

      return tab1;
    }

    /**
     * Getter for f_Well4 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Well get_f_Well4() {
      return build_f_Well4();
    }
    private org.gwtbootstrap3.client.ui.Well build_f_Well4() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Well f_Well4 = (org.gwtbootstrap3.client.ui.Well) GWT.create(org.gwtbootstrap3.client.ui.Well.class);
      // Setup section.
      f_Well4.add(get_f_FieldSet5());

      return f_Well4;
    }

    /**
     * Getter for f_FieldSet5 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.FieldSet get_f_FieldSet5() {
      return build_f_FieldSet5();
    }
    private org.gwtbootstrap3.client.ui.FieldSet build_f_FieldSet5() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FieldSet f_FieldSet5 = (org.gwtbootstrap3.client.ui.FieldSet) GWT.create(org.gwtbootstrap3.client.ui.FieldSet.class);
      // Setup section.
      f_FieldSet5.add(get_basicTabPanel());

      return f_FieldSet5;
    }

    /**
     * Getter for basicTabPanel called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_basicTabPanel() {
      return build_basicTabPanel();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_basicTabPanel() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel basicTabPanel = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.

      this.owner.basicTabPanel = basicTabPanel;

      return basicTabPanel;
    }

    /**
     * Getter for tab2 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.TabPane get_tab2() {
      return build_tab2();
    }
    private org.gwtbootstrap3.client.ui.TabPane build_tab2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TabPane tab2 = (org.gwtbootstrap3.client.ui.TabPane) GWT.create(org.gwtbootstrap3.client.ui.TabPane.class);
      // Setup section.
      tab2.add(get_f_Well6());

      this.owner.tab2 = tab2;

      return tab2;
    }

    /**
     * Getter for f_Well6 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Well get_f_Well6() {
      return build_f_Well6();
    }
    private org.gwtbootstrap3.client.ui.Well build_f_Well6() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Well f_Well6 = (org.gwtbootstrap3.client.ui.Well) GWT.create(org.gwtbootstrap3.client.ui.Well.class);
      // Setup section.
      f_Well6.add(get_f_FieldSet7());

      return f_Well6;
    }

    /**
     * Getter for f_FieldSet7 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.FieldSet get_f_FieldSet7() {
      return build_f_FieldSet7();
    }
    private org.gwtbootstrap3.client.ui.FieldSet build_f_FieldSet7() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FieldSet f_FieldSet7 = (org.gwtbootstrap3.client.ui.FieldSet) GWT.create(org.gwtbootstrap3.client.ui.FieldSet.class);
      // Setup section.
      f_FieldSet7.add(get_errorMessagesGroup());
      f_FieldSet7.add(get_filterForm());

      return f_FieldSet7;
    }

    /**
     * Getter for errorMessagesGroup called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private org.gwtbootstrap3.client.ui.FormGroup get_errorMessagesGroup() {
      return build_errorMessagesGroup();
    }
    private org.gwtbootstrap3.client.ui.FormGroup build_errorMessagesGroup() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormGroup errorMessagesGroup = (org.gwtbootstrap3.client.ui.FormGroup) GWT.create(org.gwtbootstrap3.client.ui.FormGroup.class);
      // Setup section.
      errorMessagesGroup.add(get_errorMessages());

      this.owner.errorMessagesGroup = errorMessagesGroup;

      return errorMessagesGroup;
    }

    /**
     * Getter for errorMessages called 1 times. Type: DEFAULT. Build precedence: 7.
     */
    private org.gwtbootstrap3.client.ui.HelpBlock get_errorMessages() {
      return build_errorMessages();
    }
    private org.gwtbootstrap3.client.ui.HelpBlock build_errorMessages() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.HelpBlock errorMessages = (org.gwtbootstrap3.client.ui.HelpBlock) GWT.create(org.gwtbootstrap3.client.ui.HelpBlock.class);
      // Setup section.

      this.owner.errorMessages = errorMessages;

      return errorMessages;
    }

    /**
     * Getter for filterForm called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private org.gwtbootstrap3.client.ui.Form get_filterForm() {
      return build_filterForm();
    }
    private org.gwtbootstrap3.client.ui.Form build_filterForm() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Form filterForm = (org.gwtbootstrap3.client.ui.Form) GWT.create(org.gwtbootstrap3.client.ui.Form.class);
      // Setup section.
      filterForm.setType(org.gwtbootstrap3.client.ui.constants.FormType.HORIZONTAL);

      this.owner.filterForm = filterForm;

      return filterForm;
    }
  }
}
