package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.select.SelectComponent.View;
import org.uberfire.ext.widgets.common.client.select.SelectView;

public class Type_factory__o_u_e_w_c_c_s_SelectView__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectView> { public interface o_u_e_w_c_c_s_SelectViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/select/SelectView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_w_c_c_s_SelectView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SelectView.class, "Type_factory__o_u_e_w_c_c_s_SelectView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SelectView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public SelectView createInstance(final ContextManager contextManager) {
    final SelectView instance = new SelectView();
    setIncompleteInstance(instance);
    final HTMLElement SelectView_selected = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    registerDependentScopedReference(instance, SelectView_selected);
    SelectView_HTMLElement_selected(instance, SelectView_selected);
    final HTMLUListElement SelectView_options = (HTMLUListElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLUListElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, SelectView_options);
    SelectView_HTMLUListElement_options(instance, SelectView_options);
    o_u_e_w_c_c_s_SelectViewTemplateResource templateForSelectView = GWT.create(o_u_e_w_c_c_s_SelectViewTemplateResource.class);
    Element parentElementForTemplateOfSelectView = TemplateUtil.getRootTemplateParentElement(templateForSelectView.getContents().getText(), "org/uberfire/ext/widgets/common/client/select/SelectView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/select/SelectView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSelectView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSelectView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("selected", new DataFieldMeta());
    dataFieldMetas.put("options", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.select.SelectView", "org/uberfire/ext/widgets/common/client/select/SelectView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectView_HTMLElement_selected(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "selected");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.select.SelectView", "org/uberfire/ext/widgets/common/client/select/SelectView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectView_HTMLUListElement_options(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "options");
    templateFieldsMap.put("selected", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectView_HTMLElement_selected(instance))));
    templateFieldsMap.put("options", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectView_HTMLUListElement_options(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSelectView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SelectView) instance, contextManager);
  }

  public void destroyInstanceHelper(final SelectView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLUListElement SelectView_HTMLUListElement_options(SelectView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.select.SelectView::options;
  }-*/;

  native static void SelectView_HTMLUListElement_options(SelectView instance, HTMLUListElement value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.select.SelectView::options = value;
  }-*/;

  native static HTMLElement SelectView_HTMLElement_selected(SelectView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.select.SelectView::selected;
  }-*/;

  native static void SelectView_HTMLElement_selected(SelectView instance, HTMLElement value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.select.SelectView::selected = value;
  }-*/;
}