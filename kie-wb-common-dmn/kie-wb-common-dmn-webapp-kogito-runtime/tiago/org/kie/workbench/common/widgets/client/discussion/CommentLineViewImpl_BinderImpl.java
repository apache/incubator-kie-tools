// .ui.xml template last modified: 1607096134014
package org.kie.workbench.common.widgets.client.discussion;

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

public class CommentLineViewImpl_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl>, org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl.Binder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div class='{0}'> <span id='{1}'></span> </div> <div class='{2}'> <span id='{3}'></span> </div> <div class='{4}'> <span id='{5}'></span> </div>")
    SafeHtml html1(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl owner) {


    return new Widgets(owner).get_base();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl owner;


    public Widgets(final org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId2();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_domId1Element();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_domId2Element();  // more than one getter call detected. Type: DEFAULT, precedence: 3
    }

    SafeHtml template_html1() {
      return template.html1("" + get_style().author() + "", get_domId0(), "" + get_style().comment() + "", get_domId1(), "" + get_style().date() + "", get_domId2());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenBundle) GWT.create(org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for style called 4 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenCss_style style;
    private org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenCss_style get_style() {
      return style;
    }
    private org.kie.workbench.common.widgets.client.discussion.CommentLineViewImpl_BinderImpl_GenCss_style build_style() {
      // Creation section.
      style = get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay().style();
      // Setup section.
      style.ensureInjected();

      return style;
    }

    /**
     * Getter for base called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.FocusPanel get_base() {
      return build_base();
    }
    private com.google.gwt.user.client.ui.FocusPanel build_base() {
      // Creation section.
      final com.google.gwt.user.client.ui.FocusPanel base = (com.google.gwt.user.client.ui.FocusPanel) GWT.create(com.google.gwt.user.client.ui.FocusPanel.class);
      // Setup section.
      base.add(get_f_HTMLPanel1());

      return base;
    }

    /**
     * Getter for f_HTMLPanel1 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel1() {
      return build_f_HTMLPanel1();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.
      f_HTMLPanel1.addStyleName("" + get_style().general() + "");

      {
        // Attach section.
        UiBinderUtil.TempAttachment __attachRecord__ = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());

        get_domId0Element().get();
        get_domId1Element().get();
        get_domId2Element().get();

        // Detach section.
        __attachRecord__.detach();
      }
      f_HTMLPanel1.addAndReplaceElement(get_author(), get_domId0Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_comment(), get_domId1Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_date(), get_domId2Element().get());

      return f_HTMLPanel1;
    }

    /**
     * Getter for domId0 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
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
     * Getter for author called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.Label get_author() {
      return build_author();
    }
    private com.google.gwt.user.client.ui.Label build_author() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label author = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.

      this.owner.author = author;

      return author;
    }

    /**
     * Getter for domId0Element called 2 times. Type: DEFAULT. Build precedence: 3.
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
     * Getter for domId1 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
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
     * Getter for comment called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.Label get_comment() {
      return build_comment();
    }
    private com.google.gwt.user.client.ui.Label build_comment() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label comment = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.

      this.owner.comment = comment;

      return comment;
    }

    /**
     * Getter for domId1Element called 2 times. Type: DEFAULT. Build precedence: 3.
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

    /**
     * Getter for domId2 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
     */
    private java.lang.String domId2;
    private java.lang.String get_domId2() {
      return domId2;
    }
    private java.lang.String build_domId2() {
      // Creation section.
      domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.

      return domId2;
    }

    /**
     * Getter for date called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.Label get_date() {
      return build_date();
    }
    private com.google.gwt.user.client.ui.Label build_date() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label date = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.

      this.owner.date = date;

      return date;
    }

    /**
     * Getter for domId2Element called 2 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId2Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId2Element() {
      return domId2Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId2Element() {
      // Creation section.
      domId2Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId2());
      // Setup section.

      return domId2Element;
    }
  }
}
