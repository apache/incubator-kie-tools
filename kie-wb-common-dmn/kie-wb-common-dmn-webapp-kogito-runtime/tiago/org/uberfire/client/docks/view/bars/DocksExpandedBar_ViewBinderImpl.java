// .ui.xml template last modified: 1607097787707
package org.uberfire.client.docks.view.bars;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class DocksExpandedBar_ViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.client.docks.view.bars.DocksExpandedBar>, org.uberfire.client.docks.view.bars.DocksExpandedBar.ViewBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.client.docks.view.bars.DocksExpandedBar owner) {


    return new Widgets(owner).get_f_FlowPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.client.docks.view.bars.DocksExpandedBar owner;


    public Widgets(final org.uberfire.client.docks.view.bars.DocksExpandedBar owner) {
      this.owner = owner;
      build_res();  // more than one getter call detected. Type: IMPORTED, precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.client.docks.view.bars.DocksExpandedBar_ViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.client.docks.view.bars.DocksExpandedBar_ViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.client.docks.view.bars.DocksExpandedBar_ViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.client.docks.view.bars.DocksExpandedBar_ViewBinderImpl_GenBundle) GWT.create(org.uberfire.client.docks.view.bars.DocksExpandedBar_ViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for res called 3 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.client.resources.WebAppResource res;
    private org.uberfire.client.resources.WebAppResource get_res() {
      return res;
    }
    private org.uberfire.client.resources.WebAppResource build_res() {
      // Creation section.
      res = (org.uberfire.client.resources.WebAppResource) GWT.create(org.uberfire.client.resources.WebAppResource.class);
      // Setup section.

      return res;
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
      f_FlowPanel1.add(get_titlePanel());
      f_FlowPanel1.add(get_f_ScrollPanel2());
      f_FlowPanel1.setStyleName("" + get_res().CSS().dockExpanded() + "");

      return f_FlowPanel1;
    }

    /**
     * Getter for titlePanel called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_titlePanel() {
      return build_titlePanel();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_titlePanel() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel titlePanel = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      titlePanel.setStyleName("" + get_res().CSS().dockExpandedTitlePanel() + "");

      this.owner.titlePanel = titlePanel;

      return titlePanel;
    }

    /**
     * Getter for f_ScrollPanel2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.ScrollPanel get_f_ScrollPanel2() {
      return build_f_ScrollPanel2();
    }
    private com.google.gwt.user.client.ui.ScrollPanel build_f_ScrollPanel2() {
      // Creation section.
      final com.google.gwt.user.client.ui.ScrollPanel f_ScrollPanel2 = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
      // Setup section.
      f_ScrollPanel2.add(get_targetPanel());
      f_ScrollPanel2.setStyleName("" + get_res().CSS().dockExpandedContentPanel() + "");

      return f_ScrollPanel2;
    }

    /**
     * Getter for targetPanel called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_targetPanel() {
      return build_targetPanel();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_targetPanel() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel targetPanel = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.

      this.owner.targetPanel = targetPanel;

      return targetPanel;
    }
  }
}
