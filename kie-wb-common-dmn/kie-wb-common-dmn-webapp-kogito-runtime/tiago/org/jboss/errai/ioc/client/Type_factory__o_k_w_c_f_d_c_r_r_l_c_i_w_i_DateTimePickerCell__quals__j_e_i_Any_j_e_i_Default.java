package org.jboss.errai.ioc.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerCell;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerPresenter;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerCell__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimePickerCell> { public Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerCell__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DateTimePickerCell.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerCell__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DateTimePickerCell.class, AbstractEditableCell.class, AbstractCell.class, Object.class, Cell.class });
  }

  public DateTimePickerCell createInstance(final ContextManager contextManager) {
    final DateTimePickerPresenter _dateTimePicker_0 = (DateTimePickerPresenter) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateTimePickerPresenter__quals__j_e_i_Any_j_e_i_Default");
    final DateTimePickerCell instance = new DateTimePickerCell(_dateTimePicker_0);
    registerDependentScopedReference(instance, _dateTimePicker_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DateTimePickerCell instance) {
    instance.init();
  }
}