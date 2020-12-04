package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.views.pfly.widgets.DateRangePicker;
import org.uberfire.client.views.pfly.widgets.DateRangePicker.JQueryDateRangePickerElement;
import org.uberfire.client.views.pfly.widgets.JQueryProducer.JQuery;

public class Type_factory__o_u_c_v_p_w_DateRangePicker__quals__j_e_i_Any_j_e_i_Default extends Factory<DateRangePicker> { public Type_factory__o_u_c_v_p_w_DateRangePicker__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DateRangePicker.class, "Type_factory__o_u_c_v_p_w_DateRangePicker__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DateRangePicker.class, Object.class, IsElement.class });
  }

  public DateRangePicker createInstance(final ContextManager contextManager) {
    final DateRangePicker instance = new DateRangePicker();
    setIncompleteInstance(instance);
    final TextInput DateRangePicker_input = (TextInput) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_TextInput__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DateRangePicker_input);
    DateRangePicker_TextInput_input(instance, DateRangePicker_input);
    final TranslationService DateRangePicker_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DateRangePicker_translationService);
    DateRangePicker_TranslationService_translationService(instance, DateRangePicker_translationService);
    final JQuery DateRangePicker_jQuery = (JQuery) contextManager.getInstance("Producer_factory__o_u_c_v_p_w_JQueryProducer_JQuery__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DateRangePicker_jQuery);
    DateRangePicker_JQuery_jQuery(instance, DateRangePicker_jQuery);
    setIncompleteInstance(null);
    return instance;
  }

  native static TranslationService DateRangePicker_TranslationService_translationService(DateRangePicker instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.DateRangePicker::translationService;
  }-*/;

  native static void DateRangePicker_TranslationService_translationService(DateRangePicker instance, TranslationService value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.DateRangePicker::translationService = value;
  }-*/;

  native static JQuery DateRangePicker_JQuery_jQuery(DateRangePicker instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.DateRangePicker::jQuery;
  }-*/;

  native static void DateRangePicker_JQuery_jQuery(DateRangePicker instance, JQuery<JQueryDateRangePickerElement> value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.DateRangePicker::jQuery = value;
  }-*/;

  native static TextInput DateRangePicker_TextInput_input(DateRangePicker instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.DateRangePicker::input;
  }-*/;

  native static void DateRangePicker_TextInput_input(DateRangePicker instance, TextInput value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.DateRangePicker::input = value;
  }-*/;
}