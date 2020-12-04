package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_d_DocumentationLinkItem__quals__j_e_i_Any_j_e_i_Default extends Factory<DocumentationLinkItem> { public interface o_k_w_c_d_c_e_d_DocumentationLinkItemTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinkItem.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_d_DocumentationLinkItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DocumentationLinkItem.class, "Type_factory__o_k_w_c_d_c_e_d_DocumentationLinkItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DocumentationLinkItem.class, Object.class, UberElemental.class, IsElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DocumentationLinkItem.\"] .documentation-links {\n  height: 60px;\n  overflow-y: scroll;\n  overflow-x: hidden;\n}\n[data-i18n-prefix=\"DocumentationLinkItem.\"] .col-md-10 {\n  padding: 0;\n}\n[data-i18n-prefix=\"DocumentationLinkItem.\"] .delete-link {\n  color: #777777;\n}\n\n");
  }

  public DocumentationLinkItem createInstance(final ContextManager contextManager) {
    final HTMLAnchorElement _deleteLink_2 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _item_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _link_1 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DocumentationLinkItem instance = new DocumentationLinkItem(_item_0, _link_1, _deleteLink_2);
    registerDependentScopedReference(instance, _deleteLink_2);
    registerDependentScopedReference(instance, _item_0);
    registerDependentScopedReference(instance, _link_1);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_d_DocumentationLinkItemTemplateResource templateForDocumentationLinkItem = GWT.create(o_k_w_c_d_c_e_d_DocumentationLinkItemTemplateResource.class);
    Element parentElementForTemplateOfDocumentationLinkItem = TemplateUtil.getRootTemplateParentElement(templateForDocumentationLinkItem.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinkItem.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinkItem.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDocumentationLinkItem));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDocumentationLinkItem));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("item", new DataFieldMeta());
    dataFieldMetas.put("documentationLink", new DataFieldMeta());
    dataFieldMetas.put("deleteLink", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem", "org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinkItem.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinkItem_HTMLDivElement_item(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem", "org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinkItem.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinkItem_HTMLAnchorElement_link(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "documentationLink");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem", "org/kie/workbench/common/dmn/client/editors/documentation/DocumentationLinkItem.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinkItem_HTMLAnchorElement_deleteLink(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "deleteLink");
    templateFieldsMap.put("item", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinkItem_HTMLDivElement_item(instance))));
    templateFieldsMap.put("documentationLink", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinkItem_HTMLAnchorElement_link(instance))));
    templateFieldsMap.put("deleteLink", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DocumentationLinkItem_HTMLAnchorElement_deleteLink(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDocumentationLinkItem), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("deleteLink"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onDeleteLinkClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DocumentationLinkItem) instance, contextManager);
  }

  public void destroyInstanceHelper(final DocumentationLinkItem instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLAnchorElement DocumentationLinkItem_HTMLAnchorElement_link(DocumentationLinkItem instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem::link;
  }-*/;

  native static void DocumentationLinkItem_HTMLAnchorElement_link(DocumentationLinkItem instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem::link = value;
  }-*/;

  native static HTMLAnchorElement DocumentationLinkItem_HTMLAnchorElement_deleteLink(DocumentationLinkItem instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem::deleteLink;
  }-*/;

  native static void DocumentationLinkItem_HTMLAnchorElement_deleteLink(DocumentationLinkItem instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem::deleteLink = value;
  }-*/;

  native static HTMLDivElement DocumentationLinkItem_HTMLDivElement_item(DocumentationLinkItem instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem::item;
  }-*/;

  native static void DocumentationLinkItem_HTMLDivElement_item(DocumentationLinkItem instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinkItem::item = value;
  }-*/;
}