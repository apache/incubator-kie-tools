package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLUListElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem;

public class Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView_TreeItem__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeItem> { public interface o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView_TreeItemTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView_TreeItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeItem.class, "Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView_TreeItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeItem.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public TreeItem createInstance(final ContextManager contextManager) {
    final HTMLElement _textContent_0 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLInputElement _inputText_1 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _save_4 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=i)";
        }
        public String value() {
          return "i";
        }
    } });
    final HTMLElement _edit_5 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=i)";
        }
        public String value() {
          return "i";
        }
    } });
    final HTMLUListElement _subItems_3 = (HTMLUListElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLUListElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _icon_2 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLElement _remove_6 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=i)";
        }
        public String value() {
          return "i";
        }
    } });
    final TreeItem instance = new TreeItem(_textContent_0, _inputText_1, _icon_2, _subItems_3, _save_4, _edit_5, _remove_6);
    registerDependentScopedReference(instance, _textContent_0);
    registerDependentScopedReference(instance, _inputText_1);
    registerDependentScopedReference(instance, _save_4);
    registerDependentScopedReference(instance, _edit_5);
    registerDependentScopedReference(instance, _subItems_3);
    registerDependentScopedReference(instance, _icon_2);
    registerDependentScopedReference(instance, _remove_6);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView_TreeItemTemplateResource templateForTreeItem = GWT.create(o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView_TreeItemTemplateResource.class);
    Element parentElementForTemplateOfTreeItem = TemplateUtil.getRootTemplateParentElement(templateForTreeItem.getContents().getText(), "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", "item");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeItem));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeItem));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(7);
    dataFieldMetas.put("text-content", new DataFieldMeta());
    dataFieldMetas.put("input-text", new DataFieldMeta());
    dataFieldMetas.put("icon", new DataFieldMeta());
    dataFieldMetas.put("sub-items", new DataFieldMeta());
    dataFieldMetas.put("save", new DataFieldMeta());
    dataFieldMetas.put("edit", new DataFieldMeta());
    dataFieldMetas.put("remove", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView$TreeItem", "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLElement_textContent(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "text-content");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView$TreeItem", "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLInputElement_inputText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "input-text");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView$TreeItem", "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLElement_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView$TreeItem", "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLUListElement_subItems(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "sub-items");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView$TreeItem", "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLElement_save(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "save");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView$TreeItem", "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLElement_edit(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "edit");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView$TreeItem", "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLElement_remove(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "remove");
    templateFieldsMap.put("text-content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLElement_textContent(instance))));
    templateFieldsMap.put("input-text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLInputElement_inputText(instance))));
    templateFieldsMap.put("icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLElement_icon(instance))));
    templateFieldsMap.put("sub-items", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLUListElement_subItems(instance))));
    templateFieldsMap.put("save", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLElement_save(instance))));
    templateFieldsMap.put("edit", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLElement_edit(instance))));
    templateFieldsMap.put("remove", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeItem_HTMLElement_remove(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeItem), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("remove"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onRemoveClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("text-content"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onTextContentClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("save"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onSaveClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("icon"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onIconClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("input-text"), new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        instance.onInputTextKeyPress(event);
      }
    }, KeyDownEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("edit"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onEditClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("input-text"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        instance.onInputTextBlur(event);
      }
    }, BlurEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TreeItem) instance, contextManager);
  }

  public void destroyInstanceHelper(final TreeItem instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLElement TreeItem_HTMLElement_edit(TreeItem instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::edit;
  }-*/;

  native static void TreeItem_HTMLElement_edit(TreeItem instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::edit = value;
  }-*/;

  native static HTMLElement TreeItem_HTMLElement_remove(TreeItem instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::remove;
  }-*/;

  native static void TreeItem_HTMLElement_remove(TreeItem instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::remove = value;
  }-*/;

  native static HTMLInputElement TreeItem_HTMLInputElement_inputText(TreeItem instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::inputText;
  }-*/;

  native static void TreeItem_HTMLInputElement_inputText(TreeItem instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::inputText = value;
  }-*/;

  native static HTMLUListElement TreeItem_HTMLUListElement_subItems(TreeItem instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::subItems;
  }-*/;

  native static void TreeItem_HTMLUListElement_subItems(TreeItem instance, HTMLUListElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::subItems = value;
  }-*/;

  native static HTMLElement TreeItem_HTMLElement_textContent(TreeItem instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::textContent;
  }-*/;

  native static void TreeItem_HTMLElement_textContent(TreeItem instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::textContent = value;
  }-*/;

  native static HTMLElement TreeItem_HTMLElement_icon(TreeItem instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::icon;
  }-*/;

  native static void TreeItem_HTMLElement_icon(TreeItem instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::icon = value;
  }-*/;

  native static HTMLElement TreeItem_HTMLElement_save(TreeItem instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::save;
  }-*/;

  native static void TreeItem_HTMLElement_save(TreeItem instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem::save = value;
  }-*/;
}