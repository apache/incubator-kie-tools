package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
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
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItem.View;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeListItemView> { public interface o_k_w_c_d_c_e_t_i_t_TreeListItemViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListItemView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeListItemView.class, "Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeListItemView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"TreeListItemView.\"] .row {\n  margin: 0;\n}\n[data-i18n-prefix=\"TreeListItemView.\"] .row:hover {\n  background-color: #eff8ff;\n}\n[data-i18n-prefix=\"TreeListItemView.\"] .list-view-pf-expand {\n  padding: 0 0 0 10px;\n  margin: 0;\n  width: 30px;\n}\n[data-i18n-prefix=\"TreeListItemView.\"] .col-md-2 {\n  padding: 0;\n  margin: 0;\n  width: 20px;\n}\n[data-i18n-prefix=\"TreeListItemView.\"] .col-md-6 {\n  padding: 0 10px;\n}\n[data-i18n-prefix=\"TreeListItemView.\"] [data-field=\"item-details\"] {\n  padding-bottom: 0;\n  font-weight: 600;\n  color: #0088ce;\n}\n[data-i18n-prefix=\"TreeListItemView.\"] .container-fluid {\n  overflow: hidden;\n}\n\n");
  }

  public TreeListItemView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _expandContainer_3 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _itemRoot_6 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _itemDetails_2 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _itemsContainer_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _expand_4 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLInputElement _checkbox_5 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _itemHeader_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TreeListItemView instance = new TreeListItemView(_itemHeader_0, _itemsContainer_1, _itemDetails_2, _expandContainer_3, _expand_4, _checkbox_5, _itemRoot_6);
    registerDependentScopedReference(instance, _expandContainer_3);
    registerDependentScopedReference(instance, _itemRoot_6);
    registerDependentScopedReference(instance, _itemDetails_2);
    registerDependentScopedReference(instance, _itemsContainer_1);
    registerDependentScopedReference(instance, _expand_4);
    registerDependentScopedReference(instance, _checkbox_5);
    registerDependentScopedReference(instance, _itemHeader_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_i_t_TreeListItemViewTemplateResource templateForTreeListItemView = GWT.create(o_k_w_c_d_c_e_t_i_t_TreeListItemViewTemplateResource.class);
    Element parentElementForTemplateOfTreeListItemView = TemplateUtil.getRootTemplateParentElement(templateForTreeListItemView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListItemView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeListItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeListItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(7);
    dataFieldMetas.put("item-header", new DataFieldMeta());
    dataFieldMetas.put("items-container", new DataFieldMeta());
    dataFieldMetas.put("item-details", new DataFieldMeta());
    dataFieldMetas.put("item-root", new DataFieldMeta());
    dataFieldMetas.put("expand-container", new DataFieldMeta());
    dataFieldMetas.put("expand", new DataFieldMeta());
    dataFieldMetas.put("data-object-checkbox", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView", "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLDivElement_itemHeader(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item-header");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView", "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLDivElement_itemsContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "items-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView", "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLDivElement_itemDetails(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item-details");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView", "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLDivElement_itemRoot(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item-root");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView", "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLDivElement_expandContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "expand-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView", "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLElement_expand(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "expand");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView", "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLInputElement_checkbox(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "data-object-checkbox");
    templateFieldsMap.put("item-header", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLDivElement_itemHeader(instance))));
    templateFieldsMap.put("items-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLDivElement_itemsContainer(instance))));
    templateFieldsMap.put("item-details", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLDivElement_itemDetails(instance))));
    templateFieldsMap.put("item-root", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLDivElement_itemRoot(instance))));
    templateFieldsMap.put("expand-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLDivElement_expandContainer(instance))));
    templateFieldsMap.put("expand", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLElement_expand(instance))));
    templateFieldsMap.put("data-object-checkbox", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListItemView_HTMLInputElement_checkbox(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeListItemView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("item-header"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("data-object-checkbox"), new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        instance.onCheckboxChanged(event);
      }
    }, ChangeEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TreeListItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final TreeListItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement TreeListItemView_HTMLDivElement_itemRoot(TreeListItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::itemRoot;
  }-*/;

  native static void TreeListItemView_HTMLDivElement_itemRoot(TreeListItemView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::itemRoot = value;
  }-*/;

  native static HTMLDivElement TreeListItemView_HTMLDivElement_itemHeader(TreeListItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::itemHeader;
  }-*/;

  native static void TreeListItemView_HTMLDivElement_itemHeader(TreeListItemView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::itemHeader = value;
  }-*/;

  native static HTMLDivElement TreeListItemView_HTMLDivElement_itemsContainer(TreeListItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::itemsContainer;
  }-*/;

  native static void TreeListItemView_HTMLDivElement_itemsContainer(TreeListItemView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::itemsContainer = value;
  }-*/;

  native static HTMLDivElement TreeListItemView_HTMLDivElement_expandContainer(TreeListItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::expandContainer;
  }-*/;

  native static void TreeListItemView_HTMLDivElement_expandContainer(TreeListItemView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::expandContainer = value;
  }-*/;

  native static HTMLElement TreeListItemView_HTMLElement_expand(TreeListItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::expand;
  }-*/;

  native static void TreeListItemView_HTMLElement_expand(TreeListItemView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::expand = value;
  }-*/;

  native static HTMLDivElement TreeListItemView_HTMLDivElement_itemDetails(TreeListItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::itemDetails;
  }-*/;

  native static void TreeListItemView_HTMLDivElement_itemDetails(TreeListItemView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::itemDetails = value;
  }-*/;

  native static HTMLInputElement TreeListItemView_HTMLInputElement_checkbox(TreeListItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::checkbox;
  }-*/;

  native static void TreeListItemView_HTMLInputElement_checkbox(TreeListItemView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView::checkbox = value;
  }-*/;
}