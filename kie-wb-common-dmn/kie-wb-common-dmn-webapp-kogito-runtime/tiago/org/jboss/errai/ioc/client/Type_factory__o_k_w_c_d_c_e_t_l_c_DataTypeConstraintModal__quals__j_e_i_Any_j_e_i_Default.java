package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModalView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpression;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRange;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;
import org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModal__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintModal> { public Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModal__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraintModal.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModal__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraintModal.class, Elemental2Modal.class, Object.class });
  }

  public DataTypeConstraintModal createInstance(final ContextManager contextManager) {
    final DataTypeConstraintEnumeration _constraintEnumeration_2 = (DataTypeConstraintEnumeration) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintEnumeration__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConstraintExpression _constraintExpression_3 = (DataTypeConstraintExpression) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_e_DataTypeConstraintExpression__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeShortcuts _dataTypeShortcuts_1 = (DataTypeShortcuts) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConstraintRange _constraintRange_4 = (DataTypeConstraintRange) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_r_DataTypeConstraintRange__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (DataTypeConstraintModalView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintModalView__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConstraintModal instance = new DataTypeConstraintModal(_view_0, _dataTypeShortcuts_1, _constraintEnumeration_2, _constraintExpression_3, _constraintRange_4);
    registerDependentScopedReference(instance, _constraintEnumeration_2);
    registerDependentScopedReference(instance, _constraintExpression_3);
    registerDependentScopedReference(instance, _constraintRange_4);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onDataTypeConstraintParserWarningEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent", new AbstractCDIEventCallback<DataTypeConstraintParserWarningEvent>() {
      public void fireEvent(final DataTypeConstraintParserWarningEvent event) {
        DataTypeConstraintModal_onDataTypeConstraintParserWarningEvent_DataTypeConstraintParserWarningEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypeConstraintModal) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypeConstraintModal instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onDataTypeConstraintParserWarningEventSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final DataTypeConstraintModal instance) {
    instance.setup();
  }

  public native static void DataTypeConstraintModal_onDataTypeConstraintParserWarningEvent_DataTypeConstraintParserWarningEvent(DataTypeConstraintModal instance, DataTypeConstraintParserWarningEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal::onDataTypeConstraintParserWarningEvent(Lorg/kie/workbench/common/dmn/client/editors/types/listview/constraint/common/DataTypeConstraintParserWarningEvent;)(a0);
  }-*/;
}