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
import org.uberfire.ext.widgets.common.client.menu.MenuItemHeader;
import org.uberfire.ext.widgets.common.client.menu.MenuItemHeaderView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemView;

public class Type_factory__o_u_e_w_c_c_m_MenuItemHeaderView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemHeader extends Factory<MenuItemHeaderView> { public interface o_u_e_w_c_c_m_MenuItemHeaderViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/menu/MenuItemHeaderView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_w_c_c_m_MenuItemHeaderView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemHeader() {
    super(new FactoryHandleImpl(MenuItemHeaderView.class, "Type_factory__o_u_e_w_c_c_m_MenuItemHeaderView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemHeader", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MenuItemHeaderView.class, Object.class, MenuItemView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new MenuItemHeader() {
        public Class annotationType() {
          return MenuItemHeader.class;
        }
        public String toString() {
          return "@org.uberfire.ext.widgets.common.client.menu.MenuItemHeader()";
        }
    } });
  }

  public MenuItemHeaderView createInstance(final ContextManager contextManager) {
    final MenuItemHeaderView instance = new MenuItemHeaderView();
    setIncompleteInstance(instance);
    final Span MenuItemHeaderView_caption = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MenuItemHeaderView_caption);
    MenuItemHeaderView_Span_caption(instance, MenuItemHeaderView_caption);
    o_u_e_w_c_c_m_MenuItemHeaderViewTemplateResource templateForMenuItemHeaderView = GWT.create(o_u_e_w_c_c_m_MenuItemHeaderViewTemplateResource.class);
    Element parentElementForTemplateOfMenuItemHeaderView = TemplateUtil.getRootTemplateParentElement(templateForMenuItemHeaderView.getContents().getText(), "org/uberfire/ext/widgets/common/client/menu/MenuItemHeaderView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/menu/MenuItemHeaderView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMenuItemHeaderView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMenuItemHeaderView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("caption", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.menu.MenuItemHeaderView", "org/uberfire/ext/widgets/common/client/menu/MenuItemHeaderView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MenuItemHeaderView_Span_caption(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "caption");
    templateFieldsMap.put("caption", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MenuItemHeaderView_Span_caption(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMenuItemHeaderView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MenuItemHeaderView) instance, contextManager);
  }

  public void destroyInstanceHelper(final MenuItemHeaderView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Span MenuItemHeaderView_Span_caption(MenuItemHeaderView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.menu.MenuItemHeaderView::caption;
  }-*/;

  native static void MenuItemHeaderView_Span_caption(MenuItemHeaderView instance, Span value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.menu.MenuItemHeaderView::caption = value;
  }-*/;
}