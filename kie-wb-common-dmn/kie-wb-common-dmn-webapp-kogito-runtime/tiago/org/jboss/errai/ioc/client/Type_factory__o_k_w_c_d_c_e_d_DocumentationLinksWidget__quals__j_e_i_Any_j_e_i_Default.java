package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Named;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem;
import org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUriPopoverImpl;
import org.kie.workbench.common.dmn.client.editors.documentation.links.NameAndUrlPopoverView.Presenter;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.uberfire.client.mvp.LockRequiredEvent;

public class Type_factory__o_k_w_c_d_c_e_d_DocumentationLinksWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<DocumentationLinksWidget> { public interface o_k_w_c_d_c_e_d_DocumentationLinksWidgetTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinksWidget.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_d_DocumentationLinksWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DocumentationLinksWidget.class, "Type_factory__o_k_w_c_d_c_e_d_DocumentationLinksWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DocumentationLinksWidget.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, HasEnabled.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DocumentationLinksWidget.\"] .kie-documentation-links-container {\n  width: 100%;\n}\n[data-i18n-prefix=\"DocumentationLinksWidget.\"] .kie-documentation-links-container #add-container {\n  position: absolute;\n  top: -24px;\n  right: 0px;\n}\n[data-i18n-prefix=\"DocumentationLinksWidget.\"] .documentation-links-container {\n  height: 40px;\n  overflow-y: auto;\n  overflow-x: hidden;\n}\n[data-i18n-prefix=\"DocumentationLinksWidget.\"] .col-md-10 {\n  padding: 0;\n}\n[data-i18n-prefix=\"DocumentationLinksWidget.\"] .no-link-set {\n  font-style: italic;\n  color: #777777;\n}\n[data-i18n-prefix=\"DocumentationLinksWidget.\"] .container {\n  height: auto;\n}\n[data-i18n-prefix=\"DocumentationLinksWidget.\"] .read-only {\n  cursor: not-allowed;\n  pointer-events: none;\n  filter: grayscale(100%);\n}\n\n");
  }

  public DocumentationLinksWidget createInstance(final ContextManager contextManager) {
    final HTMLElement _noLink_8 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    final ManagedInstance<DocumentationLinkItem> _listItems_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DocumentationLinkItem.class }, new Annotation[] { });
    final CellEditorControlsView _cellEditor_6 = (CellEditorControlsViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final HTMLDivElement _noneContainer_3 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _addButton_4 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final Presenter _nameAndUrlPopover_5 = (NameAndUriPopoverImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_l_NameAndUriPopoverImpl__quals__j_e_i_Any_j_e_i_Default");
    final ReadOnlyProvider _readOnlyProvider_10 = (ReadOnlyProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final Event<LockRequiredEvent> _locker_9 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LockRequiredEvent.class }, new Annotation[] { });
    final HTMLElement _addLink_7 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    final HTMLDivElement _linksContainer_2 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DocumentationLinksWidget instance = new DocumentationLinksWidget(_listItems_0, _translationService_1, _linksContainer_2, _noneContainer_3, _addButton_4, _nameAndUrlPopover_5, _cellEditor_6, _addLink_7, _noLink_8, _locker_9, _readOnlyProvider_10);
    registerDependentScopedReference(instance, _noLink_8);
    registerDependentScopedReference(instance, _listItems_0);
    registerDependentScopedReference(instance, _noneContainer_3);
    registerDependentScopedReference(instance, _addButton_4);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _locker_9);
    registerDependentScopedReference(instance, _addLink_7);
    registerDependentScopedReference(instance, _linksContainer_2);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_d_DocumentationLinksWidgetTemplateResource templateForDocumentationLinksWidget = GWT.create(o_k_w_c_d_c_e_d_DocumentationLinksWidgetTemplateResource.class);
    Element parentElementForTemplateOfDocumentationLinksWidget = TemplateUtil.getRootTemplateParentElement(templateForDocumentationLinksWidget.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinksWidget.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinksWidget.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDocumentationLinksWidget));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDocumentationLinksWidget));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(5);
    dataFieldMetas.put("documentation-links-container", new DataFieldMeta());
    dataFieldMetas.put("none-container", new DataFieldMeta());
    dataFieldMetas.put("add-button", new DataFieldMeta());
    dataFieldMetas.put("add-link", new DataFieldMeta());
    dataFieldMetas.put("no-link", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget", "org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinksWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinksWidget_HTMLDivElement_linksContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "documentation-links-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget", "org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinksWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinksWidget_HTMLDivElement_noneContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "none-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget", "org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinksWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinksWidget_HTMLAnchorElement_addButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "add-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget", "org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinksWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinksWidget_HTMLElement_addLink(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "add-link");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget", "org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinksWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinksWidget_HTMLElement_noLink(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "no-link");
    templateFieldsMap.put("documentation-links-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinksWidget_HTMLDivElement_linksContainer(instance))));
    templateFieldsMap.put("none-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinksWidget_HTMLDivElement_noneContainer(instance))));
    templateFieldsMap.put("add-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinksWidget_HTMLAnchorElement_addButton(instance))));
    templateFieldsMap.put("add-link", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinksWidget_HTMLElement_addLink(instance))));
    templateFieldsMap.put("no-link", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinksWidget_HTMLElement_noLink(instance))));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDocumentationLinksWidget), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("add-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClickTypeButton(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DocumentationLinksWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final DocumentationLinksWidget instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  public void invokePostConstructs(final DocumentationLinksWidget instance) {
    instance.init();
  }

  native static HTMLDivElement DocumentationLinksWidget_HTMLDivElement_noneContainer(DocumentationLinksWidget instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget::noneContainer;
  }-*/;

  native static void DocumentationLinksWidget_HTMLDivElement_noneContainer(DocumentationLinksWidget instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget::noneContainer = value;
  }-*/;

  native static HTMLDivElement DocumentationLinksWidget_HTMLDivElement_linksContainer(DocumentationLinksWidget instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget::linksContainer;
  }-*/;

  native static void DocumentationLinksWidget_HTMLDivElement_linksContainer(DocumentationLinksWidget instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget::linksContainer = value;
  }-*/;

  native static HTMLElement DocumentationLinksWidget_HTMLElement_addLink(DocumentationLinksWidget instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget::addLink;
  }-*/;

  native static void DocumentationLinksWidget_HTMLElement_addLink(DocumentationLinksWidget instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget::addLink = value;
  }-*/;

  native static HTMLElement DocumentationLinksWidget_HTMLElement_noLink(DocumentationLinksWidget instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget::noLink;
  }-*/;

  native static void DocumentationLinksWidget_HTMLElement_noLink(DocumentationLinksWidget instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget::noLink = value;
  }-*/;

  native static HTMLAnchorElement DocumentationLinksWidget_HTMLAnchorElement_addButton(DocumentationLinksWidget instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget::addButton;
  }-*/;

  native static void DocumentationLinksWidget_HTMLAnchorElement_addButton(DocumentationLinksWidget instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget::addButton = value;
  }-*/;
}