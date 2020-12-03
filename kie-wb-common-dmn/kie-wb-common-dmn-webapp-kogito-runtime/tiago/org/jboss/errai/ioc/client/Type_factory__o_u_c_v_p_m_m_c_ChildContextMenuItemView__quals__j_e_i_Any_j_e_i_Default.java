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
import org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.ChildContextMenuItemView;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter.View;

public class Type_factory__o_u_c_v_p_m_m_c_ChildContextMenuItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<ChildContextMenuItemView> { public interface o_u_c_v_p_m_m_c_ChildContextMenuItemViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/ChildContextMenuItemView.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_m_m_c_ChildContextMenuItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ChildContextMenuItemView.class, "Type_factory__o_u_c_v_p_m_m_c_ChildContextMenuItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ChildContextMenuItemView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, UberElement.class, HasPresenter.class });
  }

  public ChildContextMenuItemView createInstance(final ContextManager contextManager) {
    final ChildContextMenuItemView instance = new ChildContextMenuItemView();
    setIncompleteInstance(instance);
    final ListItem ChildContextMenuItemView_container = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ChildContextMenuItemView_container);
    ChildContextMenuItemView_ListItem_container(instance, ChildContextMenuItemView_container);
    final Anchor ChildContextMenuItemView_item = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ChildContextMenuItemView_item);
    ChildContextMenuItemView_Anchor_item(instance, ChildContextMenuItemView_item);
    o_u_c_v_p_m_m_c_ChildContextMenuItemViewTemplateResource templateForChildContextMenuItemView = GWT.create(o_u_c_v_p_m_m_c_ChildContextMenuItemViewTemplateResource.class);
    Element parentElementForTemplateOfChildContextMenuItemView = TemplateUtil.getRootTemplateParentElement(templateForChildContextMenuItemView.getContents().getText(), "org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/ChildContextMenuItemView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/ChildContextMenuItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfChildContextMenuItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfChildContextMenuItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("container", new DataFieldMeta());
    dataFieldMetas.put("item", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.ChildContextMenuItemView", "org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/ChildContextMenuItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ChildContextMenuItemView_ListItem_container(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "container");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.ChildContextMenuItemView", "org/uberfire/client/views/pfly/menu/megamenu/contextmenuitem/ChildContextMenuItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ChildContextMenuItemView_Anchor_item(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item");
    templateFieldsMap.put("container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ChildContextMenuItemView_ListItem_container(instance))));
    templateFieldsMap.put("item", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ChildContextMenuItemView_Anchor_item(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfChildContextMenuItemView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ChildContextMenuItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ChildContextMenuItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static ListItem ChildContextMenuItemView_ListItem_container(ChildContextMenuItemView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.ChildContextMenuItemView::container;
  }-*/;

  native static void ChildContextMenuItemView_ListItem_container(ChildContextMenuItemView instance, ListItem value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.ChildContextMenuItemView::container = value;
  }-*/;

  native static Anchor ChildContextMenuItemView_Anchor_item(ChildContextMenuItemView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.ChildContextMenuItemView::item;
  }-*/;

  native static void ChildContextMenuItemView_Anchor_item(ChildContextMenuItemView instance, Anchor value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.ChildContextMenuItemView::item = value;
  }-*/;
}