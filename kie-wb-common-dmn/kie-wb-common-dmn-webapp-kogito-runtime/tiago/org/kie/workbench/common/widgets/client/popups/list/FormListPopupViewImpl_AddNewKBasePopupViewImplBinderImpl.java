// .ui.xml template last modified: 1607095267966
package org.kie.workbench.common.widgets.client.popups.list;

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

public class FormListPopupViewImpl_AddNewKBasePopupViewImplBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl>, org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl.AddNewKBasePopupViewImplBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div style='float:left'> <div style='float:left;'> <span id='{0}'></span> </div> <div style='float:left;'> <span id='{1}'></span> </div> <div style='clear:both;'></div> </div>")
    SafeHtml html1(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl owner;


    public Widgets(final org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl owner) {
      this.owner = owner;
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId1Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId0(), get_domId1());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl_AddNewKBasePopupViewImplBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl_AddNewKBasePopupViewImplBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl_AddNewKBasePopupViewImplBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl_AddNewKBasePopupViewImplBinderImpl_GenBundle) GWT.create(org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl_AddNewKBasePopupViewImplBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 1 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants get_i18n() {
      return build_i18n();
    }
    private org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants build_i18n() {
      // Creation section.
      final org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants i18n = (org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants) GWT.create(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.class);
      // Setup section.

      return i18n;
    }

    /**
     * Getter for resources called 0 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.resources.CommonsResources get_resources() {
      return build_resources();
    }
    private org.kie.workbench.common.widgets.client.resources.CommonsResources build_resources() {
      // Creation section.
      final org.kie.workbench.common.widgets.client.resources.CommonsResources resources = (org.kie.workbench.common.widgets.client.resources.CommonsResources) GWT.create(org.kie.workbench.common.widgets.client.resources.CommonsResources.class);
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
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.
      f_HTMLPanel1.setWidth("100%");

      {
        // Attach section.
        UiBinderUtil.TempAttachment __attachRecord__ = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());

        get_domId0Element().get();
        get_domId1Element().get();

        // Detach section.
        __attachRecord__.detach();
      }
      f_HTMLPanel1.addAndReplaceElement(get_f_Label2(), get_domId0Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_listItems(), get_domId1Element().get());

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
     * Getter for f_Label2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.Label get_f_Label2() {
      return build_f_Label2();
    }
    private com.google.gwt.user.client.ui.Label build_f_Label2() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label f_Label2 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.
      f_Label2.setText("" + get_i18n().Name() + "");

      return f_Label2;
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

    /**
     * Getter for listItems called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.ListBox get_listItems() {
      return build_listItems();
    }
    private com.google.gwt.user.client.ui.ListBox build_listItems() {
      // Creation section.
      final com.google.gwt.user.client.ui.ListBox listItems = (com.google.gwt.user.client.ui.ListBox) GWT.create(com.google.gwt.user.client.ui.ListBox.class);
      // Setup section.

      this.owner.listItems = listItems;

      return listItems;
    }

    /**
     * Getter for domId1Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId1Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId1Element() {
      return domId1Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId1Element() {
      // Creation section.
      domId1Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId1());
      // Setup section.

      return domId1Element;
    }
  }
}
