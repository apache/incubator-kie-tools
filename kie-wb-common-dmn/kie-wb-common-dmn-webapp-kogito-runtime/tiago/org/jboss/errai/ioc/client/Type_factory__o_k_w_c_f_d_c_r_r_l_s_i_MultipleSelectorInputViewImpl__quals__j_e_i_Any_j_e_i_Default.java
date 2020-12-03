package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
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
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.common.rendering.client.widgets.FormWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInputView;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInputViewImpl;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInputViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSelectorInputViewImpl> { public interface o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInputViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/selector/input/MultipleSelectorInputViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInputViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultipleSelectorInputViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInputViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultipleSelectorInputViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, MultipleSelectorInputView.class, FormWidget.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class });
  }

  public MultipleSelectorInputViewImpl createInstance(final ContextManager contextManager) {
    final MultipleSelectorInputViewImpl instance = new MultipleSelectorInputViewImpl();
    setIncompleteInstance(instance);
    final Div MultipleSelectorInputViewImpl_selector = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleSelectorInputViewImpl_selector);
    MultipleSelectorInputViewImpl_Div_selector(instance, MultipleSelectorInputViewImpl_selector);
    o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInputViewImplTemplateResource templateForMultipleSelectorInputViewImpl = GWT.create(o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInputViewImplTemplateResource.class);
    Element parentElementForTemplateOfMultipleSelectorInputViewImpl = TemplateUtil.getRootTemplateParentElement(templateForMultipleSelectorInputViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/selector/input/MultipleSelectorInputViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/selector/input/MultipleSelectorInputViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleSelectorInputViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleSelectorInputViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("selector", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInputViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/selector/input/MultipleSelectorInputViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleSelectorInputViewImpl_Div_selector(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "selector");
    templateFieldsMap.put("selector", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleSelectorInputViewImpl_Div_selector(instance))));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleSelectorInputViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MultipleSelectorInputViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final MultipleSelectorInputViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static Div MultipleSelectorInputViewImpl_Div_selector(MultipleSelectorInputViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInputViewImpl::selector;
  }-*/;

  native static void MultipleSelectorInputViewImpl_Div_selector(MultipleSelectorInputViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInputViewImpl::selector = value;
  }-*/;
}