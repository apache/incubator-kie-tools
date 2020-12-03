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
import org.gwtbootstrap3.client.ui.Icon;
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
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorHeaderItemView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorHeaderItemViewImpl;

public class Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorHeaderItemViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ListSelectorHeaderItemViewImpl> { public interface o_k_w_c_d_c_w_g_c_l_ListSelectorHeaderItemViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorHeaderItemViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorHeaderItemViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ListSelectorHeaderItemViewImpl.class, "Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorHeaderItemViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ListSelectorHeaderItemViewImpl.class, Object.class, ListSelectorHeaderItemView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public ListSelectorHeaderItemViewImpl createInstance(final ContextManager contextManager) {
    final Icon _icon_1 = (Icon) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_Icon__quals__j_e_i_Any_j_e_i_Default");
    final Span _text_0 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ListSelectorHeaderItemViewImpl instance = new ListSelectorHeaderItemViewImpl(_text_0, _icon_1);
    registerDependentScopedReference(instance, _icon_1);
    registerDependentScopedReference(instance, _text_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_w_g_c_l_ListSelectorHeaderItemViewImplTemplateResource templateForListSelectorHeaderItemViewImpl = GWT.create(o_k_w_c_d_c_w_g_c_l_ListSelectorHeaderItemViewImplTemplateResource.class);
    Element parentElementForTemplateOfListSelectorHeaderItemViewImpl = TemplateUtil.getRootTemplateParentElement(templateForListSelectorHeaderItemViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorHeaderItemViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorHeaderItemViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorHeaderItemViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorHeaderItemViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("text", new DataFieldMeta());
    dataFieldMetas.put("icon", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorHeaderItemViewImpl", "org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorHeaderItemViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ListSelectorHeaderItemViewImpl_Span_text(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "text");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorHeaderItemViewImpl", "org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorHeaderItemViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ListSelectorHeaderItemViewImpl_Icon_icon(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    templateFieldsMap.put("text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ListSelectorHeaderItemViewImpl_Span_text(instance))));
    templateFieldsMap.put("icon", ListSelectorHeaderItemViewImpl_Icon_icon(instance).asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorHeaderItemViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ListSelectorHeaderItemViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ListSelectorHeaderItemViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Icon ListSelectorHeaderItemViewImpl_Icon_icon(ListSelectorHeaderItemViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorHeaderItemViewImpl::icon;
  }-*/;

  native static void ListSelectorHeaderItemViewImpl_Icon_icon(ListSelectorHeaderItemViewImpl instance, Icon value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorHeaderItemViewImpl::icon = value;
  }-*/;

  native static Span ListSelectorHeaderItemViewImpl_Span_text(ListSelectorHeaderItemViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorHeaderItemViewImpl::text;
  }-*/;

  native static void ListSelectorHeaderItemViewImpl_Span_text(ListSelectorHeaderItemViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorHeaderItemViewImpl::text = value;
  }-*/;
}