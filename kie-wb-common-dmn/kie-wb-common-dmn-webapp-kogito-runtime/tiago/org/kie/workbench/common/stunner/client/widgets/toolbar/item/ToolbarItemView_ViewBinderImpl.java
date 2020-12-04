// .ui.xml template last modified: 1607095295880
package org.kie.workbench.common.stunner.client.widgets.toolbar.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ToolbarItemView_ViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView>, org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView.ViewBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView owner) {


    return new Widgets(owner).get_button();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView owner;


    public Widgets(final org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView_ViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView_ViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView_ViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView_ViewBinderImpl_GenBundle) GWT.create(org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView_ViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for button called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.Button get_button() {
      return build_button();
    }
    private org.gwtbootstrap3.client.ui.Button build_button() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button button = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.

      this.owner.button = button;

      return button;
    }
  }
}
