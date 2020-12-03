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
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.widgets.client.popups.alert.AlertPopupView;
import org.kie.workbench.common.widgets.client.popups.alert.AlertPopupViewImpl;

public class Type_factory__o_k_w_c_w_c_p_a_AlertPopupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<AlertPopupViewImpl> { public interface o_k_w_c_w_c_p_a_AlertPopupViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/popups/alert/AlertPopupViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_p_a_AlertPopupViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AlertPopupViewImpl.class, "Type_factory__o_k_w_c_w_c_p_a_AlertPopupViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AlertPopupViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, AlertPopupView.class });
  }

  public AlertPopupViewImpl createInstance(final ContextManager contextManager) {
    final AlertPopupViewImpl instance = new AlertPopupViewImpl();
    setIncompleteInstance(instance);
    final Span AlertPopupViewImpl_alertMessage = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AlertPopupViewImpl_alertMessage);
    AlertPopupViewImpl_Span_alertMessage(instance, AlertPopupViewImpl_alertMessage);
    o_k_w_c_w_c_p_a_AlertPopupViewImplTemplateResource templateForAlertPopupViewImpl = GWT.create(o_k_w_c_w_c_p_a_AlertPopupViewImplTemplateResource.class);
    Element parentElementForTemplateOfAlertPopupViewImpl = TemplateUtil.getRootTemplateParentElement(templateForAlertPopupViewImpl.getContents().getText(), "org/kie/workbench/common/widgets/client/popups/alert/AlertPopupViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/popups/alert/AlertPopupViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAlertPopupViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAlertPopupViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("alert-message", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.alert.AlertPopupViewImpl", "org/kie/workbench/common/widgets/client/popups/alert/AlertPopupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AlertPopupViewImpl_Span_alertMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "alert-message");
    templateFieldsMap.put("alert-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AlertPopupViewImpl_Span_alertMessage(instance))));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAlertPopupViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((AlertPopupViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final AlertPopupViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  public void invokePostConstructs(final AlertPopupViewImpl instance) {
    AlertPopupViewImpl_setup(instance);
  }

  native static Span AlertPopupViewImpl_Span_alertMessage(AlertPopupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.alert.AlertPopupViewImpl::alertMessage;
  }-*/;

  native static void AlertPopupViewImpl_Span_alertMessage(AlertPopupViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.alert.AlertPopupViewImpl::alertMessage = value;
  }-*/;

  public native static void AlertPopupViewImpl_setup(AlertPopupViewImpl instance) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.alert.AlertPopupViewImpl::setup()();
  }-*/;
}