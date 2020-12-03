// .ui.xml template last modified: 1607021625917
package org.uberfire.ext.widgets.core.client.editors.defaulteditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class DefaultEditorFileUploadBase_DefaultEditorFileUploadBaseBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUploadBase>, org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUploadBase.DefaultEditorFileUploadBaseBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUploadBase owner) {


    return new Widgets(owner).get_f_Container1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUploadBase owner;


    public Widgets(final org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUploadBase owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUploadBase_DefaultEditorFileUploadBaseBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUploadBase_DefaultEditorFileUploadBaseBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUploadBase_DefaultEditorFileUploadBaseBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUploadBase_DefaultEditorFileUploadBaseBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultEditorFileUploadBase_DefaultEditorFileUploadBaseBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 1 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants get_i18n() {
      return build_i18n();
    }
    private org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants build_i18n() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants i18n = (org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants) GWT.create(org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants.class);
      // Setup section.

      return i18n;
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
      f_Row2.add(get_form());

      return f_Row2;
    }

    /**
     * Getter for form called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Form get_form() {
      return build_form();
    }
    private org.gwtbootstrap3.client.ui.Form build_form() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Form form = (org.gwtbootstrap3.client.ui.Form) GWT.create(org.gwtbootstrap3.client.ui.Form.class);
      // Setup section.
      form.add(get_f_FieldSet3());

      this.owner.form = form;

      return form;
    }

    /**
     * Getter for f_FieldSet3 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.FieldSet get_f_FieldSet3() {
      return build_f_FieldSet3();
    }
    private org.gwtbootstrap3.client.ui.FieldSet build_f_FieldSet3() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FieldSet f_FieldSet3 = (org.gwtbootstrap3.client.ui.FieldSet) GWT.create(org.gwtbootstrap3.client.ui.FieldSet.class);
      // Setup section.
      f_FieldSet3.add(get_f_FormGroup4());

      return f_FieldSet3;
    }

    /**
     * Getter for f_FormGroup4 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private org.gwtbootstrap3.client.ui.FormGroup get_f_FormGroup4() {
      return build_f_FormGroup4();
    }
    private org.gwtbootstrap3.client.ui.FormGroup build_f_FormGroup4() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.FormGroup f_FormGroup4 = (org.gwtbootstrap3.client.ui.FormGroup) GWT.create(org.gwtbootstrap3.client.ui.FormGroup.class);
      // Setup section.
      f_FormGroup4.add(get_f_FormLabel5());
      f_FormGroup4.add(get_fileUpload());

      return f_FormGroup4;
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
      f_FormLabel5.setText("" + get_i18n().SelectFileToUpload() + "");

      return f_FormLabel5;
    }

    /**
     * Getter for fileUpload called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private org.uberfire.ext.widgets.common.client.common.FileUpload get_fileUpload() {
      return build_fileUpload();
    }
    private org.uberfire.ext.widgets.common.client.common.FileUpload build_fileUpload() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.common.FileUpload fileUpload = owner.fileUpload;
      assert fileUpload != null : "UiField fileUpload with 'provided = true' was null";
      // Setup section.
      fileUpload.setName("fileUpload");

      return fileUpload;
    }
  }
}
