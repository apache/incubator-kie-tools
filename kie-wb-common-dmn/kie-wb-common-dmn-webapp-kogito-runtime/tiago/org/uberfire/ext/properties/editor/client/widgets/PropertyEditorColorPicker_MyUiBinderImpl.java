// .ui.xml template last modified: 1607100754163
package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorColorPicker_MyUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker>, org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker.MyUiBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker owner) {


    return new Widgets(owner).get_f_FlowPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker owner;


    public Widgets(final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker_MyUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker_MyUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker_MyUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker_MyUiBinderImpl_GenBundle) GWT.create(org.uberfire.ext.properties.editor.client.widgets.PropertyEditorColorPicker_MyUiBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
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
      f_FlowPanel1.add(get_f_InputGroup2());

      return f_FlowPanel1;
    }

    /**
     * Getter for f_InputGroup2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.InputGroup get_f_InputGroup2() {
      return build_f_InputGroup2();
    }
    private org.gwtbootstrap3.client.ui.InputGroup build_f_InputGroup2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.InputGroup f_InputGroup2 = (org.gwtbootstrap3.client.ui.InputGroup) GWT.create(org.gwtbootstrap3.client.ui.InputGroup.class);
      // Setup section.
      f_InputGroup2.add(get_icon());
      f_InputGroup2.add(get_colorTextBox());

      return f_InputGroup2;
    }

    /**
     * Getter for icon called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.InputGroupAddon get_icon() {
      return build_icon();
    }
    private org.gwtbootstrap3.client.ui.InputGroupAddon build_icon() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.InputGroupAddon icon = (org.gwtbootstrap3.client.ui.InputGroupAddon) GWT.create(org.gwtbootstrap3.client.ui.InputGroupAddon.class);
      // Setup section.
      icon.setIcon(org.gwtbootstrap3.client.ui.constants.IconType.EDIT);

      this.owner.icon = icon;

      return icon;
    }

    /**
     * Getter for colorTextBox called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.TextBox get_colorTextBox() {
      return build_colorTextBox();
    }
    private org.gwtbootstrap3.client.ui.TextBox build_colorTextBox() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TextBox colorTextBox = (org.gwtbootstrap3.client.ui.TextBox) GWT.create(org.gwtbootstrap3.client.ui.TextBox.class);
      // Setup section.

      this.owner.colorTextBox = colorTextBox;

      return colorTextBox;
    }
  }
}
