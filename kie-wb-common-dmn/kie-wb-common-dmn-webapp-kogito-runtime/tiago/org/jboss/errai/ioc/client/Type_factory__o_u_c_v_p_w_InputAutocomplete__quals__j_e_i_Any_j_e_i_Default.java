package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLInputElement;
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
import org.uberfire.client.views.pfly.widgets.InputAutocomplete;
import org.uberfire.client.views.pfly.widgets.InputAutocomplete.InputAutocompleteElement;
import org.uberfire.client.views.pfly.widgets.JQueryElementalProducer.JQuery;

public class Type_factory__o_u_c_v_p_w_InputAutocomplete__quals__j_e_i_Any_j_e_i_Default extends Factory<InputAutocomplete> { public interface o_u_c_v_p_w_InputAutocompleteTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/widgets/InputAutocomplete.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_w_InputAutocomplete__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(InputAutocomplete.class, "Type_factory__o_u_c_v_p_w_InputAutocomplete__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { InputAutocomplete.class, Object.class, IsElement.class });
  }

  public InputAutocomplete createInstance(final ContextManager contextManager) {
    final InputAutocomplete instance = new InputAutocomplete();
    setIncompleteInstance(instance);
    final JQuery InputAutocomplete_jQuery = (JQuery) contextManager.getInstance("Producer_factory__o_u_c_v_p_w_JQueryElementalProducer_JQuery__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, InputAutocomplete_jQuery);
    InputAutocomplete_JQuery_jQuery(instance, InputAutocomplete_jQuery);
    final HTMLInputElement InputAutocomplete_input = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, InputAutocomplete_input);
    InputAutocomplete_HTMLInputElement_input(instance, InputAutocomplete_input);
    o_u_c_v_p_w_InputAutocompleteTemplateResource templateForInputAutocomplete = GWT.create(o_u_c_v_p_w_InputAutocompleteTemplateResource.class);
    Element parentElementForTemplateOfInputAutocomplete = TemplateUtil.getRootTemplateParentElement(templateForInputAutocomplete.getContents().getText(), "org/uberfire/client/views/pfly/widgets/InputAutocomplete.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/widgets/InputAutocomplete.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfInputAutocomplete));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfInputAutocomplete));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("input", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.InputAutocomplete", "org/uberfire/client/views/pfly/widgets/InputAutocomplete.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(InputAutocomplete_HTMLInputElement_input(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "input");
    templateFieldsMap.put("input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(InputAutocomplete_HTMLInputElement_input(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfInputAutocomplete), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((InputAutocomplete) instance, contextManager);
  }

  public void destroyInstanceHelper(final InputAutocomplete instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLInputElement InputAutocomplete_HTMLInputElement_input(InputAutocomplete instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.InputAutocomplete::input;
  }-*/;

  native static void InputAutocomplete_HTMLInputElement_input(InputAutocomplete instance, HTMLInputElement value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.InputAutocomplete::input = value;
  }-*/;

  native static JQuery InputAutocomplete_JQuery_jQuery(InputAutocomplete instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.InputAutocomplete::jQuery;
  }-*/;

  native static void InputAutocomplete_JQuery_jQuery(InputAutocomplete instance, JQuery<InputAutocompleteElement> value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.InputAutocomplete::jQuery = value;
  }-*/;
}