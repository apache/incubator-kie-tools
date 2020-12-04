// .ui.xml template last modified: 1607096134014
package org.kie.workbench.common.widgets.client.handlers.workbench.configuration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget>, org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget.ConfigurationItemWidgetBinder {


  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget owner) {


    return new Widgets(owner).get_f_HorizontalPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget owner;


    public Widgets(final org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget owner) {
      this.owner = owner;
      build_style();  // generated css resource must be always created. Type: GENERATED_CSS. Precedence: 1
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 1 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenBundle) GWT.create(org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenBundle.class);
      // Setup section.

      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for style called 3 times. Type: GENERATED_CSS. Build precedence: 1.
     */
    private org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenCss_style style;
    private org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenCss_style get_style() {
      return style;
    }
    private org.kie.workbench.common.widgets.client.handlers.workbench.configuration.ConfigurationComboBoxItemWidget_ConfigurationItemWidgetBinderImpl_GenCss_style build_style() {
      // Creation section.
      style = get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay().style();
      // Setup section.
      style.ensureInjected();

      return style;
    }

    /**
     * Getter for f_HorizontalPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.HorizontalPanel get_f_HorizontalPanel1() {
      return build_f_HorizontalPanel1();
    }
    private com.google.gwt.user.client.ui.HorizontalPanel build_f_HorizontalPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);
      // Setup section.
      f_HorizontalPanel1.add(get_f_FlowPanel2());
      f_HorizontalPanel1.add(get_f_FlowPanel3());
      f_HorizontalPanel1.addStyleName("" + get_style().horizontalContainer() + "");

      return f_HorizontalPanel1;
    }

    /**
     * Getter for f_FlowPanel2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_f_FlowPanel2() {
      return build_f_FlowPanel2();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_f_FlowPanel2() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel f_FlowPanel2 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      f_FlowPanel2.add(get_extensionItemLabel());
      f_FlowPanel2.addStyleName("" + get_style().right() + "");

      return f_FlowPanel2;
    }

    /**
     * Getter for extensionItemLabel called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel get_extensionItemLabel() {
      return build_extensionItemLabel();
    }
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel build_extensionItemLabel() {
      // Creation section.
      final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel extensionItemLabel = (org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel) GWT.create(org.uberfire.ext.properties.editor.client.widgets.PropertyEditorItemLabel.class);
      // Setup section.
      extensionItemLabel.setWidth("200px");

      this.owner.extensionItemLabel = extensionItemLabel;

      return extensionItemLabel;
    }

    /**
     * Getter for f_FlowPanel3 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.FlowPanel get_f_FlowPanel3() {
      return build_f_FlowPanel3();
    }
    private com.google.gwt.user.client.ui.FlowPanel build_f_FlowPanel3() {
      // Creation section.
      final com.google.gwt.user.client.ui.FlowPanel f_FlowPanel3 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
      // Setup section.
      f_FlowPanel3.add(get_extensionItem());
      f_FlowPanel3.addStyleName("" + get_style().center() + "");

      return f_FlowPanel3;
    }

    /**
     * Getter for extensionItem called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox get_extensionItem() {
      return build_extensionItem();
    }
    private org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox build_extensionItem() {
      // Creation section.
      final org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox extensionItem = (org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox) GWT.create(org.uberfire.ext.properties.editor.client.widgets.PropertyEditorComboBox.class);
      // Setup section.
      extensionItem.setWidth("200px");

      this.owner.extensionItem = extensionItem;

      return extensionItem;
    }
  }
}
