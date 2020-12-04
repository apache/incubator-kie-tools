// .ui.xml template last modified: 1607100813679
package org.guvnor.common.services.project.client.repositories;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ConflictingRepositoriesPopupViewImpl_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl>, org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl.Binder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl owner) {


    return new Widgets(owner).get_f_VerticalPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl owner;


    public Widgets(final org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenBundle) GWT.create(org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 0 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.guvnor.common.services.project.client.resources.i18n.ProjectConstants get_i18n() {
      return build_i18n();
    }
    private org.guvnor.common.services.project.client.resources.i18n.ProjectConstants build_i18n() {
      // Creation section.
      final org.guvnor.common.services.project.client.resources.i18n.ProjectConstants i18n = (org.guvnor.common.services.project.client.resources.i18n.ProjectConstants) GWT.create(org.guvnor.common.services.project.client.resources.i18n.ProjectConstants.class);
      // Setup section.

      return i18n;
    }

    /**
     * Getter for style called 1 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenCss_style get_style() {
      return build_style();
    }
    private org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenCss_style build_style() {
      // Creation section.
      final org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl_BinderImpl_GenCss_style style = get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay().style();
      // Setup section.
      style.ensureInjected();

      return style;
    }

    /**
     * Getter for f_VerticalPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.VerticalPanel get_f_VerticalPanel1() {
      return build_f_VerticalPanel1();
    }
    private com.google.gwt.user.client.ui.VerticalPanel build_f_VerticalPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
      // Setup section.
      f_VerticalPanel1.add(get_f_Well2());
      f_VerticalPanel1.add(get_table());
      f_VerticalPanel1.setWidth("100%");

      return f_VerticalPanel1;
    }

    /**
     * Getter for f_Well2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Well get_f_Well2() {
      return build_f_Well2();
    }
    private org.gwtbootstrap3.client.ui.Well build_f_Well2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Well f_Well2 = (org.gwtbootstrap3.client.ui.Well) GWT.create(org.gwtbootstrap3.client.ui.Well.class);
      // Setup section.
      f_Well2.add(get_f_Container3());

      return f_Well2;
    }

    /**
     * Getter for f_Container3 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Container get_f_Container3() {
      return build_f_Container3();
    }
    private org.gwtbootstrap3.client.ui.Container build_f_Container3() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Container f_Container3 = (org.gwtbootstrap3.client.ui.Container) GWT.create(org.gwtbootstrap3.client.ui.Container.class);
      // Setup section.
      f_Container3.add(get_f_Icon4());
      f_Container3.add(get_header());
      f_Container3.setFluid(true);

      return f_Container3;
    }

    /**
     * Getter for f_Icon4 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Icon get_f_Icon4() {
      return build_f_Icon4();
    }
    private org.gwtbootstrap3.client.ui.Icon build_f_Icon4() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Icon f_Icon4 = new org.gwtbootstrap3.client.ui.Icon(org.gwtbootstrap3.client.ui.constants.IconType.EXCLAMATION_TRIANGLE);
      // Setup section.
      f_Icon4.addStyleName("" + get_style().icon() + "");
      f_Icon4.setSize(org.gwtbootstrap3.client.ui.constants.IconSize.TIMES3);

      return f_Icon4;
    }

    /**
     * Getter for header called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Heading get_header() {
      return build_header();
    }
    private org.gwtbootstrap3.client.ui.Heading build_header() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Heading header = new org.gwtbootstrap3.client.ui.Heading(org.gwtbootstrap3.client.ui.constants.HeadingSize.H5);
      // Setup section.

      this.owner.header = header;

      return header;
    }

    /**
     * Getter for table called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.gwt.CellTable get_table() {
      return build_table();
    }
    private org.gwtbootstrap3.client.ui.gwt.CellTable build_table() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.gwt.CellTable table = owner.table;
      assert table != null : "UiField table with 'provided = true' was null";
      // Setup section.

      return table;
    }
  }
}
