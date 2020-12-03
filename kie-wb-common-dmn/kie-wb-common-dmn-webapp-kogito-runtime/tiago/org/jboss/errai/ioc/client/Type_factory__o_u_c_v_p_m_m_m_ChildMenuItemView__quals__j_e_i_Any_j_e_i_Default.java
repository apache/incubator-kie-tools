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
import org.uberfire.client.views.pfly.menu.megamenu.menuitem.ChildMenuItemView;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.ChildMenuItemPresenter.View;

public class Type_factory__o_u_c_v_p_m_m_m_ChildMenuItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<ChildMenuItemView> { public interface o_u_c_v_p_m_m_m_ChildMenuItemViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/menu/megamenu/menuitem/ChildMenuItemView.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_m_m_m_ChildMenuItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ChildMenuItemView.class, "Type_factory__o_u_c_v_p_m_m_m_ChildMenuItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ChildMenuItemView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, UberElement.class, HasPresenter.class });
  }

  public ChildMenuItemView createInstance(final ContextManager contextManager) {
    final ChildMenuItemView instance = new ChildMenuItemView();
    setIncompleteInstance(instance);
    final Anchor ChildMenuItemView_item = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ChildMenuItemView_item);
    ChildMenuItemView_Anchor_item(instance, ChildMenuItemView_item);
    o_u_c_v_p_m_m_m_ChildMenuItemViewTemplateResource templateForChildMenuItemView = GWT.create(o_u_c_v_p_m_m_m_ChildMenuItemViewTemplateResource.class);
    Element parentElementForTemplateOfChildMenuItemView = TemplateUtil.getRootTemplateParentElement(templateForChildMenuItemView.getContents().getText(), "org/uberfire/client/views/pfly/menu/megamenu/menuitem/ChildMenuItemView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/menu/megamenu/menuitem/ChildMenuItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfChildMenuItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfChildMenuItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("item", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.menuitem.ChildMenuItemView", "org/uberfire/client/views/pfly/menu/megamenu/menuitem/ChildMenuItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ChildMenuItemView_Anchor_item(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item");
    templateFieldsMap.put("item", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ChildMenuItemView_Anchor_item(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfChildMenuItemView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ChildMenuItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ChildMenuItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Anchor ChildMenuItemView_Anchor_item(ChildMenuItemView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.menuitem.ChildMenuItemView::item;
  }-*/;

  native static void ChildMenuItemView_Anchor_item(ChildMenuItemView instance, Anchor value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.menuitem.ChildMenuItemView::item = value;
  }-*/;
}