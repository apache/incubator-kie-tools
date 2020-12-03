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
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.common.rendering.client.widgets.FormWidget;
import org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAheadView;
import org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAheadViewImpl;

public class Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAheadViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<BindableTypeAheadViewImpl> { public interface o_k_w_c_f_c_r_c_w_t_BindableTypeAheadViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/common/rendering/client/widgets/typeahead/BindableTypeAheadViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAheadViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BindableTypeAheadViewImpl.class, "Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAheadViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BindableTypeAheadViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, BindableTypeAheadView.class, FormWidget.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class });
  }

  public BindableTypeAheadViewImpl createInstance(final ContextManager contextManager) {
    final BindableTypeAheadViewImpl instance = new BindableTypeAheadViewImpl();
    setIncompleteInstance(instance);
    final FlowPanel BindableTypeAheadViewImpl_content = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_g_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, BindableTypeAheadViewImpl_content);
    BindableTypeAheadViewImpl_FlowPanel_content(instance, BindableTypeAheadViewImpl_content);
    o_k_w_c_f_c_r_c_w_t_BindableTypeAheadViewImplTemplateResource templateForBindableTypeAheadViewImpl = GWT.create(o_k_w_c_f_c_r_c_w_t_BindableTypeAheadViewImplTemplateResource.class);
    Element parentElementForTemplateOfBindableTypeAheadViewImpl = TemplateUtil.getRootTemplateParentElement(templateForBindableTypeAheadViewImpl.getContents().getText(), "org/kie/workbench/common/forms/common/rendering/client/widgets/typeahead/BindableTypeAheadViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/common/rendering/client/widgets/typeahead/BindableTypeAheadViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfBindableTypeAheadViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfBindableTypeAheadViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAheadViewImpl", "org/kie/workbench/common/forms/common/rendering/client/widgets/typeahead/BindableTypeAheadViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return BindableTypeAheadViewImpl_FlowPanel_content(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("content", BindableTypeAheadViewImpl_FlowPanel_content(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfBindableTypeAheadViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((BindableTypeAheadViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final BindableTypeAheadViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static FlowPanel BindableTypeAheadViewImpl_FlowPanel_content(BindableTypeAheadViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAheadViewImpl::content;
  }-*/;

  native static void BindableTypeAheadViewImpl_FlowPanel_content(BindableTypeAheadViewImpl instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAheadViewImpl::content = value;
  }-*/;
}