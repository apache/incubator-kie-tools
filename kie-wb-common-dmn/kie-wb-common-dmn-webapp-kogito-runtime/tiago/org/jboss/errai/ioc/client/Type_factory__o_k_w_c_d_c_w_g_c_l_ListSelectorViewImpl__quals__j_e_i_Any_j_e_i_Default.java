package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorDividerItemView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorHeaderItemView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorTextItemView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.PopoverView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ListSelectorViewImpl> { public interface o_k_w_c_d_c_w_g_c_l_ListSelectorViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ListSelectorViewImpl.class, "Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ListSelectorViewImpl.class, Object.class, ListSelectorView.class, PopoverView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, CanBeClosedByKeyboard.class, UberElement.class, HasPresenter.class });
  }

  public ListSelectorViewImpl createInstance(final ContextManager contextManager) {
    final ManagedInstance<ListSelectorTextItemView> _listSelectorTextItemViews_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ListSelectorTextItemView.class }, new Annotation[] { });
    final UnorderedList _itemsContainer_0 = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ManagedInstance<ListSelectorDividerItemView> _listSelectorDividerItemViews_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ListSelectorDividerItemView.class }, new Annotation[] { });
    final ManagedInstance<ListSelectorHeaderItemView> _listSelectorHeaderItemViews_3 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ListSelectorHeaderItemView.class }, new Annotation[] { });
    final ListSelectorViewImpl instance = new ListSelectorViewImpl(_itemsContainer_0, _listSelectorTextItemViews_1, _listSelectorDividerItemViews_2, _listSelectorHeaderItemViews_3);
    registerDependentScopedReference(instance, _listSelectorTextItemViews_1);
    registerDependentScopedReference(instance, _itemsContainer_0);
    registerDependentScopedReference(instance, _listSelectorDividerItemViews_2);
    registerDependentScopedReference(instance, _listSelectorHeaderItemViews_3);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_w_g_c_l_ListSelectorViewImplTemplateResource templateForListSelectorViewImpl = GWT.create(o_k_w_c_d_c_w_g_c_l_ListSelectorViewImplTemplateResource.class);
    Element parentElementForTemplateOfListSelectorViewImpl = TemplateUtil.getRootTemplateParentElement(templateForListSelectorViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("items-container", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorViewImpl", "org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ListSelectorViewImpl_UnorderedList_itemsContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "items-container");
    templateFieldsMap.put("items-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ListSelectorViewImpl_UnorderedList_itemsContainer(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ListSelectorViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ListSelectorViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static UnorderedList ListSelectorViewImpl_UnorderedList_itemsContainer(ListSelectorViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorViewImpl::itemsContainer;
  }-*/;

  native static void ListSelectorViewImpl_UnorderedList_itemsContainer(ListSelectorViewImpl instance, UnorderedList value) /*-{
    instance.@org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorViewImpl::itemsContainer = value;
  }-*/;
}