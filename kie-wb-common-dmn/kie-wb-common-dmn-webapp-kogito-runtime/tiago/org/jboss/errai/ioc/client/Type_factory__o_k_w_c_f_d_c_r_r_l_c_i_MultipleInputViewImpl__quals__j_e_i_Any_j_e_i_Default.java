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
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInputView;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInputViewImpl;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInputViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInputViewImpl> { public interface o_k_w_c_f_d_c_r_r_l_c_i_MultipleInputViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/MultipleInputViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInputViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultipleInputViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInputViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultipleInputViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, MultipleInputView.class, FormWidget.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class });
  }

  public MultipleInputViewImpl createInstance(final ContextManager contextManager) {
    final MultipleInputViewImpl instance = new MultipleInputViewImpl();
    setIncompleteInstance(instance);
    final Div MultipleInputViewImpl_container = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultipleInputViewImpl_container);
    MultipleInputViewImpl_Div_container(instance, MultipleInputViewImpl_container);
    o_k_w_c_f_d_c_r_r_l_c_i_MultipleInputViewImplTemplateResource templateForMultipleInputViewImpl = GWT.create(o_k_w_c_f_d_c_r_r_l_c_i_MultipleInputViewImplTemplateResource.class);
    Element parentElementForTemplateOfMultipleInputViewImpl = TemplateUtil.getRootTemplateParentElement(templateForMultipleInputViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/MultipleInputViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/MultipleInputViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleInputViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleInputViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("container", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInputViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/MultipleInputViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputViewImpl_Div_container(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "container");
    templateFieldsMap.put("container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultipleInputViewImpl_Div_container(instance))));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultipleInputViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MultipleInputViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final MultipleInputViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static Div MultipleInputViewImpl_Div_container(MultipleInputViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInputViewImpl::container;
  }-*/;

  native static void MultipleInputViewImpl_Div_container(MultipleInputViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInputViewImpl::container = value;
  }-*/;
}