// .ui.xml template last modified: 1597706469392
package com.google.gwt.user.client.ui;

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
import com.google.gwt.dom.client.Element;

public class NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl implements UiBinder<com.google.gwt.dom.client.Element, com.google.gwt.user.client.ui.NativeVerticalScrollbar>, com.google.gwt.user.client.ui.NativeVerticalScrollbar.NativeVerticalScrollbarUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div class='{0}'><div class='{1}' id='{2}'> <div id='{3}'></div> </div></div>")
    SafeHtml html1(String arg0, String arg1, String arg2, String arg3);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.dom.client.Element createAndBindUi(final com.google.gwt.user.client.ui.NativeVerticalScrollbar owner) {


    return new Widgets(owner).get_f_div1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final com.google.gwt.user.client.ui.NativeVerticalScrollbar owner;


    public Widgets(final com.google.gwt.user.client.ui.NativeVerticalScrollbar owner) {
      this.owner = owner;
      build_res();  // more than one getter call detected. Type: IMPORTED, precedence: 1
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1("" + get_res().nativeVerticalScrollbarUi().viewport() + "", "" + get_res().nativeVerticalScrollbarUi().scrollable() + "", get_domId0(), get_domId1());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private com.google.gwt.user.client.ui.NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final com.google.gwt.user.client.ui.NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (com.google.gwt.user.client.ui.NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl_GenBundle) GWT.create(com.google.gwt.user.client.ui.NativeVerticalScrollbar_NativeVerticalScrollbarUiBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for res called 2 times. Type: IMPORTED. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.NativeVerticalScrollbar.UiBinderBundle res;
    private com.google.gwt.user.client.ui.NativeVerticalScrollbar.UiBinderBundle get_res() {
      return res;
    }
    private com.google.gwt.user.client.ui.NativeVerticalScrollbar.UiBinderBundle build_res() {
      // Creation section.
      res = (com.google.gwt.user.client.ui.NativeVerticalScrollbar.UiBinderBundle) GWT.create(com.google.gwt.user.client.ui.NativeVerticalScrollbar.UiBinderBundle.class);
      // Setup section.

      return res;
    }

    /**
     * Getter for f_div1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.dom.client.DivElement get_f_div1() {
      return build_f_div1();
    }
    private com.google.gwt.dom.client.DivElement build_f_div1() {
      // Creation section.
      final com.google.gwt.dom.client.DivElement f_div1 = (com.google.gwt.dom.client.DivElement) UiBinderUtil.fromHtml(template_html1().asString());
      // Setup section.

      {
        // Attach section.
        UiBinderUtil.TempAttachment __attachRecord__ = UiBinderUtil.attachToDom(f_div1);

        get_scrollable();
        get_contentDiv();

        // Detach section.
        __attachRecord__.detach();
      }

      return f_div1;
    }

    /**
     * Getter for scrollable called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.dom.client.DivElement get_scrollable() {
      return build_scrollable();
    }
    private com.google.gwt.dom.client.DivElement build_scrollable() {
      // Creation section.
      final com.google.gwt.dom.client.DivElement scrollable = new com.google.gwt.uibinder.client.LazyDomElement(get_domId0()).get().cast();
      // Setup section.

      this.owner.scrollable = scrollable;

      return scrollable;
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
     * Getter for contentDiv called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.dom.client.DivElement get_contentDiv() {
      return build_contentDiv();
    }
    private com.google.gwt.dom.client.DivElement build_contentDiv() {
      // Creation section.
      final com.google.gwt.dom.client.DivElement contentDiv = new com.google.gwt.uibinder.client.LazyDomElement(get_domId1()).get().cast();
      // Setup section.

      this.owner.contentDiv = contentDiv;

      return contentDiv;
    }

    /**
     * Getter for domId1 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId1;
    private java.lang.String get_domId1() {
      return domId1;
    }
    private java.lang.String build_domId1() {
      // Creation section.
      domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.

      return domId1;
    }
  }
}
