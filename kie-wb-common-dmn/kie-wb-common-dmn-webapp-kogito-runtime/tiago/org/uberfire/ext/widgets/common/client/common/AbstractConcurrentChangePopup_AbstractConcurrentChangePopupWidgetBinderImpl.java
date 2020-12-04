// .ui.xml template last modified: 1607096093334
package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class AbstractConcurrentChangePopup_AbstractConcurrentChangePopupWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.common.client.common.AbstractConcurrentChangePopup>, org.uberfire.ext.widgets.common.client.common.AbstractConcurrentChangePopup.AbstractConcurrentChangePopupWidgetBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.common.client.common.AbstractConcurrentChangePopup owner) {


    return new Widgets(owner).get_f_Well1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.common.client.common.AbstractConcurrentChangePopup owner;


    public Widgets(final org.uberfire.ext.widgets.common.client.common.AbstractConcurrentChangePopup owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.common.AbstractConcurrentChangePopup_AbstractConcurrentChangePopupWidgetBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.common.client.common.AbstractConcurrentChangePopup_AbstractConcurrentChangePopupWidgetBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.common.AbstractConcurrentChangePopup_AbstractConcurrentChangePopupWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.common.client.common.AbstractConcurrentChangePopup_AbstractConcurrentChangePopupWidgetBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.common.client.common.AbstractConcurrentChangePopup_AbstractConcurrentChangePopupWidgetBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for f_Well1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.Well get_f_Well1() {
      return build_f_Well1();
    }
    private org.gwtbootstrap3.client.ui.Well build_f_Well1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Well f_Well1 = (org.gwtbootstrap3.client.ui.Well) GWT.create(org.gwtbootstrap3.client.ui.Well.class);
      // Setup section.
      f_Well1.add(get_f_ScrollPanel2());

      return f_Well1;
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
      f_ScrollPanel2.add(get_message());
      f_ScrollPanel2.setWidth("100%");
      f_ScrollPanel2.setHeight("200px");

      return f_ScrollPanel2;
    }

    /**
     * Getter for message called 1 times. Type: DEFAULT. Build precedence: 3.
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
