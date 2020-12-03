package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
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
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayerView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayerViewImpl;

public class Type_factory__o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ConfigErrorDisplayerViewImpl> { public interface o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayerViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/configError/ConfigErrorDisplayerViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConfigErrorDisplayerViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConfigErrorDisplayerViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, ConfigErrorDisplayerView.class });
  }

  public ConfigErrorDisplayerViewImpl createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ConfigErrorDisplayerViewImpl instance = new ConfigErrorDisplayerViewImpl(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayerViewImplTemplateResource templateForConfigErrorDisplayerViewImpl = GWT.create(o_k_w_c_f_d_c_r_f_i_c_ConfigErrorDisplayerViewImplTemplateResource.class);
    Element parentElementForTemplateOfConfigErrorDisplayerViewImpl = TemplateUtil.getRootTemplateParentElement(templateForConfigErrorDisplayerViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/configError/ConfigErrorDisplayerViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/configError/ConfigErrorDisplayerViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfConfigErrorDisplayerViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfConfigErrorDisplayerViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("list", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayerViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/configError/ConfigErrorDisplayerViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(ConfigErrorDisplayerViewImpl_UListElement_list(instance));
      }
    }, dataFieldElements, dataFieldMetas, "list");
    templateFieldsMap.put("list", ElementWrapperWidget.getWidget(ConfigErrorDisplayerViewImpl_UListElement_list(instance)));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfConfigErrorDisplayerViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ConfigErrorDisplayerViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ConfigErrorDisplayerViewImpl instance, final ContextManager contextManager) {
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(ConfigErrorDisplayerViewImpl_UListElement_list(instance)));
    TemplateUtil.cleanupWidget(instance);
  }

  native static UListElement ConfigErrorDisplayerViewImpl_UListElement_list(ConfigErrorDisplayerViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayerViewImpl::list;
  }-*/;

  native static void ConfigErrorDisplayerViewImpl_UListElement_list(ConfigErrorDisplayerViewImpl instance, UListElement value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayerViewImpl::list = value;
  }-*/;
}