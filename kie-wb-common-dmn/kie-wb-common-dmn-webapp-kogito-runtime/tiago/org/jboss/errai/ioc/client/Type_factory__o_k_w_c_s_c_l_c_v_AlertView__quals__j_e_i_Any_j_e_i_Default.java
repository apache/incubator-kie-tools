package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
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
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.client.lienzo.components.views.AlertView;

public class Type_factory__o_k_w_c_s_c_l_c_v_AlertView__quals__j_e_i_Any_j_e_i_Default extends Factory<AlertView> { public interface o_k_w_c_s_c_l_c_v_AlertViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/client/lienzo/components/views/AlertView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_c_l_c_v_AlertView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AlertView.class, "Type_factory__o_k_w_c_s_c_l_c_v_AlertView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AlertView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public AlertView createInstance(final ContextManager contextManager) {
    final AlertView instance = new AlertView();
    setIncompleteInstance(instance);
    final Span AlertView_text = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AlertView_text);
    AlertView_Span_text(instance, AlertView_text);
    o_k_w_c_s_c_l_c_v_AlertViewTemplateResource templateForAlertView = GWT.create(o_k_w_c_s_c_l_c_v_AlertViewTemplateResource.class);
    Element parentElementForTemplateOfAlertView = TemplateUtil.getRootTemplateParentElement(templateForAlertView.getContents().getText(), "org/kie/workbench/common/stunner/client/lienzo/components/views/AlertView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/lienzo/components/views/AlertView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAlertView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAlertView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("text", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.views.AlertView", "org/kie/workbench/common/stunner/client/lienzo/components/views/AlertView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AlertView_Span_text(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "text");
    templateFieldsMap.put("text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AlertView_Span_text(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAlertView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((AlertView) instance, contextManager);
  }

  public void destroyInstanceHelper(final AlertView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Span AlertView_Span_text(AlertView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.views.AlertView::text;
  }-*/;

  native static void AlertView_Span_text(AlertView instance, Span value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.views.AlertView::text = value;
  }-*/;
}