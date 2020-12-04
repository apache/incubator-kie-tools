// .ui.xml template last modified: 1607097787707
package org.uberfire.client.docks.view.items;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class SouthDockItem_ViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.client.docks.view.items.SouthDockItem>, org.uberfire.client.docks.view.items.SouthDockItem.ViewBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.client.docks.view.items.SouthDockItem owner) {


    return new Widgets(owner).get_itemButton();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.client.docks.view.items.SouthDockItem owner;


    public Widgets(final org.uberfire.client.docks.view.items.SouthDockItem owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.client.docks.view.items.SouthDockItem_ViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.client.docks.view.items.SouthDockItem_ViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.client.docks.view.items.SouthDockItem_ViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.client.docks.view.items.SouthDockItem_ViewBinderImpl_GenBundle) GWT.create(org.uberfire.client.docks.view.items.SouthDockItem_ViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for itemButton called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.Button get_itemButton() {
      return build_itemButton();
    }
    private org.gwtbootstrap3.client.ui.Button build_itemButton() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button itemButton = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.

      this.owner.itemButton = itemButton;

      return itemButton;
    }
  }
}
