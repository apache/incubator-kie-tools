package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationService;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationServiceImpl;
import org.kie.workbench.common.dmn.client.editors.documentation.common.HTMLDownloadHelper;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation.DMNDocumentationViewButtonsVisibilitySupplier;
import org.kie.workbench.common.stunner.core.client.util.PrintHelper;
import org.kie.workbench.common.stunner.core.documentation.DefaultDiagramDocumentationView;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;

public class Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationView__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNDocumentationView> { public interface o_k_w_c_d_c_e_d_DMNDocumentationViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/documentation/DMNDocumentationView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationView__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNDocumentationView.class, "Type_factory__o_k_w_c_d_c_e_d_DMNDocumentationView__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDocumentationView.class, DefaultDiagramDocumentationView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, DocumentationView.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DMNDocumentationView.\"] {\n  background-color: #f5f5f5;\n  padding: 30px;\n}\n[data-i18n-prefix=\"DMNDocumentationView.\"] .documentation-panel {\n  margin: 0 auto;\n  max-width: 1000px;\n  min-height: 1500px;\n  background: #FFF;\n  box-shadow: 0 15px 10px #AAA;\n}\n[data-i18n-prefix=\"DMNDocumentationView.\"] .documentation-header {\n  position: relative;\n}\n[data-i18n-prefix=\"DMNDocumentationView.\"] .documentation-header button {\n  position: absolute;\n  top: 10px;\n  padding: 3px 13px;\n  margin-left: 15px;\n  left: unset;\n}\n@media (min-width: 2100px) {\n  [data-i18n-prefix=\"DMNDocumentationView.\"] .documentation-header button {\n    top: 0;\n    left: 100%;\n    right: unset;\n  }\n  [data-i18n-prefix=\"DMNDocumentationView.\"] .documentation-header button.download-html-button {\n    top: 40px;\n  }\n}\n[data-i18n-prefix=\"DMNDocumentationView.\"] .documentation-header button i {\n  margin-right: 2px;\n}\n[data-i18n-prefix=\"DMNDocumentationView.\"] .documentation-header button.download-html-button {\n  right: 10px;\n}\n[data-i18n-prefix=\"DMNDocumentationView.\"] .documentation-header button.print-button {\n  right: 180px;\n}\n\n");
  }

  public DMNDocumentationView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _documentationContent_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _printButton_2 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDownloadHelper _downloadHelper_6 = (HTMLDownloadHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_c_HTMLDownloadHelper__quals__j_e_i_Any_j_e_i_Default");
    final PrintHelper _printHelper_4 = (PrintHelper) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_u_PrintHelper__quals__j_e_i_Any_j_e_i_Default");
    final HTMLButtonElement _downloadHtmlFile_3 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _documentationPanel_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationViewButtonsVisibilitySupplier _buttonsVisibilitySupplier_7 = (DMNDocumentationViewButtonsVisibilitySupplier) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_e_d_DMNDocumentationViewButtonsVisibilitySupplier__quals__j_e_i_Any_j_e_i_Default");
    final DMNDocumentationService _documentationService_5 = (DMNDocumentationServiceImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    final DMNDocumentationView instance = new DMNDocumentationView(_documentationPanel_0, _documentationContent_1, _printButton_2, _downloadHtmlFile_3, _printHelper_4, _documentationService_5, _downloadHelper_6, _buttonsVisibilitySupplier_7);
    registerDependentScopedReference(instance, _documentationContent_1);
    registerDependentScopedReference(instance, _printButton_2);
    registerDependentScopedReference(instance, _downloadHelper_6);
    registerDependentScopedReference(instance, _printHelper_4);
    registerDependentScopedReference(instance, _downloadHtmlFile_3);
    registerDependentScopedReference(instance, _documentationPanel_0);
    registerDependentScopedReference(instance, _documentationService_5);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_d_DMNDocumentationViewTemplateResource templateForDMNDocumentationView = GWT.create(o_k_w_c_d_c_e_d_DMNDocumentationViewTemplateResource.class);
    Element parentElementForTemplateOfDMNDocumentationView = TemplateUtil.getRootTemplateParentElement(templateForDMNDocumentationView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/documentation/DMNDocumentationView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/documentation/DMNDocumentationView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNDocumentationView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNDocumentationView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("documentation-panel", new DataFieldMeta());
    dataFieldMetas.put("documentation-content", new DataFieldMeta());
    dataFieldMetas.put("print-button", new DataFieldMeta());
    dataFieldMetas.put("download-html-file", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView", "org/kie/workbench/common/dmn/client/editors/documentation/DMNDocumentationView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNDocumentationView_HTMLDivElement_documentationPanel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "documentation-panel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView", "org/kie/workbench/common/dmn/client/editors/documentation/DMNDocumentationView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNDocumentationView_HTMLDivElement_documentationContent(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "documentation-content");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView", "org/kie/workbench/common/dmn/client/editors/documentation/DMNDocumentationView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNDocumentationView_HTMLButtonElement_printButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "print-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView", "org/kie/workbench/common/dmn/client/editors/documentation/DMNDocumentationView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNDocumentationView_HTMLButtonElement_downloadHtmlFile(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "download-html-file");
    templateFieldsMap.put("documentation-panel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNDocumentationView_HTMLDivElement_documentationPanel(instance))));
    templateFieldsMap.put("documentation-content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNDocumentationView_HTMLDivElement_documentationContent(instance))));
    templateFieldsMap.put("print-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNDocumentationView_HTMLButtonElement_printButton(instance))));
    templateFieldsMap.put("download-html-file", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNDocumentationView_HTMLButtonElement_downloadHtmlFile(instance))));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNDocumentationView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("print-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onPrintButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("download-html-file"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onDownloadHtmlFile(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DMNDocumentationView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DMNDocumentationView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static HTMLDivElement DMNDocumentationView_HTMLDivElement_documentationContent(DMNDocumentationView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView::documentationContent;
  }-*/;

  native static void DMNDocumentationView_HTMLDivElement_documentationContent(DMNDocumentationView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView::documentationContent = value;
  }-*/;

  native static HTMLButtonElement DMNDocumentationView_HTMLButtonElement_downloadHtmlFile(DMNDocumentationView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView::downloadHtmlFile;
  }-*/;

  native static void DMNDocumentationView_HTMLButtonElement_downloadHtmlFile(DMNDocumentationView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView::downloadHtmlFile = value;
  }-*/;

  native static HTMLButtonElement DMNDocumentationView_HTMLButtonElement_printButton(DMNDocumentationView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView::printButton;
  }-*/;

  native static void DMNDocumentationView_HTMLButtonElement_printButton(DMNDocumentationView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView::printButton = value;
  }-*/;

  native static HTMLDivElement DMNDocumentationView_HTMLDivElement_documentationPanel(DMNDocumentationView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView::documentationPanel;
  }-*/;

  native static void DMNDocumentationView_HTMLDivElement_documentationPanel(DMNDocumentationView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DMNDocumentationView::documentationPanel = value;
  }-*/;
}