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
import com.google.gwt.user.client.ui.FlowPanel;
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
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.crud.client.component.CrudComponent.CrudComponentView;
import org.kie.workbench.common.forms.crud.client.component.CrudComponentViewImpl;

public class Type_factory__o_k_w_c_f_c_c_c_CrudComponentViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CrudComponentViewImpl> { public interface o_k_w_c_f_c_c_c_CrudComponentViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/crud/client/component/CrudComponentViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_c_c_c_CrudComponentViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CrudComponentViewImpl.class, "Type_factory__o_k_w_c_f_c_c_c_CrudComponentViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CrudComponentViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, CrudComponentView.class });
  }

  public CrudComponentViewImpl createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final CrudComponentViewImpl instance = new CrudComponentViewImpl(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    final FlowPanel CrudComponentViewImpl_content = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__c_g_g_u_c_u_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CrudComponentViewImpl_content);
    CrudComponentViewImpl_FlowPanel_content(instance, CrudComponentViewImpl_content);
    o_k_w_c_f_c_c_c_CrudComponentViewImplTemplateResource templateForCrudComponentViewImpl = GWT.create(o_k_w_c_f_c_c_c_CrudComponentViewImplTemplateResource.class);
    Element parentElementForTemplateOfCrudComponentViewImpl = TemplateUtil.getRootTemplateParentElement(templateForCrudComponentViewImpl.getContents().getText(), "org/kie/workbench/common/forms/crud/client/component/CrudComponentViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/crud/client/component/CrudComponentViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCrudComponentViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCrudComponentViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.crud.client.component.CrudComponentViewImpl", "org/kie/workbench/common/forms/crud/client/component/CrudComponentViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return CrudComponentViewImpl_FlowPanel_content(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("content", CrudComponentViewImpl_FlowPanel_content(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCrudComponentViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CrudComponentViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final CrudComponentViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static FlowPanel CrudComponentViewImpl_FlowPanel_content(CrudComponentViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.crud.client.component.CrudComponentViewImpl::content;
  }-*/;

  native static void CrudComponentViewImpl_FlowPanel_content(CrudComponentViewImpl instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.forms.crud.client.component.CrudComponentViewImpl::content = value;
  }-*/;
}