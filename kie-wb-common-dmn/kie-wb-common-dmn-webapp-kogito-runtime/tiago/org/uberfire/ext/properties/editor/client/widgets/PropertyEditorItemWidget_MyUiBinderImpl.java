// .ui.xml template last modified: 1607100754163
package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorItemWidget_MyUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemWidget>, org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemWidget.MyUiBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemWidget owner) {


    return new Widgets(owner).get_item();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemWidget owner;


    public Widgets(final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemWidget owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemWidget_MyUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemWidget_MyUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemWidget_MyUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemWidget_MyUiBinderImpl_GenBundle) GWT.create(org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemWidget_MyUiBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for item called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.Column get_item() {
      return build_item();
    }
    private org.gwtbootstrap3.client.ui.Column build_item() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column item = new org.gwtbootstrap3.client.ui.Column("MD_8");
      // Setup section.

      this.owner.item = item;

      return item;
    }
  }
}
