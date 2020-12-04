// .ui.xml template last modified: 1607097769903
package org.uberfire.client.views.pfly.toolbar;

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
import com.google.gwt.user.client.ui.Panel;

public class WorkbenchToolBarView_WorkbenchToolBarViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Panel, org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView>, org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView.WorkbenchToolBarViewBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("⋮")
    SafeHtml html1();
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.Panel createAndBindUi(final org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView owner) {


    return new Widgets(owner).get_masterContainer();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView owner;


    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.handleClick((com.google.gwt.event.dom.client.ClickEvent) event);
      }
    };

    public Widgets(final org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
    }

    SafeHtml template_html1() {
      return template.html1();
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView_WorkbenchToolBarViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView_WorkbenchToolBarViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView_WorkbenchToolBarViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView_WorkbenchToolBarViewBinderImpl_GenBundle) GWT.create(org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView_WorkbenchToolBarViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for res called 1 times. Type: IMPORTED. Build precedence: 1.
     */
    private org.uberfire.client.resources.WorkbenchResources get_res() {
      return build_res();
    }
    private org.uberfire.client.resources.WorkbenchResources build_res() {
      // Creation section.
      final org.uberfire.client.resources.WorkbenchResources res = (org.uberfire.client.resources.WorkbenchResources) GWT.create(org.uberfire.client.resources.WorkbenchResources.class);
      // Setup section.

      return res;
    }

    /**
     * Getter for style called 4 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView_WorkbenchToolBarViewBinderImpl_GenCss_style style;
    private org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView_WorkbenchToolBarViewBinderImpl_GenCss_style get_style() {
      return style;
    }
    private org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView_WorkbenchToolBarViewBinderImpl_GenCss_style build_style() {
      // Creation section.
      style = get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay().style();
      // Setup section.
      style.ensureInjected();

      return style;
    }

    /**
     * Getter for masterContainer called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_masterContainer() {
      return build_masterContainer();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_masterContainer() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel masterContainer = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      masterContainer.add(get_container());
      masterContainer.add(get_simpleMargin());

      this.owner.masterContainer = masterContainer;

      return masterContainer;
    }

    /**
     * Getter for container called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_container() {
      return build_container();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_container() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel container = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      container.add(get_toolBar());
      container.add(get_tip());
      container.addStyleName("" + get_style().container() + "");
      container.addStyleName("" + get_res().CSS().toolbar() + "");

      this.owner.container = container;

      return container;
    }

    /**
     * Getter for toolBar called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.ButtonToolBar get_toolBar() {
      return build_toolBar();
    }
    private org.gwtbootstrap3.client.ui.ButtonToolBar build_toolBar() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.ButtonToolBar toolBar = (org.gwtbootstrap3.client.ui.ButtonToolBar) GWT.create(org.gwtbootstrap3.client.ui.ButtonToolBar.class);
      // Setup section.
      toolBar.addStyleName("" + get_style().reset_toolbar_margin() + "");

      this.owner.toolBar = toolBar;

      return toolBar;
    }

    /**
     * Getter for tip called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.gwtbootstrap3.client.ui.Tooltip get_tip() {
      return build_tip();
    }
    private org.gwtbootstrap3.client.ui.Tooltip build_tip() {
      // Creation section.
      final org.gwtbootstrap3.client.ui.Tooltip tip = (org.gwtbootstrap3.client.ui.Tooltip) GWT.create(org.gwtbootstrap3.client.ui.Tooltip.class);
      // Setup section.
      tip.add(get_viewControl());

      this.owner.tip = tip;

      return tip;
    }

    /**
     * Getter for viewControl called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.user.client.ui.Button get_viewControl() {
      return build_viewControl();
    }
    private com.google.gwt.user.client.ui.Button build_viewControl() {
      // Creation section.
      final com.google.gwt.user.client.ui.Button viewControl = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
      // Setup section.
      viewControl.setHTML(template_html1().asString());
      viewControl.addStyleName("" + get_style().expand() + "");
      viewControl.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

      this.owner.viewControl = viewControl;

      return viewControl;
    }

    /**
     * Getter for simpleMargin called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.SimplePanel get_simpleMargin() {
      return build_simpleMargin();
    }
    private com.google.gwt.user.client.ui.SimplePanel build_simpleMargin() {
      // Creation section.
      final com.google.gwt.user.client.ui.SimplePanel simpleMargin = (com.google.gwt.user.client.ui.SimplePanel) GWT.create(com.google.gwt.user.client.ui.SimplePanel.class);
      // Setup section.
      simpleMargin.addStyleName("" + get_style().margin() + "");

      this.owner.simpleMargin = simpleMargin;

      return simpleMargin;
    }
  }
}
