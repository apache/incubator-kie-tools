package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.common.rendering.client.widgets.FormWidget;
import org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBoxView;
import org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBoxViewImpl;

public class Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBoxViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DecimalBoxViewImpl> { public interface o_k_w_c_f_c_r_c_w_d_DecimalBoxViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/common/rendering/client/widgets/decimalBox/DecimalBoxViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBoxViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecimalBoxViewImpl.class, "Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBoxViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecimalBoxViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, DecimalBoxView.class, FormWidget.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class });
  }

  public DecimalBoxViewImpl createInstance(final ContextManager contextManager) {
    final DecimalBoxViewImpl instance = new DecimalBoxViewImpl();
    setIncompleteInstance(instance);
    final TextInput DecimalBoxViewImpl_input = (TextInput) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_TextInput__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DecimalBoxViewImpl_input);
    DecimalBoxViewImpl_TextInput_input(instance, DecimalBoxViewImpl_input);
    o_k_w_c_f_c_r_c_w_d_DecimalBoxViewImplTemplateResource templateForDecimalBoxViewImpl = GWT.create(o_k_w_c_f_c_r_c_w_d_DecimalBoxViewImplTemplateResource.class);
    Element parentElementForTemplateOfDecimalBoxViewImpl = TemplateUtil.getRootTemplateParentElement(templateForDecimalBoxViewImpl.getContents().getText(), "org/kie/workbench/common/forms/common/rendering/client/widgets/decimalBox/DecimalBoxViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/common/rendering/client/widgets/decimalBox/DecimalBoxViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecimalBoxViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecimalBoxViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("input", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBoxViewImpl", "org/kie/workbench/common/forms/common/rendering/client/widgets/decimalBox/DecimalBoxViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecimalBoxViewImpl_TextInput_input(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "input");
    templateFieldsMap.put("input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecimalBoxViewImpl_TextInput_input(instance))));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecimalBoxViewImpl), templateFieldsMap.values());
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("input"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.onEvent(event);
      }
    }, 1152);
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DecimalBoxViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DecimalBoxViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static TextInput DecimalBoxViewImpl_TextInput_input(DecimalBoxViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBoxViewImpl::input;
  }-*/;

  native static void DecimalBoxViewImpl_TextInput_input(DecimalBoxViewImpl instance, TextInput value) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBoxViewImpl::input = value;
  }-*/;
}