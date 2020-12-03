// .ui.xml template last modified: 1607021867087
package org.kie.workbench.common.widgets.client.popups.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class TextBoxFormPopupViewImpl_AddNewKBasePopupViewImplBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl>, org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl.AddNewKBasePopupViewImplBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl owner) {


    return new Widgets(owner).get_f_Container1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl owner;


    public Widgets(final org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl_AddNewKBasePopupViewImplBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl_AddNewKBasePopupViewImplBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl_AddNewKBasePopupViewImplBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl_AddNewKBasePopupViewImplBinderImpl_GenBundle) GWT.create(org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl_AddNewKBasePopupViewImplBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 1 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants get_i18n() {
      return build_i18n();
    }
    private org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants build_i18n() {
      // Creation section.
      final org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants i18n = (org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants) GWT.create(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.class);
      // Setup section.

      return i18n;
    }

    /**
     * Getter for resources called 0 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.resources.CommonsResources get_resources() {
      return build_resources();
    }
    private org.kie.workbench.common.widgets.client.resources.CommonsResources build_resources() {
      // Creation section.
      final org.kie.workbench.common.widgets.client.resources.CommonsResources resources = (org.kie.workbench.common.widgets.client.resources.CommonsResources) GWT.create(org.kie.workbench.common.widgets.client.resources.CommonsResources.class);
      // Setup section.

      return resources;
    }

    /**
     * Getter for f_Container1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.Container get_f_Container1() {
      return build_f_Container1();
    }
    private org.gwtbootstrap3.client.ui.Container build_f_Container1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Container f_Container1 = (org.gwtbootstrap3.client.ui.Container) GWT.create(org.gwtbootstrap3.client.ui.Container.class);
      // Setup section.
      f_Container1.add(get_f_Row2());
      f_Container1.setFluid(true);

      return f_Container1;
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
      f_Row2.add(get_f_Column3());

      return f_Row2;
    }

    /**
     * Getter for f_Column3 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column3() {
      return build_f_Column3();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column3() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column3 = new org.gwtbootstrap3.client.ui.Column("MD_12");
      // Setup section.
      f_Column3.add(get_f_Form4());

      return f_Column3;
    }

    /**
     * Getter for f_Form4 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Form get_f_Form4() {
      return build_f_Form4();
    }
    private org.gwtbootstrap3.client.ui.Form build_f_Form4() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Form f_Form4 = (org.gwtbootstrap3.client.ui.Form) GWT.create(org.gwtbootstrap3.client.ui.Form.class);
      // Setup section.
      f_Form4.add(get_formGroupName());
      f_Form4.setType(org.gwtbootstrap3.client.ui.constants.FormType.HORIZONTAL);

      return f_Form4;
    }

    /**
     * Getter for formGroupName called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.FormGroup get_formGroupName() {
      return build_formGroupName();
    }
    private org.gwtbootstrap3.client.ui.FormGroup build_formGroupName() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormGroup formGroupName = (org.gwtbootstrap3.client.ui.FormGroup) GWT.create(org.gwtbootstrap3.client.ui.FormGroup.class);
      // Setup section.
      formGroupName.add(get_f_FormLabel5());
      formGroupName.add(get_f_Column6());

      this.owner.formGroupName = formGroupName;

      return formGroupName;
    }

    /**
     * Getter for f_FormLabel5 called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private org.gwtbootstrap3.client.ui.FormLabel get_f_FormLabel5() {
      return build_f_FormLabel5();
    }
    private org.gwtbootstrap3.client.ui.FormLabel build_f_FormLabel5() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormLabel f_FormLabel5 = (org.gwtbootstrap3.client.ui.FormLabel) GWT.create(org.gwtbootstrap3.client.ui.FormLabel.class);
      // Setup section.
      f_FormLabel5.addStyleName("col-md-2");
      f_FormLabel5.setText("" + get_i18n().Name() + "");

      return f_FormLabel5;
    }

    /**
     * Getter for f_Column6 called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column6() {
      return build_f_Column6();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column6() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column6 = new org.gwtbootstrap3.client.ui.Column("MD_10");
      // Setup section.
      f_Column6.add(get_nameTextBox());
      f_Column6.add(get_nameHelpBlock());

      return f_Column6;
    }

    /**
     * Getter for nameTextBox called 1 times. Type: DEFAULT. Build precedence: 7.
     */
    private org.gwtbootstrap3.client.ui.TextBox get_nameTextBox() {
      return build_nameTextBox();
    }
    private org.gwtbootstrap3.client.ui.TextBox build_nameTextBox() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TextBox nameTextBox = (org.gwtbootstrap3.client.ui.TextBox) GWT.create(org.gwtbootstrap3.client.ui.TextBox.class);
      // Setup section.

      this.owner.nameTextBox = nameTextBox;

      return nameTextBox;
    }

    /**
     * Getter for nameHelpBlock called 1 times. Type: DEFAULT. Build precedence: 7.
     */
    private org.gwtbootstrap3.client.ui.HelpBlock get_nameHelpBlock() {
      return build_nameHelpBlock();
    }
    private org.gwtbootstrap3.client.ui.HelpBlock build_nameHelpBlock() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.HelpBlock nameHelpBlock = (org.gwtbootstrap3.client.ui.HelpBlock) GWT.create(org.gwtbootstrap3.client.ui.HelpBlock.class);
      // Setup section.

      this.owner.nameHelpBlock = nameHelpBlock;

      return nameHelpBlock;
    }
  }
}
