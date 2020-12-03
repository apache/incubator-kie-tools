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
import org.jboss.errai.common.client.dom.Heading;
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
import org.uberfire.client.views.pfly.menu.megamenu.menuitem.GroupMenuItemView;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.GroupMenuItemPresenter.View;

public class Type_factory__o_u_c_v_p_m_m_m_GroupMenuItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<GroupMenuItemView> { public interface o_u_c_v_p_m_m_m_GroupMenuItemViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/menu/megamenu/menuitem/GroupMenuItemView.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_m_m_m_GroupMenuItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GroupMenuItemView.class, "Type_factory__o_u_c_v_p_m_m_m_GroupMenuItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GroupMenuItemView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, UberElement.class, HasPresenter.class });
  }

  public GroupMenuItemView createInstance(final ContextManager contextManager) {
    final GroupMenuItemView instance = new GroupMenuItemView();
    setIncompleteInstance(instance);
    final Heading GroupMenuItemView_title = (Heading) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Heading__quals__j_e_i_Any_j_e_i_Default_j_i_Named_1");
    registerDependentScopedReference(instance, GroupMenuItemView_title);
    GroupMenuItemView_Heading_title(instance, GroupMenuItemView_title);
    final UnorderedList GroupMenuItemView_items = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, GroupMenuItemView_items);
    GroupMenuItemView_UnorderedList_items(instance, GroupMenuItemView_items);
    o_u_c_v_p_m_m_m_GroupMenuItemViewTemplateResource templateForGroupMenuItemView = GWT.create(o_u_c_v_p_m_m_m_GroupMenuItemViewTemplateResource.class);
    Element parentElementForTemplateOfGroupMenuItemView = TemplateUtil.getRootTemplateParentElement(templateForGroupMenuItemView.getContents().getText(), "org/uberfire/client/views/pfly/menu/megamenu/menuitem/GroupMenuItemView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/menu/megamenu/menuitem/GroupMenuItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfGroupMenuItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfGroupMenuItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("title", new DataFieldMeta());
    dataFieldMetas.put("items", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.menuitem.GroupMenuItemView", "org/uberfire/client/views/pfly/menu/megamenu/menuitem/GroupMenuItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(GroupMenuItemView_Heading_title(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "title");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.menuitem.GroupMenuItemView", "org/uberfire/client/views/pfly/menu/megamenu/menuitem/GroupMenuItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(GroupMenuItemView_UnorderedList_items(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "items");
    templateFieldsMap.put("title", ElementWrapperWidget.getWidget(TemplateUtil.asElement(GroupMenuItemView_Heading_title(instance))));
    templateFieldsMap.put("items", ElementWrapperWidget.getWidget(TemplateUtil.asElement(GroupMenuItemView_UnorderedList_items(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfGroupMenuItemView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((GroupMenuItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final GroupMenuItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static UnorderedList GroupMenuItemView_UnorderedList_items(GroupMenuItemView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.menuitem.GroupMenuItemView::items;
  }-*/;

  native static void GroupMenuItemView_UnorderedList_items(GroupMenuItemView instance, UnorderedList value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.menuitem.GroupMenuItemView::items = value;
  }-*/;

  native static Heading GroupMenuItemView_Heading_title(GroupMenuItemView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.menuitem.GroupMenuItemView::title;
  }-*/;

  native static void GroupMenuItemView_Heading_title(GroupMenuItemView instance, Heading value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.menuitem.GroupMenuItemView::title = value;
  }-*/;
}