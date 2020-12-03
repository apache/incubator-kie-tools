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
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.ext.widgets.common.client.menu.MenuItemDivider;
import org.uberfire.ext.widgets.common.client.menu.MenuItemDividerView;
import org.uberfire.ext.widgets.common.client.menu.MenuItemView;

public class Type_factory__o_u_e_w_c_c_m_MenuItemDividerView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemDivider extends Factory<MenuItemDividerView> { public interface o_u_e_w_c_c_m_MenuItemDividerViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/menu/MenuItemDividerView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_w_c_c_m_MenuItemDividerView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemDivider() {
    super(new FactoryHandleImpl(MenuItemDividerView.class, "Type_factory__o_u_e_w_c_c_m_MenuItemDividerView__quals__j_e_i_Any_o_u_e_w_c_c_m_MenuItemDivider", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MenuItemDividerView.class, Object.class, MenuItemView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new MenuItemDivider() {
        public Class annotationType() {
          return MenuItemDivider.class;
        }
        public String toString() {
          return "@org.uberfire.ext.widgets.common.client.menu.MenuItemDivider()";
        }
    } });
  }

  public MenuItemDividerView createInstance(final ContextManager contextManager) {
    final MenuItemDividerView instance = new MenuItemDividerView();
    setIncompleteInstance(instance);
    o_u_e_w_c_c_m_MenuItemDividerViewTemplateResource templateForMenuItemDividerView = GWT.create(o_u_e_w_c_c_m_MenuItemDividerViewTemplateResource.class);
    Element parentElementForTemplateOfMenuItemDividerView = TemplateUtil.getRootTemplateParentElement(templateForMenuItemDividerView.getContents().getText(), "org/uberfire/ext/widgets/common/client/menu/MenuItemDividerView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/menu/MenuItemDividerView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMenuItemDividerView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMenuItemDividerView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(0);
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMenuItemDividerView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MenuItemDividerView) instance, contextManager);
  }

  public void destroyInstanceHelper(final MenuItemDividerView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }
}