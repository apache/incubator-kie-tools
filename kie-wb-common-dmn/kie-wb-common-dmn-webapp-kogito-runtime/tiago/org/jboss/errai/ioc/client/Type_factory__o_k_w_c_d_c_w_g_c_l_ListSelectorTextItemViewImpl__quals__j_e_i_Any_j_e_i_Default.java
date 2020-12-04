package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemViewImpl;

public class Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorTextItemViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ListSelectorTextItemViewImpl> { public interface o_k_w_c_d_c_w_g_c_l_ListSelectorTextItemViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorTextItemViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorTextItemViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ListSelectorTextItemViewImpl.class, "Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorTextItemViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ListSelectorTextItemViewImpl.class, Object.class, ListSelectorTextItemView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public ListSelectorTextItemViewImpl createInstance(final ContextManager contextManager) {
    final Span _text_1 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ListItem _item_0 = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ListSelectorTextItemViewImpl instance = new ListSelectorTextItemViewImpl(_item_0, _text_1);
    registerDependentScopedReference(instance, _text_1);
    registerDependentScopedReference(instance, _item_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_w_g_c_l_ListSelectorTextItemViewImplTemplateResource templateForListSelectorTextItemViewImpl = GWT.create(o_k_w_c_d_c_w_g_c_l_ListSelectorTextItemViewImplTemplateResource.class);
    Element parentElementForTemplateOfListSelectorTextItemViewImpl = TemplateUtil.getRootTemplateParentElement(templateForListSelectorTextItemViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorTextItemViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorTextItemViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorTextItemViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorTextItemViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("item", new DataFieldMeta());
    dataFieldMetas.put("text", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemViewImpl", "org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorTextItemViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ListSelectorTextItemViewImpl_ListItem_item(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemViewImpl", "org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorTextItemViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ListSelectorTextItemViewImpl_Span_text(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "text");
    templateFieldsMap.put("item", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ListSelectorTextItemViewImpl_ListItem_item(instance))));
    templateFieldsMap.put("text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ListSelectorTextItemViewImpl_Span_text(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorTextItemViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ListSelectorTextItemViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ListSelectorTextItemViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static ListItem ListSelectorTextItemViewImpl_ListItem_item(ListSelectorTextItemViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemViewImpl::item;
  }-*/;

  native static void ListSelectorTextItemViewImpl_ListItem_item(ListSelectorTextItemViewImpl instance, ListItem value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemViewImpl::item = value;
  }-*/;

  native static Span ListSelectorTextItemViewImpl_Span_text(ListSelectorTextItemViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemViewImpl::text;
  }-*/;

  native static void ListSelectorTextItemViewImpl_Span_text(ListSelectorTextItemViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemViewImpl::text = value;
  }-*/;
}