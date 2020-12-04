package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLUListElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.views.pfly.widgets.KebabMenu;

public class Type_factory__o_u_c_v_p_w_KebabMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<KebabMenu> { public interface o_u_c_v_p_w_KebabMenuTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/widgets/KebabMenu.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_w_KebabMenu__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KebabMenu.class, "Type_factory__o_u_c_v_p_w_KebabMenu__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KebabMenu.class, Object.class, IsElement.class });
  }

  public KebabMenu createInstance(final ContextManager contextManager) {
    final KebabMenu instance = new KebabMenu();
    setIncompleteInstance(instance);
    final HTMLUListElement KebabMenu_dropdownMenu = (HTMLUListElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLUListElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KebabMenu_dropdownMenu);
    KebabMenu_HTMLUListElement_dropdownMenu(instance, KebabMenu_dropdownMenu);
    final HTMLDivElement KebabMenu_kebab = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KebabMenu_kebab);
    KebabMenu_HTMLDivElement_kebab(instance, KebabMenu_kebab);
    final HTMLDocument KebabMenu_document = (HTMLDocument) contextManager.getInstance("Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, KebabMenu_document);
    KebabMenu_HTMLDocument_document(instance, KebabMenu_document);
    o_u_c_v_p_w_KebabMenuTemplateResource templateForKebabMenu = GWT.create(o_u_c_v_p_w_KebabMenuTemplateResource.class);
    Element parentElementForTemplateOfKebabMenu = TemplateUtil.getRootTemplateParentElement(templateForKebabMenu.getContents().getText(), "org/uberfire/client/views/pfly/widgets/KebabMenu.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/widgets/KebabMenu.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKebabMenu));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKebabMenu));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("kebab", new DataFieldMeta());
    dataFieldMetas.put("dropdown-menu", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.KebabMenu", "org/uberfire/client/views/pfly/widgets/KebabMenu.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabMenu_HTMLDivElement_kebab(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "kebab");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.KebabMenu", "org/uberfire/client/views/pfly/widgets/KebabMenu.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabMenu_HTMLUListElement_dropdownMenu(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dropdown-menu");
    templateFieldsMap.put("kebab", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabMenu_HTMLDivElement_kebab(instance))));
    templateFieldsMap.put("dropdown-menu", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabMenu_HTMLUListElement_dropdownMenu(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKebabMenu), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KebabMenu) instance, contextManager);
  }

  public void destroyInstanceHelper(final KebabMenu instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDocument KebabMenu_HTMLDocument_document(KebabMenu instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.KebabMenu::document;
  }-*/;

  native static void KebabMenu_HTMLDocument_document(KebabMenu instance, HTMLDocument value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.KebabMenu::document = value;
  }-*/;

  native static HTMLDivElement KebabMenu_HTMLDivElement_kebab(KebabMenu instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.KebabMenu::kebab;
  }-*/;

  native static void KebabMenu_HTMLDivElement_kebab(KebabMenu instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.KebabMenu::kebab = value;
  }-*/;

  native static HTMLUListElement KebabMenu_HTMLUListElement_dropdownMenu(KebabMenu instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.KebabMenu::dropdownMenu;
  }-*/;

  native static void KebabMenu_HTMLUListElement_dropdownMenu(KebabMenu instance, HTMLUListElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.KebabMenu::dropdownMenu = value;
  }-*/;
}