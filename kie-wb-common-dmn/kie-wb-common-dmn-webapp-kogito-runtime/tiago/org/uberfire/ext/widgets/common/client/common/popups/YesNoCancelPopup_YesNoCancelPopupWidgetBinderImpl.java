// .ui.xml template last modified: 1607021623860
package org.uberfire.ext.widgets.common.client.common.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class YesNoCancelPopup_YesNoCancelPopupWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup>, org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup.YesNoCancelPopupWidgetBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup owner) {


    return new Widgets(owner).get_scroll();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup owner;


    public Widgets(final org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup_YesNoCancelPopupWidgetBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup_YesNoCancelPopupWidgetBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup_YesNoCancelPopupWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup_YesNoCancelPopupWidgetBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup_YesNoCancelPopupWidgetBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for scroll called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.ScrollPanel get_scroll() {
      return build_scroll();
    }
    private com.google.gwt.user.client.ui.ScrollPanel build_scroll() {
      // Creation section.
      final com.google.gwt.user.client.ui.ScrollPanel scroll = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
      // Setup section.
      scroll.add(get_message());
      scroll.setWidth("100%");
      scroll.setHeight("200px");

      this.owner.scroll = scroll;

      return scroll;
    }

    /**
     * Getter for message called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.HTML get_message() {
      return build_message();
    }
    private com.google.gwt.user.client.ui.HTML build_message() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTML message = (com.google.gwt.user.client.ui.HTML) GWT.create(com.google.gwt.user.client.ui.HTML.class);
      // Setup section.

      this.owner.message = message;

      return message;
    }
  }
}
