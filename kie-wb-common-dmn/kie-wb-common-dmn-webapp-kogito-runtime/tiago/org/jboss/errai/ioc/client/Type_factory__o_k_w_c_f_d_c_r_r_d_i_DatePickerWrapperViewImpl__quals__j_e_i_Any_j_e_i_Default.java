package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
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
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.common.rendering.client.widgets.FormWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperView;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl;
import org.uberfire.client.views.pfly.widgets.JQueryProducer.JQuery;
import org.uberfire.client.views.pfly.widgets.Popover;

public class Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DatePickerWrapperViewImpl> { public interface o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapperViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/date/input/DatePickerWrapperViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DatePickerWrapperViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DatePickerWrapperViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, DatePickerWrapperView.class, FormWidget.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class });
  }

  public DatePickerWrapperViewImpl createInstance(final ContextManager contextManager) {
    final DatePickerWrapperViewImpl instance = new DatePickerWrapperViewImpl();
    setIncompleteInstance(instance);
    final TranslationService DatePickerWrapperViewImpl_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DatePickerWrapperViewImpl_translationService);
    DatePickerWrapperViewImpl_TranslationService_translationService(instance, DatePickerWrapperViewImpl_translationService);
    final JQuery DatePickerWrapperViewImpl_jQueryPopover = (JQuery) contextManager.getInstance("Producer_factory__o_u_c_v_p_w_JQueryProducer_JQuery__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DatePickerWrapperViewImpl_jQueryPopover);
    DatePickerWrapperViewImpl_JQuery_jQueryPopover(instance, DatePickerWrapperViewImpl_jQueryPopover);
    final Span DatePickerWrapperViewImpl_selector = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DatePickerWrapperViewImpl_selector);
    DatePickerWrapperViewImpl_Span_selector(instance, DatePickerWrapperViewImpl_selector);
    final Button DatePickerWrapperViewImpl_clearBtn = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DatePickerWrapperViewImpl_clearBtn);
    DatePickerWrapperViewImpl_Button_clearBtn(instance, DatePickerWrapperViewImpl_clearBtn);
    final Button DatePickerWrapperViewImpl_showCalendarBtn = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DatePickerWrapperViewImpl_showCalendarBtn);
    DatePickerWrapperViewImpl_Button_showCalendarBtn(instance, DatePickerWrapperViewImpl_showCalendarBtn);
    o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapperViewImplTemplateResource templateForDatePickerWrapperViewImpl = GWT.create(o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapperViewImplTemplateResource.class);
    Element parentElementForTemplateOfDatePickerWrapperViewImpl = TemplateUtil.getRootTemplateParentElement(templateForDatePickerWrapperViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/date/input/DatePickerWrapperViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/renderers/date/input/DatePickerWrapperViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDatePickerWrapperViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDatePickerWrapperViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("selector", new DataFieldMeta());
    dataFieldMetas.put("clearBtn", new DataFieldMeta());
    dataFieldMetas.put("showCalendarBtn", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/date/input/DatePickerWrapperViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DatePickerWrapperViewImpl_Span_selector(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "selector");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/date/input/DatePickerWrapperViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DatePickerWrapperViewImpl_Button_clearBtn(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "clearBtn");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/renderers/date/input/DatePickerWrapperViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DatePickerWrapperViewImpl_Button_showCalendarBtn(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "showCalendarBtn");
    templateFieldsMap.put("selector", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DatePickerWrapperViewImpl_Span_selector(instance))));
    templateFieldsMap.put("clearBtn", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DatePickerWrapperViewImpl_Button_clearBtn(instance))));
    templateFieldsMap.put("showCalendarBtn", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DatePickerWrapperViewImpl_Button_showCalendarBtn(instance))));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDatePickerWrapperViewImpl), templateFieldsMap.values());
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("showCalendarBtn"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.onShowCalendar(event);
      }
    }, 1);
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("clearBtn"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.onClear(event);
      }
    }, 1);
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DatePickerWrapperViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DatePickerWrapperViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static Span DatePickerWrapperViewImpl_Span_selector(DatePickerWrapperViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl::selector;
  }-*/;

  native static void DatePickerWrapperViewImpl_Span_selector(DatePickerWrapperViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl::selector = value;
  }-*/;

  native static Button DatePickerWrapperViewImpl_Button_clearBtn(DatePickerWrapperViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl::clearBtn;
  }-*/;

  native static void DatePickerWrapperViewImpl_Button_clearBtn(DatePickerWrapperViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl::clearBtn = value;
  }-*/;

  native static TranslationService DatePickerWrapperViewImpl_TranslationService_translationService(DatePickerWrapperViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl::translationService;
  }-*/;

  native static void DatePickerWrapperViewImpl_TranslationService_translationService(DatePickerWrapperViewImpl instance, TranslationService value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl::translationService = value;
  }-*/;

  native static JQuery DatePickerWrapperViewImpl_JQuery_jQueryPopover(DatePickerWrapperViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl::jQueryPopover;
  }-*/;

  native static void DatePickerWrapperViewImpl_JQuery_jQueryPopover(DatePickerWrapperViewImpl instance, JQuery<Popover> value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl::jQueryPopover = value;
  }-*/;

  native static Button DatePickerWrapperViewImpl_Button_showCalendarBtn(DatePickerWrapperViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl::showCalendarBtn;
  }-*/;

  native static void DatePickerWrapperViewImpl_Button_showCalendarBtn(DatePickerWrapperViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl::showCalendarBtn = value;
  }-*/;
}