package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLLIElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import jsinterop.base.Js;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.views.pfly.widgets.DropdownMenuItem;

public class Type_factory__o_u_c_v_p_w_DropdownMenuItem__quals__j_e_i_Any_j_e_i_Default extends Factory<DropdownMenuItem> { public interface o_u_c_v_p_w_DropdownMenuItemTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/widgets/DropdownMenuItem.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_w_DropdownMenuItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DropdownMenuItem.class, "Type_factory__o_u_c_v_p_w_DropdownMenuItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DropdownMenuItem.class, Object.class, IsElement.class });
  }

  public DropdownMenuItem createInstance(final ContextManager contextManager) {
    final DropdownMenuItem instance = new DropdownMenuItem();
    setIncompleteInstance(instance);
    final HTMLLIElement DropdownMenuItem_item = (HTMLLIElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLLIElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DropdownMenuItem_item);
    DropdownMenuItem_HTMLLIElement_item(instance, DropdownMenuItem_item);
    final HTMLAnchorElement DropdownMenuItem_anchor = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DropdownMenuItem_anchor);
    DropdownMenuItem_HTMLAnchorElement_anchor(instance, DropdownMenuItem_anchor);
    o_u_c_v_p_w_DropdownMenuItemTemplateResource templateForDropdownMenuItem = GWT.create(o_u_c_v_p_w_DropdownMenuItemTemplateResource.class);
    Element parentElementForTemplateOfDropdownMenuItem = TemplateUtil.getRootTemplateParentElement(templateForDropdownMenuItem.getContents().getText(), "org/uberfire/client/views/pfly/widgets/DropdownMenuItem.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/widgets/DropdownMenuItem.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDropdownMenuItem));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDropdownMenuItem));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("dropdown-item", new DataFieldMeta());
    dataFieldMetas.put("anchor", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.DropdownMenuItem", "org/uberfire/client/views/pfly/widgets/DropdownMenuItem.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DropdownMenuItem_HTMLLIElement_item(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropdown-item");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.DropdownMenuItem", "org/uberfire/client/views/pfly/widgets/DropdownMenuItem.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DropdownMenuItem_HTMLAnchorElement_anchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "anchor");
    templateFieldsMap.put("dropdown-item", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DropdownMenuItem_HTMLLIElement_item(instance))));
    templateFieldsMap.put("anchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DropdownMenuItem_HTMLAnchorElement_anchor(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDropdownMenuItem), templateFieldsMap.values());
    final EventListener listenerForEventCallingOnClick = new EventListener() {
      public void call(Event event) {
        instance.onClick(Js.cast(event));
      }
    };
    TemplateUtil.setupBrowserEventListener(instance, templateFieldsMap.get("anchor"), listenerForEventCallingOnClick, "click");
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DropdownMenuItem) instance, contextManager);
  }

  public void destroyInstanceHelper(final DropdownMenuItem instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLAnchorElement DropdownMenuItem_HTMLAnchorElement_anchor(DropdownMenuItem instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.DropdownMenuItem::anchor;
  }-*/;

  native static void DropdownMenuItem_HTMLAnchorElement_anchor(DropdownMenuItem instance, HTMLAnchorElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.DropdownMenuItem::anchor = value;
  }-*/;

  native static HTMLLIElement DropdownMenuItem_HTMLLIElement_item(DropdownMenuItem instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.DropdownMenuItem::item;
  }-*/;

  native static void DropdownMenuItem_HTMLLIElement_item(DropdownMenuItem instance, HTMLLIElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.DropdownMenuItem::item = value;
  }-*/;
}