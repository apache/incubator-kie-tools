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
import org.uberfire.client.views.pfly.widgets.KebabMenuItem;

public class Type_factory__o_u_c_v_p_w_KebabMenuItem__quals__j_e_i_Any_j_e_i_Default extends Factory<KebabMenuItem> { public interface o_u_c_v_p_w_KebabMenuItemTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/widgets/KebabMenuItem.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_w_KebabMenuItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KebabMenuItem.class, "Type_factory__o_u_c_v_p_w_KebabMenuItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KebabMenuItem.class, Object.class, IsElement.class });
  }

  public KebabMenuItem createInstance(final ContextManager contextManager) {
    final KebabMenuItem instance = new KebabMenuItem();
    setIncompleteInstance(instance);
    final HTMLLIElement KebabMenuItem_item = (HTMLLIElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLLIElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KebabMenuItem_item);
    KebabMenuItem_HTMLLIElement_item(instance, KebabMenuItem_item);
    final HTMLAnchorElement KebabMenuItem_anchor = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KebabMenuItem_anchor);
    KebabMenuItem_HTMLAnchorElement_anchor(instance, KebabMenuItem_anchor);
    o_u_c_v_p_w_KebabMenuItemTemplateResource templateForKebabMenuItem = GWT.create(o_u_c_v_p_w_KebabMenuItemTemplateResource.class);
    Element parentElementForTemplateOfKebabMenuItem = TemplateUtil.getRootTemplateParentElement(templateForKebabMenuItem.getContents().getText(), "org/uberfire/client/views/pfly/widgets/KebabMenuItem.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/widgets/KebabMenuItem.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKebabMenuItem));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKebabMenuItem));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("kebab-item", new DataFieldMeta());
    dataFieldMetas.put("anchor", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.KebabMenuItem", "org/uberfire/client/views/pfly/widgets/KebabMenuItem.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabMenuItem_HTMLLIElement_item(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "kebab-item");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.KebabMenuItem", "org/uberfire/client/views/pfly/widgets/KebabMenuItem.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabMenuItem_HTMLAnchorElement_anchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "anchor");
    templateFieldsMap.put("kebab-item", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabMenuItem_HTMLLIElement_item(instance))));
    templateFieldsMap.put("anchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabMenuItem_HTMLAnchorElement_anchor(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKebabMenuItem), templateFieldsMap.values());
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
    destroyInstanceHelper((KebabMenuItem) instance, contextManager);
  }

  public void destroyInstanceHelper(final KebabMenuItem instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLAnchorElement KebabMenuItem_HTMLAnchorElement_anchor(KebabMenuItem instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.KebabMenuItem::anchor;
  }-*/;

  native static void KebabMenuItem_HTMLAnchorElement_anchor(KebabMenuItem instance, HTMLAnchorElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.KebabMenuItem::anchor = value;
  }-*/;

  native static HTMLLIElement KebabMenuItem_HTMLLIElement_item(KebabMenuItem instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.KebabMenuItem::item;
  }-*/;

  native static void KebabMenuItem_HTMLLIElement_item(KebabMenuItem instance, HTMLLIElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.KebabMenuItem::item = value;
  }-*/;
}