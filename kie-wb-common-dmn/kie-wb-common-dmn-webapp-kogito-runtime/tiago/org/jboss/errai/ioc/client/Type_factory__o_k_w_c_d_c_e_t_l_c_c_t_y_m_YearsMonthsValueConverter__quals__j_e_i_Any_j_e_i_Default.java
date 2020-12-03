package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsValueConverter;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsValueConverter__quals__j_e_i_Any_j_e_i_Default extends Factory<YearsMonthsValueConverter> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsValueConverter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(YearsMonthsValueConverter.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsValueConverter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { YearsMonthsValueConverter.class, Object.class });
  }

  public YearsMonthsValueConverter createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final YearsMonthsValueConverter instance = new YearsMonthsValueConverter(_translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}