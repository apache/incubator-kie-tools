// .ui.xml template last modified: 1607092935139
package org.uberfire.ext.properties.editor.client;

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

public class PropertyEditorWidget_MyUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.uberfire.ext.properties.editor.client.PropertyEditorWidget>, org.uberfire.ext.properties.editor.client.PropertyEditorWidget.MyUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span id='{0}'></span> <span id='{1}'></span>")
    SafeHtml html1(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.uberfire.ext.properties.editor.client.PropertyEditorWidget owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.ext.properties.editor.client.PropertyEditorWidget owner;


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.onReload((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    final com.google.gwt.event.dom.client.KeyUpHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.KeyUpHandler() {
      public void onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent event) {
        owner.onKeyUp((com.google.gwt.event.dom.client.KeyUpEvent) event);
      }
    };

    public Widgets(final org.uberfire.ext.properties.editor.client.PropertyEditorWidget owner) {
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
    private org.uberfire.ext.properties.editor.client.PropertyEditorWidget_MyUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.ext.properties.editor.client.PropertyEditorWidget_MyUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.ext.properties.editor.client.PropertyEditorWidget_MyUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.ext.properties.editor.client.PropertyEditorWidget_MyUiBinderImpl_GenBundle) GWT.create(org.uberfire.ext.properties.editor.client.PropertyEditorWidget_MyUiBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for i18n called 1 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.ext.properties.editor.client.resources.i18n.CommonConstants get_i18n() {
      return build_i18n();
    }
    private org.uberfire.ext.properties.editor.client.resources.i18n.CommonConstants build_i18n() {
      // Creation section.
      final org.uberfire.ext.properties.editor.client.resources.i18n.CommonConstants i18n = (org.uberfire.ext.properties.editor.client.resources.i18n.CommonConstants) GWT.create(org.uberfire.ext.properties.editor.client.resources.i18n.CommonConstants.class);
      // Setup section.

      return i18n;
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

      {
        // Attach section.
        UiBinderUtil.TempAttachment __attachRecord__ = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());

        get_domId0Element().get();
        get_domId1Element().get();

        // Detach section.
        __attachRecord__.detach();
      }
      f_HTMLPanel1.addAndReplaceElement(get_filterGroup(), get_domId0Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_propertyMenu(), get_domId1Element().get());

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
     * Getter for filterGroup called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.InputGroup get_filterGroup() {
      return build_filterGroup();
    }
    private org.gwtbootstrap3.client.ui.InputGroup build_filterGroup() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.InputGroup filterGroup = (org.gwtbootstrap3.client.ui.InputGroup) GWT.create(org.gwtbootstrap3.client.ui.InputGroup.class);
      // Setup section.
      filterGroup.add(get_filterBox());
      filterGroup.add(get_f_InputGroupButton2());

      this.owner.filterGroup = filterGroup;

      return filterGroup;
    }

    /**
     * Getter for filterBox called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.TextBox get_filterBox() {
      return build_filterBox();
    }
    private org.gwtbootstrap3.client.ui.TextBox build_filterBox() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TextBox filterBox = (org.gwtbootstrap3.client.ui.TextBox) GWT.create(org.gwtbootstrap3.client.ui.TextBox.class);
      // Setup section.
      filterBox.setPlaceholder("" + get_i18n().FilterProperties() + "");
      filterBox.addKeyUpHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

      this.owner.filterBox = filterBox;

      return filterBox;
    }

    /**
     * Getter for f_InputGroupButton2 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.InputGroupButton get_f_InputGroupButton2() {
      return build_f_InputGroupButton2();
    }
    private org.gwtbootstrap3.client.ui.InputGroupButton build_f_InputGroupButton2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.InputGroupButton f_InputGroupButton2 = (org.gwtbootstrap3.client.ui.InputGroupButton) GWT.create(org.gwtbootstrap3.client.ui.InputGroupButton.class);
      // Setup section.
      f_InputGroupButton2.add(get_reload());

      return f_InputGroupButton2;
    }

    /**
     * Getter for reload called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.Button get_reload() {
      return build_reload();
    }
    private org.gwtbootstrap3.client.ui.Button build_reload() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Button reload = (org.gwtbootstrap3.client.ui.Button) GWT.create(org.gwtbootstrap3.client.ui.Button.class);
      // Setup section.
      reload.setIcon(org.gwtbootstrap3.client.ui.constants.IconType.RETWEET);
      reload.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

      this.owner.reload = reload;

      return reload;
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
     * Getter for propertyMenu called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.PanelGroup get_propertyMenu() {
      return build_propertyMenu();
    }
    private org.gwtbootstrap3.client.ui.PanelGroup build_propertyMenu() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.PanelGroup propertyMenu = (org.gwtbootstrap3.client.ui.PanelGroup) GWT.create(org.gwtbootstrap3.client.ui.PanelGroup.class);
      // Setup section.

      this.owner.propertyMenu = propertyMenu;

      return propertyMenu;
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
