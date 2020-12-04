package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import org.jboss.errai.common.client.dom.Div;
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
import org.uberfire.ext.widgets.common.client.menu.MenuItemView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIcon;
import org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView;

public class Type_factory__o_u_e_w_c_c_m_MenuItemWithIconView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemWithIcon extends Factory<MenuItemWithIconView> { public interface o_u_e_w_c_c_m_MenuItemWithIconViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/menu/MenuItemWithIconView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_w_c_c_m_MenuItemWithIconView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemWithIcon() {
    super(new FactoryHandleImpl(MenuItemWithIconView.class, "Type_factory__o_u_e_w_c_c_m_MenuItemWithIconView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemWithIcon", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MenuItemWithIconView.class, Object.class, MenuItemView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new MenuItemWithIcon() {
        public Class annotationType() {
          return MenuItemWithIcon.class;
        }
        public String toString() {
          return "@org.uberfire.ext.widgets.common.client.menu.MenuItemWithIcon()";
        }
    } });
  }

  public MenuItemWithIconView createInstance(final ContextManager contextManager) {
    final MenuItemWithIconView instance = new MenuItemWithIconView();
    setIncompleteInstance(instance);
    final Div MenuItemWithIconView_icon = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MenuItemWithIconView_icon);
    MenuItemWithIconView_Div_icon(instance, MenuItemWithIconView_icon);
    final ListItem MenuItemWithIconView_listItem = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MenuItemWithIconView_listItem);
    MenuItemWithIconView_ListItem_listItem(instance, MenuItemWithIconView_listItem);
    final Span MenuItemWithIconView_caption = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MenuItemWithIconView_caption);
    MenuItemWithIconView_Span_caption(instance, MenuItemWithIconView_caption);
    o_u_e_w_c_c_m_MenuItemWithIconViewTemplateResource templateForMenuItemWithIconView = GWT.create(o_u_e_w_c_c_m_MenuItemWithIconViewTemplateResource.class);
    Element parentElementForTemplateOfMenuItemWithIconView = TemplateUtil.getRootTemplateParentElement(templateForMenuItemWithIconView.getContents().getText(), "org/uberfire/ext/widgets/common/client/menu/MenuItemWithIconView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/menu/MenuItemWithIconView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMenuItemWithIconView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMenuItemWithIconView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("icon", new DataFieldMeta());
    dataFieldMetas.put("caption", new DataFieldMeta());
    dataFieldMetas.put("listItem", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView", "org/uberfire/ext/widgets/common/client/menu/MenuItemWithIconView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MenuItemWithIconView_Div_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView", "org/uberfire/ext/widgets/common/client/menu/MenuItemWithIconView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MenuItemWithIconView_Span_caption(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "caption");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView", "org/uberfire/ext/widgets/common/client/menu/MenuItemWithIconView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MenuItemWithIconView_ListItem_listItem(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "listItem");
    templateFieldsMap.put("icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MenuItemWithIconView_Div_icon(instance))));
    templateFieldsMap.put("caption", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MenuItemWithIconView_Span_caption(instance))));
    templateFieldsMap.put("listItem", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MenuItemWithIconView_ListItem_listItem(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMenuItemWithIconView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("listItem"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClickListItem(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MenuItemWithIconView) instance, contextManager);
  }

  public void destroyInstanceHelper(final MenuItemWithIconView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static ListItem MenuItemWithIconView_ListItem_listItem(MenuItemWithIconView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView::listItem;
  }-*/;

  native static void MenuItemWithIconView_ListItem_listItem(MenuItemWithIconView instance, ListItem value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView::listItem = value;
  }-*/;

  native static Div MenuItemWithIconView_Div_icon(MenuItemWithIconView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView::icon;
  }-*/;

  native static void MenuItemWithIconView_Div_icon(MenuItemWithIconView instance, Div value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView::icon = value;
  }-*/;

  native static Span MenuItemWithIconView_Span_caption(MenuItemWithIconView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView::caption;
  }-*/;

  native static void MenuItemWithIconView_Span_caption(MenuItemWithIconView instance, Span value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.menu.MenuItemWithIconView::caption = value;
  }-*/;
}