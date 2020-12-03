package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLInputElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelectorView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_c_BaseSelectorView__quals__j_e_i_Any_j_e_i_Default extends Factory<BaseSelectorView> { public interface o_k_w_c_d_c_e_t_l_c_c_t_c_BaseSelectorViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/common/BaseSelectorView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_c_BaseSelectorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BaseSelectorView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_c_BaseSelectorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BaseSelectorView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public BaseSelectorView createInstance(final ContextManager contextManager) {
    final HTMLInputElement _input_0 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final BaseSelectorView instance = new BaseSelectorView(_input_0);
    registerDependentScopedReference(instance, _input_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_c_t_c_BaseSelectorViewTemplateResource templateForBaseSelectorView = GWT.create(o_k_w_c_d_c_e_t_l_c_c_t_c_BaseSelectorViewTemplateResource.class);
    Element parentElementForTemplateOfBaseSelectorView = TemplateUtil.getRootTemplateParentElement(templateForBaseSelectorView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/common/BaseSelectorView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/common/BaseSelectorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfBaseSelectorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfBaseSelectorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("generic-input", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelectorView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/typed/common/BaseSelectorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseSelectorView_HTMLInputElement_input(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "generic-input");
    templateFieldsMap.put("generic-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseSelectorView_HTMLInputElement_input(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfBaseSelectorView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("generic-input"), new BlurHandler() {
      public void onBlur(BlurEvent event) {
        BaseSelectorView_onGenericInputBlur_BlurEvent(instance, event);
      }
    }, BlurEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((BaseSelectorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final BaseSelectorView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLInputElement BaseSelectorView_HTMLInputElement_input(BaseSelectorView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelectorView::input;
  }-*/;

  native static void BaseSelectorView_HTMLInputElement_input(BaseSelectorView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelectorView::input = value;
  }-*/;

  public native static void BaseSelectorView_onGenericInputBlur_BlurEvent(BaseSelectorView instance, BlurEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelectorView::onGenericInputBlur(Lcom/google/gwt/event/dom/client/BlurEvent;)(a0);
  }-*/;
}