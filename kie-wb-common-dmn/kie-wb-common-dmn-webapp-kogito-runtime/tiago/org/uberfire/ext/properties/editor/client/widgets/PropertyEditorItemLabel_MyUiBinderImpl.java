// .ui.xml template last modified: 1607100754163
package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorItemLabel_MyUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel>, org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel.MyUiBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel owner) {


    return new Widgets(owner).get_label();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel owner;


    public Widgets(final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel_MyUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel_MyUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel_MyUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel_MyUiBinderImpl_GenBundle) GWT.create(org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel_MyUiBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for label called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.uberfire.client.views.pfly.widgets.FormLabelHelp get_label() {
      return build_label();
    }
    private org.uberfire.client.views.pfly.widgets.FormLabelHelp build_label() {
      // Creation section.
      final org.uberfire.client.views.pfly.widgets.FormLabelHelp label = (org.uberfire.client.views.pfly.widgets.FormLabelHelp) GWT.create(org.uberfire.client.views.pfly.widgets.FormLabelHelp.class);
      // Setup section.
      label.addStyleName("col-md-4");

      this.owner.label = label;

      return label;
    }
  }
}
