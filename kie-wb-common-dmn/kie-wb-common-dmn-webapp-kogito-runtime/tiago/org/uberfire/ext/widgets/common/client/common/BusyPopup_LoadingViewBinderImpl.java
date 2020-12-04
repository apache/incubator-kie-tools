// .ui.xml template last modified: 1607096093334
package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class BusyPopup_LoadingViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.widgets.common.client.common.BusyPopup>, org.uberfire.ext.widgets.common.client.common.BusyPopup.LoadingViewBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("")
    SafeHtml html1();
     
    @Template("<span id='{0}'></span>")
    SafeHtml html2(String arg0);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.widgets.common.client.common.BusyPopup owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.widgets.common.client.common.BusyPopup owner;


    public Widgets(final org.uberfire.ext.widgets.common.client.common.BusyPopup owner) {
      this.owner = owner;
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1();
    }
    SafeHtml template_html2() {
      return template.html2(get_domId0());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.common.BusyPopup_LoadingViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.widgets.common.client.common.BusyPopup_LoadingViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.common.BusyPopup_LoadingViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.widgets.common.client.common.BusyPopup_LoadingViewBinderImpl_GenBundle) GWT.create(org.uberfire.ext.widgets.common.client.common.BusyPopup_LoadingViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for resources called 1 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.widgets.common.client.resources.CommonResources get_resources() {
      return build_resources();
    }
    private org.uberfire.ext.widgets.common.client.resources.CommonResources build_resources() {
      // Creation section.
      final org.uberfire.ext.widgets.common.client.resources.CommonResources resources = (org.uberfire.ext.widgets.common.client.resources.CommonResources) GWT.create(org.uberfire.ext.widgets.common.client.resources.CommonResources.class);
      // Setup section.

      return resources;
    }

    /**
     * Getter for f_HTMLPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel1() {
      return build_f_HTMLPanel1();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html2().asString());
      // Setup section.
      f_HTMLPanel1.setStyleName("" + get_resources().CSS().busyPopup() + "");

      {
        // Attach section.
        UiBinderUtil.TempAttachment __attachRecord__ = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());

        get_domId0Element().get();

        // Detach section.
        __attachRecord__.detach();
      }
      f_HTMLPanel1.addAndReplaceElement(get_f_Well2(), get_domId0Element().get());

      return f_HTMLPanel1;
    }

    /**
     * Getter for domId0 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId0;
    private java.lang.String get_domId0() {
      return domId0;
    }
    private java.lang.String build_domId0() {
      // Creation section.
      domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.

      return domId0;
    }

    /**
     * Getter for f_Well2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Well get_f_Well2() {
      return build_f_Well2();
    }
    private org.gwtbootstrap3.client.ui.Well build_f_Well2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Well f_Well2 = (org.gwtbootstrap3.client.ui.Well) GWT.create(org.gwtbootstrap3.client.ui.Well.class);
      // Setup section.
      f_Well2.add(get_f_Div3());
      f_Well2.add(get_message());

      return f_Well2;
    }

    /**
     * Getter for f_Div3 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.html.Div get_f_Div3() {
      return build_f_Div3();
    }
    private org.gwtbootstrap3.client.ui.html.Div build_f_Div3() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.html.Div f_Div3 = (org.gwtbootstrap3.client.ui.html.Div) GWT.create(org.gwtbootstrap3.client.ui.html.Div.class);
      // Setup section.
      f_Div3.addStyleName("spinner");
      f_Div3.addStyleName("spinner-lg");
      f_Div3.setPull(org.gwtbootstrap3.client.ui.constants.Pull.LEFT);

      return f_Div3;
    }

    /**
     * Getter for message called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.html.Span get_message() {
      return build_message();
    }
    private org.gwtbootstrap3.client.ui.html.Span build_message() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.html.Span message = new org.gwtbootstrap3.client.ui.html.Span(template_html1().asString());
      // Setup section.

      this.owner.message = message;

      return message;
    }

    /**
     * Getter for domId0Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId0Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId0Element() {
      return domId0Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId0Element() {
      // Creation section.
      domId0Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId0());
      // Setup section.

      return domId0Element;
    }
  }
}
