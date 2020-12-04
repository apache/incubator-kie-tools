// .ui.xml template last modified: 1607096159327
package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class DefaultToolbarView_ViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView>, org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView.ViewBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView owner) {


    return new Widgets(owner).get_mainGroup();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView owner;


    public Widgets(final org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView_ViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView_ViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView_ViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView_ViewBinderImpl_GenBundle) GWT.create(org.kie.workbench.common.stunner.client.widgets.toolbar.impl.DefaultToolbarView_ViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for mainGroup called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.ButtonGroup get_mainGroup() {
      return build_mainGroup();
    }
    private org.gwtbootstrap3.client.ui.ButtonGroup build_mainGroup() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.ButtonGroup mainGroup = (org.gwtbootstrap3.client.ui.ButtonGroup) GWT.create(org.gwtbootstrap3.client.ui.ButtonGroup.class);
      // Setup section.
      mainGroup.setSize(org.gwtbootstrap3.client.ui.constants.ButtonGroupSize.LARGE);
      mainGroup.setHeight("48px");

      this.owner.mainGroup = mainGroup;

      return mainGroup;
    }
  }
}
