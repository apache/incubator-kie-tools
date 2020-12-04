// .ui.xml template last modified: 1607093014736
package org.uberfire.client.docks.view.bars;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class DocksCollapsedBar_ViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.client.docks.view.bars.DocksCollapsedBar>, org.uberfire.client.docks.view.bars.DocksCollapsedBar.ViewBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.client.docks.view.bars.DocksCollapsedBar owner) {


    return new Widgets(owner).get_docksBarPanel();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.client.docks.view.bars.DocksCollapsedBar owner;


    public Widgets(final org.uberfire.client.docks.view.bars.DocksCollapsedBar owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.client.docks.view.bars.DocksCollapsedBar_ViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.client.docks.view.bars.DocksCollapsedBar_ViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.client.docks.view.bars.DocksCollapsedBar_ViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.client.docks.view.bars.DocksCollapsedBar_ViewBinderImpl_GenBundle) GWT.create(org.uberfire.client.docks.view.bars.DocksCollapsedBar_ViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for docksBarPanel called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_docksBarPanel() {
      return build_docksBarPanel();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_docksBarPanel() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel docksBarPanel = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.

      this.owner.docksBarPanel = docksBarPanel;

      return docksBarPanel;
    }
  }
}
