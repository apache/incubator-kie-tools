package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerPresenterView;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerPresenterViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimePickerPresenterViewImpl> { public interface o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/impl/DateTimePickerPresenterViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/impl/DateTimePickerPresenterViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DateTimePickerPresenterViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DateTimePickerPresenterViewImpl.class, Object.class, DateTimePickerPresenterView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource) GWT.create(o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public DateTimePickerPresenterViewImpl createInstance(final ContextManager contextManager) {
    final DateTimePickerPresenterViewImpl instance = new DateTimePickerPresenterViewImpl();
    setIncompleteInstance(instance);
    final Div DateTimePickerPresenterViewImpl_container = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DateTimePickerPresenterViewImpl_container);
    DateTimePickerPresenterViewImpl_Div_container(instance, DateTimePickerPresenterViewImpl_container);
    o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource templateForDateTimePickerPresenterViewImpl = GWT.create(o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenterViewImplTemplateResource.class);
    Element parentElementForTemplateOfDateTimePickerPresenterViewImpl = TemplateUtil.getRootTemplateParentElement(templateForDateTimePickerPresenterViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/impl/DateTimePickerPresenterViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/impl/DateTimePickerPresenterViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDateTimePickerPresenterViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDateTimePickerPresenterViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("container", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerPresenterViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/lov/creator/input/widget/impl/DateTimePickerPresenterViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DateTimePickerPresenterViewImpl_Div_container(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "container");
    templateFieldsMap.put("container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DateTimePickerPresenterViewImpl_Div_container(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDateTimePickerPresenterViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DateTimePickerPresenterViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DateTimePickerPresenterViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div DateTimePickerPresenterViewImpl_Div_container(DateTimePickerPresenterViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerPresenterViewImpl::container;
  }-*/;

  native static void DateTimePickerPresenterViewImpl_Div_container(DateTimePickerPresenterViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerPresenterViewImpl::container = value;
  }-*/;
}