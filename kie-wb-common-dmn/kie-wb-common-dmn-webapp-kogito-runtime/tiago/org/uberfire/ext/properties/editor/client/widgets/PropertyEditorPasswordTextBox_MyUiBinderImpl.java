// .ui.xml template last modified: 1607021624412
package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorPasswordTextBox_MyUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox>, org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox.MyUiBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox owner) {


    return new Widgets(owner).get_passwordTextBox();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox owner;


    public Widgets(final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox_MyUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox_MyUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox_MyUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox_MyUiBinderImpl_GenBundle) GWT.create(org.uberfire.ext.properties.editor.client.widgets.PropertyEditorPasswordTextBox_MyUiBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for passwordTextBox called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.Input get_passwordTextBox() {
      return build_passwordTextBox();
    }
    private org.gwtbootstrap3.client.ui.Input build_passwordTextBox() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Input passwordTextBox = new org.gwtbootstrap3.client.ui.Input(org.gwtbootstrap3.client.ui.constants.InputType.PASSWORD);
      // Setup section.

      this.owner.passwordTextBox = passwordTextBox;

      return passwordTextBox;
    }
  }
}
