package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayer.ModalFormDisplayerView;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayerViewImpl;

public class Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ModalFormDisplayerViewImpl> { public interface o_k_w_c_f_c_c_c_f_m_ModalFormDisplayerViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/crud/client/component/formDisplay/modal/ModalFormDisplayerViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ModalFormDisplayerViewImpl.class, "Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ModalFormDisplayerViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, ModalFormDisplayerView.class });
  }

  public ModalFormDisplayerViewImpl createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ModalFormDisplayerViewImpl instance = new ModalFormDisplayerViewImpl(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    o_k_w_c_f_c_c_c_f_m_ModalFormDisplayerViewImplTemplateResource templateForModalFormDisplayerViewImpl = GWT.create(o_k_w_c_f_c_c_c_f_m_ModalFormDisplayerViewImplTemplateResource.class);
    Element parentElementForTemplateOfModalFormDisplayerViewImpl = TemplateUtil.getRootTemplateParentElement(templateForModalFormDisplayerViewImpl.getContents().getText(), "org/kie/workbench/common/forms/crud/client/component/formDisplay/modal/ModalFormDisplayerViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/crud/client/component/formDisplay/modal/ModalFormDisplayerViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfModalFormDisplayerViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfModalFormDisplayerViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayerViewImpl", "org/kie/workbench/common/forms/crud/client/component/formDisplay/modal/ModalFormDisplayerViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ModalFormDisplayerViewImpl_SimplePanel_content(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("content", ModalFormDisplayerViewImpl_SimplePanel_content(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfModalFormDisplayerViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ModalFormDisplayerViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ModalFormDisplayerViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  public void invokePostConstructs(final ModalFormDisplayerViewImpl instance) {
    instance.initialize();
  }

  native static SimplePanel ModalFormDisplayerViewImpl_SimplePanel_content(ModalFormDisplayerViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayerViewImpl::content;
  }-*/;

  native static void ModalFormDisplayerViewImpl_SimplePanel_content(ModalFormDisplayerViewImpl instance, SimplePanel value) /*-{
    instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayerViewImpl::content = value;
  }-*/;
}