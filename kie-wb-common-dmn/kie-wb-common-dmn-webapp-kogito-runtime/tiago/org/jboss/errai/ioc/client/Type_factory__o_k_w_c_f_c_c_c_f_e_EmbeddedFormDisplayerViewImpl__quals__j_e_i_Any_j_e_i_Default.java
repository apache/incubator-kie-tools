package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
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
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayer.EmbeddedFormDisplayerView;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl;

public class Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<EmbeddedFormDisplayerViewImpl> { public interface o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayerViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/crud/client/component/formDisplay/embedded/EmbeddedFormDisplayerViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(EmbeddedFormDisplayerViewImpl.class, "Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { EmbeddedFormDisplayerViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, EmbeddedFormDisplayerView.class });
  }

  public EmbeddedFormDisplayerViewImpl createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final EmbeddedFormDisplayerViewImpl instance = new EmbeddedFormDisplayerViewImpl(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    final Button EmbeddedFormDisplayerViewImpl_cancel = (Button) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, EmbeddedFormDisplayerViewImpl_cancel);
    EmbeddedFormDisplayerViewImpl_Button_cancel(instance, EmbeddedFormDisplayerViewImpl_cancel);
    final Button EmbeddedFormDisplayerViewImpl_accept = (Button) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, EmbeddedFormDisplayerViewImpl_accept);
    EmbeddedFormDisplayerViewImpl_Button_accept(instance, EmbeddedFormDisplayerViewImpl_accept);
    o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayerViewImplTemplateResource templateForEmbeddedFormDisplayerViewImpl = GWT.create(o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayerViewImplTemplateResource.class);
    Element parentElementForTemplateOfEmbeddedFormDisplayerViewImpl = TemplateUtil.getRootTemplateParentElement(templateForEmbeddedFormDisplayerViewImpl.getContents().getText(), "org/kie/workbench/common/forms/crud/client/component/formDisplay/embedded/EmbeddedFormDisplayerViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/crud/client/component/formDisplay/embedded/EmbeddedFormDisplayerViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfEmbeddedFormDisplayerViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfEmbeddedFormDisplayerViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("heading", new DataFieldMeta());
    dataFieldMetas.put("content", new DataFieldMeta());
    dataFieldMetas.put("accept", new DataFieldMeta());
    dataFieldMetas.put("cancel", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl", "org/kie/workbench/common/forms/crud/client/component/formDisplay/embedded/EmbeddedFormDisplayerViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(EmbeddedFormDisplayerViewImpl_Element_heading(instance));
      }
    }, dataFieldElements, dataFieldMetas, "heading");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl", "org/kie/workbench/common/forms/crud/client/component/formDisplay/embedded/EmbeddedFormDisplayerViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return EmbeddedFormDisplayerViewImpl_SimplePanel_content(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "content");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl", "org/kie/workbench/common/forms/crud/client/component/formDisplay/embedded/EmbeddedFormDisplayerViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return EmbeddedFormDisplayerViewImpl_Button_accept(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "accept");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl", "org/kie/workbench/common/forms/crud/client/component/formDisplay/embedded/EmbeddedFormDisplayerViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return EmbeddedFormDisplayerViewImpl_Button_cancel(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "cancel");
    templateFieldsMap.put("heading", ElementWrapperWidget.getWidget(EmbeddedFormDisplayerViewImpl_Element_heading(instance)));
    templateFieldsMap.put("content", EmbeddedFormDisplayerViewImpl_SimplePanel_content(instance).asWidget());
    templateFieldsMap.put("accept", EmbeddedFormDisplayerViewImpl_Button_accept(instance).asWidget());
    templateFieldsMap.put("cancel", EmbeddedFormDisplayerViewImpl_Button_cancel(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfEmbeddedFormDisplayerViewImpl), templateFieldsMap.values());
    ((HasClickHandlers) templateFieldsMap.get("accept")).addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.doAccept(event);
      }
    });
    ((HasClickHandlers) templateFieldsMap.get("cancel")).addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.doCancel(event);
      }
    });
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((EmbeddedFormDisplayerViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final EmbeddedFormDisplayerViewImpl instance, final ContextManager contextManager) {
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(EmbeddedFormDisplayerViewImpl_Element_heading(instance)));
    TemplateUtil.cleanupWidget(instance);
  }

  public void invokePostConstructs(final EmbeddedFormDisplayerViewImpl instance) {
    EmbeddedFormDisplayerViewImpl_initialize(instance);
  }

  native static SimplePanel EmbeddedFormDisplayerViewImpl_SimplePanel_content(EmbeddedFormDisplayerViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl::content;
  }-*/;

  native static void EmbeddedFormDisplayerViewImpl_SimplePanel_content(EmbeddedFormDisplayerViewImpl instance, SimplePanel value) /*-{
    instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl::content = value;
  }-*/;

  native static Button EmbeddedFormDisplayerViewImpl_Button_cancel(EmbeddedFormDisplayerViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl::cancel;
  }-*/;

  native static void EmbeddedFormDisplayerViewImpl_Button_cancel(EmbeddedFormDisplayerViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl::cancel = value;
  }-*/;

  native static Element EmbeddedFormDisplayerViewImpl_Element_heading(EmbeddedFormDisplayerViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl::heading;
  }-*/;

  native static void EmbeddedFormDisplayerViewImpl_Element_heading(EmbeddedFormDisplayerViewImpl instance, Element value) /*-{
    instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl::heading = value;
  }-*/;

  native static Button EmbeddedFormDisplayerViewImpl_Button_accept(EmbeddedFormDisplayerViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl::accept;
  }-*/;

  native static void EmbeddedFormDisplayerViewImpl_Button_accept(EmbeddedFormDisplayerViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl::accept = value;
  }-*/;

  public native static void EmbeddedFormDisplayerViewImpl_initialize(EmbeddedFormDisplayerViewImpl instance) /*-{
    instance.@org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl::initialize()();
  }-*/;
}