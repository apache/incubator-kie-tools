// .ui.xml template last modified: 1607092935139
package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorComboBox_MyUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox>, org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox.MyUiBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox owner) {


    return new Widgets(owner).get_listBox();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox owner;


    public Widgets(final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox_MyUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox_MyUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox_MyUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox_MyUiBinderImpl_GenBundle) GWT.create(org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox_MyUiBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for listBox called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.ListBox get_listBox() {
      return build_listBox();
    }
    private org.gwtbootstrap3.client.ui.ListBox build_listBox() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.ListBox listBox = (org.gwtbootstrap3.client.ui.ListBox) GWT.create(org.gwtbootstrap3.client.ui.ListBox.class);
      // Setup section.

      this.owner.listBox = listBox;

      return listBox;
    }
  }
}
