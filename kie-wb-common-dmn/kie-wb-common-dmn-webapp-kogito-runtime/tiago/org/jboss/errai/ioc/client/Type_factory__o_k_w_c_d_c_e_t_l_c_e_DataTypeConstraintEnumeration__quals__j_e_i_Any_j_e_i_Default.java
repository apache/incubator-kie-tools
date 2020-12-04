package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumerationView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItem;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientServicesProxyImpl;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintEnumeration__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintEnumeration> { public Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintEnumeration__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraintEnumeration.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintEnumeration__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraintEnumeration.class, Object.class, DataTypeConstraintComponent.class });
  }

  public DataTypeConstraintEnumeration createInstance(final ContextManager contextManager) {
    final DMNClientServicesProxy _clientServicesProxy_1 = (DMNClientServicesProxyImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<DataTypeConstraintParserWarningEvent> _parserWarningEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DataTypeConstraintParserWarningEvent.class }, new Annotation[] { });
    final ManagedInstance<DataTypeConstraintEnumerationItem> _enumerationItemInstances_4 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DataTypeConstraintEnumerationItem.class }, new Annotation[] { });
    final View _view_0 = (DataTypeConstraintEnumerationView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintEnumerationView__quals__j_e_i_Any_j_e_i_Default");
    final ScrollHelper _scrollHelper_2 = (ScrollHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConstraintEnumeration instance = new DataTypeConstraintEnumeration(_view_0, _clientServicesProxy_1, _scrollHelper_2, _parserWarningEvent_3, _enumerationItemInstances_4);
    registerDependentScopedReference(instance, _clientServicesProxy_1);
    registerDependentScopedReference(instance, _parserWarningEvent_3);
    registerDependentScopedReference(instance, _enumerationItemInstances_4);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DataTypeConstraintEnumeration instance) {
    DataTypeConstraintEnumeration_setup(instance);
  }

  public native static void DataTypeConstraintEnumeration_setup(DataTypeConstraintEnumeration instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration::setup()();
  }-*/;
}