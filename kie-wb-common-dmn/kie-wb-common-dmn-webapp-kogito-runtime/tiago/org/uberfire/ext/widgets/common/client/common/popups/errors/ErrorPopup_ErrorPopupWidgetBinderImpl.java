// .ui.xml template last modified: 1607100753488
package org.uberfire.ext.widgets.common.client.common.popups.errors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ErrorPopup_ErrorPopupWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup>, org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup.ErrorPopupWidgetBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup owner) {


    return new Widgets(owner).get_f_ScrollPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup owner;


    public Widgets(final org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup_ErrorPopupWidgetBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup_ErrorPopupWidgetBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup_ErrorPopupWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup_ErrorPopupWidgetBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup_ErrorPopupWidgetBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for f_ScrollPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.ScrollPanel get_f_ScrollPanel1() {
      return build_f_ScrollPanel1();
    }
    private com.google.gwt.user.client.ui.ScrollPanel build_f_ScrollPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.ScrollPanel f_ScrollPanel1 = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
      // Setup section.
      f_ScrollPanel1.add(get_message());
      f_ScrollPanel1.setWidth("100%");
      f_ScrollPanel1.setHeight("200px");

      return f_ScrollPanel1;
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
