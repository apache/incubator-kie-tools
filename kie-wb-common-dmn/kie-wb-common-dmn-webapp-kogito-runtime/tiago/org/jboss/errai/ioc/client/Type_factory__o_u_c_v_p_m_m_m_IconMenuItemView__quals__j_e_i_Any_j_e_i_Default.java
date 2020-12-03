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
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.menu.megamenu.menuitem.IconMenuItemView;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.IconMenuItemPresenter.View;

public class Type_factory__o_u_c_v_p_m_m_m_IconMenuItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<IconMenuItemView> { public interface o_u_c_v_p_m_m_m_IconMenuItemViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/menu/megamenu/menuitem/IconMenuItemView.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_m_m_m_IconMenuItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IconMenuItemView.class, "Type_factory__o_u_c_v_p_m_m_m_IconMenuItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IconMenuItemView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, UberElement.class, HasPresenter.class });
  }

  public IconMenuItemView createInstance(final ContextManager contextManager) {
    final IconMenuItemView instance = new IconMenuItemView();
    setIncompleteInstance(instance);
    final Anchor IconMenuItemView_item = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, IconMenuItemView_item);
    IconMenuItemView_Anchor_item(instance, IconMenuItemView_item);
    final Span IconMenuItemView_icon = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, IconMenuItemView_icon);
    IconMenuItemView_Span_icon(instance, IconMenuItemView_icon);
    o_u_c_v_p_m_m_m_IconMenuItemViewTemplateResource templateForIconMenuItemView = GWT.create(o_u_c_v_p_m_m_m_IconMenuItemViewTemplateResource.class);
    Element parentElementForTemplateOfIconMenuItemView = TemplateUtil.getRootTemplateParentElement(templateForIconMenuItemView.getContents().getText(), "org/uberfire/client/views/pfly/menu/megamenu/menuitem/IconMenuItemView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/menu/megamenu/menuitem/IconMenuItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfIconMenuItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfIconMenuItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("item", new DataFieldMeta());
    dataFieldMetas.put("icon", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.menuitem.IconMenuItemView", "org/uberfire/client/views/pfly/menu/megamenu/menuitem/IconMenuItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IconMenuItemView_Anchor_item(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.menu.megamenu.menuitem.IconMenuItemView", "org/uberfire/client/views/pfly/menu/megamenu/menuitem/IconMenuItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(IconMenuItemView_Span_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    templateFieldsMap.put("item", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IconMenuItemView_Anchor_item(instance))));
    templateFieldsMap.put("icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(IconMenuItemView_Span_icon(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfIconMenuItemView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((IconMenuItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final IconMenuItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Anchor IconMenuItemView_Anchor_item(IconMenuItemView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.menuitem.IconMenuItemView::item;
  }-*/;

  native static void IconMenuItemView_Anchor_item(IconMenuItemView instance, Anchor value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.menuitem.IconMenuItemView::item = value;
  }-*/;

  native static Span IconMenuItemView_Span_icon(IconMenuItemView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.megamenu.menuitem.IconMenuItemView::icon;
  }-*/;

  native static void IconMenuItemView_Span_icon(IconMenuItemView instance, Span value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.megamenu.menuitem.IconMenuItemView::icon = value;
  }-*/;
}