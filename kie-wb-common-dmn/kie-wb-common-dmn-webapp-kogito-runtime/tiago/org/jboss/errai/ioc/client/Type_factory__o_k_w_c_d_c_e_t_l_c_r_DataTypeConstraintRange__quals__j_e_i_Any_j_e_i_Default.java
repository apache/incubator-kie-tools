package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRange;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRange.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRangeView;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientServicesProxyImpl;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRange__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintRange> { public Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRange__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraintRange.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRange__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraintRange.class, Object.class, DataTypeConstraintComponent.class });
  }

  public DataTypeConstraintRange createInstance(final ContextManager contextManager) {
    final View _view_0 = (DataTypeConstraintRangeView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRangeView__quals__j_e_i_Any_j_e_i_Default");
    final Event<DataTypeConstraintParserWarningEvent> _parserWarningEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DataTypeConstraintParserWarningEvent.class }, new Annotation[] { });
    final ConstraintPlaceholderHelper _placeholderHelper_1 = (ConstraintPlaceholderHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_ConstraintPlaceholderHelper__quals__j_e_i_Any_j_e_i_Default");
    final DMNClientServicesProxy _clientServicesProxy_2 = (DMNClientServicesProxyImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConstraintRange instance = new DataTypeConstraintRange(_view_0, _placeholderHelper_1, _clientServicesProxy_2, _parserWarningEvent_3);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _parserWarningEvent_3);
    registerDependentScopedReference(instance, _placeholderHelper_1);
    registerDependentScopedReference(instance, _clientServicesProxy_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DataTypeConstraintRange instance) {
    DataTypeConstraintRange_setup(instance);
  }

  public native static void DataTypeConstraintRange_setup(DataTypeConstraintRange instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRange::setup()();
  }-*/;
}