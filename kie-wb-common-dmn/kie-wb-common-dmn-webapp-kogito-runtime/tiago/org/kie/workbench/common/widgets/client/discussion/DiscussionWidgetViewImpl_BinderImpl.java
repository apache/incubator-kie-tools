// .ui.xml template last modified: 1607102263361
package org.kie.workbench.common.widgets.client.discussion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class DiscussionWidgetViewImpl_BinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl>, org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl.Binder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl owner) {


    return new Widgets(owner).get_f_Container1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl owner;


    final com.google.gwt.event.dom.client.KeyUpHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.KeyUpHandler() {
      public void onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent event) {
        owner.onCommentBoxEnter((com.google.gwt.event.dom.client.KeyUpEvent) event);
      }
    };

    public Widgets(final org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenBundle) GWT.create(org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenBundle.class);
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
     * Getter for style called 3 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenCss_style style;
    private org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenCss_style get_style() {
      return style;
    }
    private org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetViewImpl_BinderImpl_GenCss_style build_style() {
      // Creation section.
      style = get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay().style();
      // Setup section.
      style.ensureInjected();

      return style;
    }

    /**
     * Getter for f_Container1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private org.gwtbootstrap3.client.ui.Container get_f_Container1() {
      return build_f_Container1();
    }
    private org.gwtbootstrap3.client.ui.Container build_f_Container1() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Container f_Container1 = (org.gwtbootstrap3.client.ui.Container) GWT.create(org.gwtbootstrap3.client.ui.Container.class);
      // Setup section.
      f_Container1.add(get_f_Row2());
      f_Container1.add(get_f_Row4());
      f_Container1.add(get_f_Row6());
      f_Container1.addStyleName("well");
      f_Container1.setFluid(true);

      return f_Container1;
    }

    /**
     * Getter for f_Row2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Row get_f_Row2() {
      return build_f_Row2();
    }
    private org.gwtbootstrap3.client.ui.Row build_f_Row2() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Row f_Row2 = (org.gwtbootstrap3.client.ui.Row) GWT.create(org.gwtbootstrap3.client.ui.Row.class);
      // Setup section.
      f_Row2.add(get_f_Heading3());

      return f_Row2;
    }

    /**
     * Getter for f_Heading3 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Heading get_f_Heading3() {
      return build_f_Heading3();
    }
    private org.gwtbootstrap3.client.ui.Heading build_f_Heading3() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Heading f_Heading3 = new org.gwtbootstrap3.client.ui.Heading(org.gwtbootstrap3.client.ui.constants.HeadingSize.H4);
      // Setup section.
      f_Heading3.addStyleName("col-md-12");
      f_Heading3.addStyleName("" + get_style().textAlign() + "");
      f_Heading3.setText("" + get_i18n().Comments() + "");

      return f_Heading3;
    }

    /**
     * Getter for f_Row4 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Row get_f_Row4() {
      return build_f_Row4();
    }
    private org.gwtbootstrap3.client.ui.Row build_f_Row4() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Row f_Row4 = (org.gwtbootstrap3.client.ui.Row) GWT.create(org.gwtbootstrap3.client.ui.Row.class);
      // Setup section.
      f_Row4.add(get_f_Column5());

      return f_Row4;
    }

    /**
     * Getter for f_Column5 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column5() {
      return build_f_Column5();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column5() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column5 = new org.gwtbootstrap3.client.ui.Column("MD_12");
      // Setup section.
      f_Column5.add(get_commentScroll());

      return f_Column5;
    }

    /**
     * Getter for commentScroll called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.user.client.ui.ScrollPanel get_commentScroll() {
      return build_commentScroll();
    }
    private com.google.gwt.user.client.ui.ScrollPanel build_commentScroll() {
      // Creation section.
      final com.google.gwt.user.client.ui.ScrollPanel commentScroll = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
      // Setup section.
      commentScroll.add(get_lines());
      commentScroll.addStyleName("" + get_style().commentScroll() + "");

      this.owner.commentScroll = commentScroll;

      return commentScroll;
    }

    /**
     * Getter for lines called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private com.google.gwt.user.client.ui.VerticalPanel get_lines() {
      return build_lines();
    }
    private com.google.gwt.user.client.ui.VerticalPanel build_lines() {
      // Creation section.
      final com.google.gwt.user.client.ui.VerticalPanel lines = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
      // Setup section.

      this.owner.lines = lines;

      return lines;
    }

    /**
     * Getter for f_Row6 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private org.gwtbootstrap3.client.ui.Row get_f_Row6() {
      return build_f_Row6();
    }
    private org.gwtbootstrap3.client.ui.Row build_f_Row6() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Row f_Row6 = (org.gwtbootstrap3.client.ui.Row) GWT.create(org.gwtbootstrap3.client.ui.Row.class);
      // Setup section.
      f_Row6.add(get_f_Column7());

      return f_Row6;
    }

    /**
     * Getter for f_Column7 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Column get_f_Column7() {
      return build_f_Column7();
    }
    private org.gwtbootstrap3.client.ui.Column build_f_Column7() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Column f_Column7 = new org.gwtbootstrap3.client.ui.Column("MD_12");
      // Setup section.
      f_Column7.add(get_textBox());
      f_Column7.addStyleName("" + get_style().comment() + "");

      return f_Column7;
    }

    /**
     * Getter for textBox called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private org.gwtbootstrap3.client.ui.TextArea get_textBox() {
      return build_textBox();
    }
    private org.gwtbootstrap3.client.ui.TextArea build_textBox() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.TextArea textBox = (org.gwtbootstrap3.client.ui.TextArea) GWT.create(org.gwtbootstrap3.client.ui.TextArea.class);
      // Setup section.
      textBox.addKeyUpHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

      this.owner.textBox = textBox;

      return textBox;
    }
  }
}
