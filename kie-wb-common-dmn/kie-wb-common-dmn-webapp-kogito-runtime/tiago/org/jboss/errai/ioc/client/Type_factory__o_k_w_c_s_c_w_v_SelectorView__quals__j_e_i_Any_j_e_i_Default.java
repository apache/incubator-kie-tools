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
import jsinterop.base.Js;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.client.widgets.views.SelectorView;

public class Type_factory__o_k_w_c_s_c_w_v_SelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectorView> { public interface o_k_w_c_s_c_w_v_SelectorViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/views/SelectorView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_c_w_v_SelectorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SelectorView.class, "Type_factory__o_k_w_c_s_c_w_v_SelectorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SelectorView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public SelectorView createInstance(final ContextManager contextManager) {
    final SelectorView instance = new SelectorView();
    setIncompleteInstance(instance);
    final Div SelectorView_selectorContainer = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, SelectorView_selectorContainer);
    SelectorView_Div_selectorContainer(instance, SelectorView_selectorContainer);
    final Select SelectorView_selectorInput = (Select) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Select__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, SelectorView_selectorInput);
    SelectorView_Select_selectorInput(instance, SelectorView_selectorInput);
    o_k_w_c_s_c_w_v_SelectorViewTemplateResource templateForSelectorView = GWT.create(o_k_w_c_s_c_w_v_SelectorViewTemplateResource.class);
    Element parentElementForTemplateOfSelectorView = TemplateUtil.getRootTemplateParentElement(templateForSelectorView.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/views/SelectorView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/views/SelectorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSelectorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSelectorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("selector-root", new DataFieldMeta());
    dataFieldMetas.put("selector-input", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.views.SelectorView", "org/kie/workbench/common/stunner/client/widgets/views/SelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectorView_Div_selectorContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "selector-root");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.views.SelectorView", "org/kie/workbench/common/stunner/client/widgets/views/SelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectorView_Select_selectorInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "selector-input");
    templateFieldsMap.put("selector-root", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectorView_Div_selectorContainer(instance))));
    templateFieldsMap.put("selector-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectorView_Select_selectorInput(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSelectorView), templateFieldsMap.values());
    final EventListener listenerForEventCallingOnValueChanged = new EventListener() {
      public void call(Event event) {
        SelectorView_onValueChanged_Event(instance, Js.cast(event));
      }
    };
    TemplateUtil.setupBrowserEventListener(instance, templateFieldsMap.get("selector-input"), listenerForEventCallingOnValueChanged, "change");
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SelectorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final SelectorView instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div SelectorView_Div_selectorContainer(SelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.views.SelectorView::selectorContainer;
  }-*/;

  native static void SelectorView_Div_selectorContainer(SelectorView instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.views.SelectorView::selectorContainer = value;
  }-*/;

  native static Select SelectorView_Select_selectorInput(SelectorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.views.SelectorView::selectorInput;
  }-*/;

  native static void SelectorView_Select_selectorInput(SelectorView instance, Select value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.views.SelectorView::selectorInput = value;
  }-*/;

  public native static void SelectorView_onValueChanged_Event(SelectorView instance, Event a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.views.SelectorView::onValueChanged(Lorg/jboss/errai/common/client/dom/Event;)(a0);
  }-*/;
}