package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLSelectElement;
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
import org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElement.View;
import org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElementView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElementView__quals__j_e_i_Any_j_e_i_Default extends Factory<KieMultipleSelectElementView> { public interface o_k_w_c_w_c_w_KieMultipleSelectElementViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/widget/KieMultipleSelectElementView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElementView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KieMultipleSelectElementView.class, "Type_factory__o_k_w_c_w_c_w_KieMultipleSelectElementView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KieMultipleSelectElementView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public KieMultipleSelectElementView createInstance(final ContextManager contextManager) {
    final KieMultipleSelectElementView instance = new KieMultipleSelectElementView();
    setIncompleteInstance(instance);
    final HTMLSelectElement KieMultipleSelectElementView_select = (HTMLSelectElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLSelectElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KieMultipleSelectElementView_select);
    KieMultipleSelectElementView_HTMLSelectElement_select(instance, KieMultipleSelectElementView_select);
    final HTMLDivElement KieMultipleSelectElementView_root = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KieMultipleSelectElementView_root);
    KieMultipleSelectElementView_HTMLDivElement_root(instance, KieMultipleSelectElementView_root);
    o_k_w_c_w_c_w_KieMultipleSelectElementViewTemplateResource templateForKieMultipleSelectElementView = GWT.create(o_k_w_c_w_c_w_KieMultipleSelectElementViewTemplateResource.class);
    Element parentElementForTemplateOfKieMultipleSelectElementView = TemplateUtil.getRootTemplateParentElement(templateForKieMultipleSelectElementView.getContents().getText(), "org/kie/workbench/common/widgets/client/widget/KieMultipleSelectElementView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/widget/KieMultipleSelectElementView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKieMultipleSelectElementView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKieMultipleSelectElementView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("root", new DataFieldMeta());
    dataFieldMetas.put("select", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElementView", "org/kie/workbench/common/widgets/client/widget/KieMultipleSelectElementView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KieMultipleSelectElementView_HTMLDivElement_root(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "root");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElementView", "org/kie/workbench/common/widgets/client/widget/KieMultipleSelectElementView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KieMultipleSelectElementView_HTMLSelectElement_select(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "select");
    templateFieldsMap.put("root", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KieMultipleSelectElementView_HTMLDivElement_root(instance))));
    templateFieldsMap.put("select", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KieMultipleSelectElementView_HTMLSelectElement_select(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKieMultipleSelectElementView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("select"), new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        KieMultipleSelectElementView_onSelectChanged_ChangeEvent(instance, event);
      }
    }, ChangeEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KieMultipleSelectElementView) instance, contextManager);
  }

  public void destroyInstanceHelper(final KieMultipleSelectElementView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement KieMultipleSelectElementView_HTMLDivElement_root(KieMultipleSelectElementView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElementView::root;
  }-*/;

  native static void KieMultipleSelectElementView_HTMLDivElement_root(KieMultipleSelectElementView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElementView::root = value;
  }-*/;

  native static HTMLSelectElement KieMultipleSelectElementView_HTMLSelectElement_select(KieMultipleSelectElementView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElementView::select;
  }-*/;

  native static void KieMultipleSelectElementView_HTMLSelectElement_select(KieMultipleSelectElementView instance, HTMLSelectElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElementView::select = value;
  }-*/;

  public native static void KieMultipleSelectElementView_onSelectChanged_ChangeEvent(KieMultipleSelectElementView instance, ChangeEvent a0) /*-{
    instance.@org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElementView::onSelectChanged(Lcom/google/gwt/event/dom/client/ChangeEvent;)(a0);
  }-*/;
}