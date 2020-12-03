// .ui.xml template last modified: 1607021895153
package org.kie.workbench.common.stunner.client.widgets.explorer.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class TreeExplorerView_ViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView>, org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView.ViewBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView owner) {


    return new Widgets(owner).get_f_FlowPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView owner;


    public Widgets(final org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView_ViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView_ViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView_ViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView_ViewBinderImpl_GenBundle) GWT.create(org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView_ViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for style called 0 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView_ViewBinderImpl_GenCss_style get_style() {
      return build_style();
    }
    private org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView_ViewBinderImpl_GenCss_style build_style() {
      // Creation section.
      final org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView_ViewBinderImpl_GenCss_style style = get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay().style();
      // Setup section.
      style.ensureInjected();

      return style;
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
      f_FlowPanel1.add(get_tree());

      return f_FlowPanel1;
    }

    /**
     * Getter for tree called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.uberfire.ext.widgets.core.client.tree.Tree get_tree() {
      return build_tree();
    }
    private org.uberfire.ext.widgets.core.client.tree.Tree build_tree() {
      // Creation section.
      final org.uberfire.ext.widgets.core.client.tree.Tree tree = (org.uberfire.ext.widgets.core.client.tree.Tree) GWT.create(org.uberfire.ext.widgets.core.client.tree.Tree.class);
      // Setup section.

      this.owner.tree = tree;

      return tree;
    }
  }
}
