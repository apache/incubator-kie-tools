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
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter.View;

public class Type_factory__o_u_c_v_p_m_m_c_GroupContextMenuItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<GroupContextMenuItemView> { public interface o_u_c_v_p_m_m_c_GroupContextMenuItemViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/GroupContextMenuItemView.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_m_m_c_GroupContextMenuItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GroupContextMenuItemView.class, "Type_factory__o_u_c_v_p_m_m_c_GroupContextMenuItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GroupContextMenuItemView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, UberElement.class, HasPresenter.class });
  }

  public GroupContextMenuItemView createInstance(final ContextManager contextManager) {
    final GroupContextMenuItemView instance = new GroupContextMenuItemView();
    setIncompleteInstance(instance);
    final UnorderedList GroupContextMenuItemView_items = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, GroupContextMenuItemView_items);
    GroupContextMenuItemView_UnorderedList_items(instance, GroupContextMenuItemView_items);
    final ListItem GroupContextMenuItemView_container = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, GroupContextMenuItemView_container);
    GroupContextMenuItemView_ListItem_container(instance, GroupContextMenuItemView_container);
    final Anchor GroupContextMenuItemView_dropdown = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, GroupContextMenuItemView_dropdown);
    GroupContextMenuItemView_Anchor_dropdown(instance, GroupContextMenuItemView_dropdown);
    o_u_c_v_p_m_m_c_GroupContextMenuItemViewTemplateResource templateForGroupContextMenuItemView = GWT.create(o_u_c_v_p_m_m_c_GroupContextMenuItemViewTemplateResource.class);
    Element parentElementForTemplateOfGroupContextMenuItemView = TemplateUtil.getRootTemplateParentElement(templateForGroupContextMenuItemView.getContents().getText(), "org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/GroupContextMenuItemView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/GroupContextMenuItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfGroupContextMenuItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfGroupContextMenuItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("container", new DataFieldMeta());
    dataFieldMetas.put("dropdown", new DataFieldMeta());
    dataFieldMetas.put("items", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView", "org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/GroupContextMenuItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(GroupContextMenuItemView_ListItem_container(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "container");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView", "org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/GroupContextMenuItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(GroupContextMenuItemView_Anchor_dropdown(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropdown");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView", "org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/GroupContextMenuItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(GroupContextMenuItemView_UnorderedList_items(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "items");
    templateFieldsMap.put("container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(GroupContextMenuItemView_ListItem_container(instance))));
    templateFieldsMap.put("dropdown", ElementWrapperWidget.getWidget(TemplateUtil.asElement(GroupContextMenuItemView_Anchor_dropdown(instance))));
    templateFieldsMap.put("items", ElementWrapperWidget.getWidget(TemplateUtil.asElement(GroupContextMenuItemView_UnorderedList_items(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfGroupContextMenuItemView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((GroupContextMenuItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final GroupContextMenuItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static UnorderedList GroupContextMenuItemView_UnorderedList_items(GroupContextMenuItemView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView::items;
  }-*/;

  native static void GroupContextMenuItemView_UnorderedList_items(GroupContextMenuItemView instance, UnorderedList value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView::items = value;
  }-*/;

  native static Anchor GroupContextMenuItemView_Anchor_dropdown(GroupContextMenuItemView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView::dropdown;
  }-*/;

  native static void GroupContextMenuItemView_Anchor_dropdown(GroupContextMenuItemView instance, Anchor value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView::dropdown = value;
  }-*/;

  native static ListItem GroupContextMenuItemView_ListItem_container(GroupContextMenuItemView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView::container;
  }-*/;

  native static void GroupContextMenuItemView_ListItem_container(GroupContextMenuItemView instance, ListItem value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView::container = value;
  }-*/;
}