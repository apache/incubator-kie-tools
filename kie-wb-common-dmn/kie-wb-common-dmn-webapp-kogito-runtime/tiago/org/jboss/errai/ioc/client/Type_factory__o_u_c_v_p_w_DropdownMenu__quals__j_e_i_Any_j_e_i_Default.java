package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.views.pfly.widgets.DropdownMenu;

public class Type_factory__o_u_c_v_p_w_DropdownMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<DropdownMenu> { public interface o_u_c_v_p_w_DropdownMenuTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/widgets/DropdownMenu.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_w_DropdownMenu__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DropdownMenu.class, "Type_factory__o_u_c_v_p_w_DropdownMenu__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DropdownMenu.class, Object.class, IsElement.class });
  }

  public DropdownMenu createInstance(final ContextManager contextManager) {
    final DropdownMenu instance = new DropdownMenu();
    setIncompleteInstance(instance);
    final HTMLUListElement DropdownMenu_dropdownMenu = (HTMLUListElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLUListElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DropdownMenu_dropdownMenu);
    DropdownMenu_HTMLUListElement_dropdownMenu(instance, DropdownMenu_dropdownMenu);
    final HTMLDivElement DropdownMenu_dropdown = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DropdownMenu_dropdown);
    DropdownMenu_HTMLDivElement_dropdown(instance, DropdownMenu_dropdown);
    final HTMLElement DropdownMenu_text = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    registerDependentScopedReference(instance, DropdownMenu_text);
    DropdownMenu_HTMLElement_text(instance, DropdownMenu_text);
    o_u_c_v_p_w_DropdownMenuTemplateResource templateForDropdownMenu = GWT.create(o_u_c_v_p_w_DropdownMenuTemplateResource.class);
    Element parentElementForTemplateOfDropdownMenu = TemplateUtil.getRootTemplateParentElement(templateForDropdownMenu.getContents().getText(), "org/uberfire/client/views/pfly/widgets/DropdownMenu.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/widgets/DropdownMenu.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDropdownMenu));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDropdownMenu));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("dropdown", new DataFieldMeta());
    dataFieldMetas.put("text", new DataFieldMeta());
    dataFieldMetas.put("dropdown-menu", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.DropdownMenu", "org/uberfire/client/views/pfly/widgets/DropdownMenu.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DropdownMenu_HTMLDivElement_dropdown(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropdown");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.DropdownMenu", "org/uberfire/client/views/pfly/widgets/DropdownMenu.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DropdownMenu_HTMLElement_text(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "text");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.DropdownMenu", "org/uberfire/client/views/pfly/widgets/DropdownMenu.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DropdownMenu_HTMLUListElement_dropdownMenu(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropdown-menu");
    templateFieldsMap.put("dropdown", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DropdownMenu_HTMLDivElement_dropdown(instance))));
    templateFieldsMap.put("text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DropdownMenu_HTMLElement_text(instance))));
    templateFieldsMap.put("dropdown-menu", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DropdownMenu_HTMLUListElement_dropdownMenu(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDropdownMenu), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DropdownMenu) instance, contextManager);
  }

  public void destroyInstanceHelper(final DropdownMenu instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLElement DropdownMenu_HTMLElement_text(DropdownMenu instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.DropdownMenu::text;
  }-*/;

  native static void DropdownMenu_HTMLElement_text(DropdownMenu instance, HTMLElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.DropdownMenu::text = value;
  }-*/;

  native static HTMLDivElement DropdownMenu_HTMLDivElement_dropdown(DropdownMenu instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.DropdownMenu::dropdown;
  }-*/;

  native static void DropdownMenu_HTMLDivElement_dropdown(DropdownMenu instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.DropdownMenu::dropdown = value;
  }-*/;

  native static HTMLUListElement DropdownMenu_HTMLUListElement_dropdownMenu(DropdownMenu instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.DropdownMenu::dropdownMenu;
  }-*/;

  native static void DropdownMenu_HTMLUListElement_dropdownMenu(DropdownMenu instance, HTMLUListElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.DropdownMenu::dropdownMenu = value;
  }-*/;
}