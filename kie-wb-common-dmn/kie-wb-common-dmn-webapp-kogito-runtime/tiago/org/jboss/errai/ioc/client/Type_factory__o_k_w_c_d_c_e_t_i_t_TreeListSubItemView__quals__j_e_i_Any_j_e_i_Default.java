package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLElement;
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
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItem.View;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItemView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeListSubItemView> { public interface o_k_w_c_d_c_e_t_i_t_TreeListSubItemViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListSubItemView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeListSubItemView.class, "Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeListSubItemView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n.sub-item-details {\n  padding-left: 50px;\n}\n.sub-item-details span {\n  font-weight: 600;\n}\n\n");
  }

  public TreeListSubItemView createInstance(final ContextManager contextManager) {
    final HTMLElement _description_0 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLElement _details_1 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final TreeListSubItemView instance = new TreeListSubItemView(_description_0, _details_1);
    registerDependentScopedReference(instance, _description_0);
    registerDependentScopedReference(instance, _details_1);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_i_t_TreeListSubItemViewTemplateResource templateForTreeListSubItemView = GWT.create(o_k_w_c_d_c_e_t_i_t_TreeListSubItemViewTemplateResource.class);
    Element parentElementForTemplateOfTreeListSubItemView = TemplateUtil.getRootTemplateParentElement(templateForTreeListSubItemView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListSubItemView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListSubItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeListSubItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeListSubItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("description", new DataFieldMeta());
    dataFieldMetas.put("details", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItemView", "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListSubItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListSubItemView_HTMLElement_description(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "description");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItemView", "org/kie/workbench/common/dmn/client/editors/types/imported/treelist/TreeListSubItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListSubItemView_HTMLElement_details(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "details");
    templateFieldsMap.put("description", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListSubItemView_HTMLElement_description(instance))));
    templateFieldsMap.put("details", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeListSubItemView_HTMLElement_details(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeListSubItemView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TreeListSubItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final TreeListSubItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLElement TreeListSubItemView_HTMLElement_description(TreeListSubItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItemView::description;
  }-*/;

  native static void TreeListSubItemView_HTMLElement_description(TreeListSubItemView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItemView::description = value;
  }-*/;

  native static HTMLElement TreeListSubItemView_HTMLElement_details(TreeListSubItemView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItemView::details;
  }-*/;

  native static void TreeListSubItemView_HTMLElement_details(TreeListSubItemView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItemView::details = value;
  }-*/;
}