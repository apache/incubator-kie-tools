// .ui.xml template last modified: 1607102263361
package org.kie.workbench.common.widgets.client.handlers.workbench.configuration;

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

public class WorkbenchConfigurationViewImpl_WorkbenchConfigurationViewBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl>, org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl.WorkbenchConfigurationViewBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("")
    SafeHtml html1();
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl owner) {


    return new Widgets(owner).get_view();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl owner;


    public Widgets(final org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl owner) {
      this.owner = owner;
    }

    SafeHtml template_html1() {
      return template.html1();
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl_WorkbenchConfigurationViewBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl_WorkbenchConfigurationViewBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl_WorkbenchConfigurationViewBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl_WorkbenchConfigurationViewBinderImpl_GenBundle) GWT.create(org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl_WorkbenchConfigurationViewBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for view called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_view() {
      return build_view();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_view() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel view = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.

      this.owner.view = view;

      return view;
    }
  }
}
