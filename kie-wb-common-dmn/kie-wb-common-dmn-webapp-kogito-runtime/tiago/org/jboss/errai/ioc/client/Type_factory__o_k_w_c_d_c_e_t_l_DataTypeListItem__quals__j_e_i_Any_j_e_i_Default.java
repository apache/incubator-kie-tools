package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.DataTypeChangedEvent;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelect;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeEditModeToggleEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.DataTypeConfirmation;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint;
import org.kie.workbench.common.dmn.client.editors.types.listview.validation.DataTypeNameFormatValidator;

public class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItem__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListItem> { public Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeListItem.class, "Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeListItem.class, Object.class });
  }

  public DataTypeListItem createInstance(final ContextManager contextManager) {
    final Event<DataTypeChangedEvent> _dataTypeChangedEvent_8 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DataTypeChangedEvent.class }, new Annotation[] { });
    final DataTypeNameFormatValidator _nameFormatValidator_6 = (DataTypeNameFormatValidator) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_v_DataTypeNameFormatValidator__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConfirmation _confirmation_5 = (DataTypeConfirmation) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConfirmation__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeManager _dataTypeManager_4 = (DataTypeManager) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeConstraint _dataTypeConstraintComponent_2 = (DataTypeConstraint) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraint__quals__j_e_i_Any_j_e_i_Default");
    final Event<DataTypeEditModeToggleEvent> _editModeToggleEvent_7 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DataTypeEditModeToggleEvent.class }, new Annotation[] { });
    final View _view_0 = (DataTypeListItemView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListItemView__quals__j_e_i_Any_j_e_i_Default");
    final SmallSwitchComponent _dataTypeListComponent_3 = (SmallSwitchComponent) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponent__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeSelect _dataTypeSelectComponent_1 = (DataTypeSelect) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelect__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeListItem instance = new DataTypeListItem(_view_0, _dataTypeSelectComponent_1, _dataTypeConstraintComponent_2, _dataTypeListComponent_3, _dataTypeManager_4, _confirmation_5, _nameFormatValidator_6, _editModeToggleEvent_7, _dataTypeChangedEvent_8);
    registerDependentScopedReference(instance, _dataTypeChangedEvent_8);
    registerDependentScopedReference(instance, _nameFormatValidator_6);
    registerDependentScopedReference(instance, _confirmation_5);
    registerDependentScopedReference(instance, _dataTypeManager_4);
    registerDependentScopedReference(instance, _dataTypeConstraintComponent_2);
    registerDependentScopedReference(instance, _editModeToggleEvent_7);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _dataTypeListComponent_3);
    registerDependentScopedReference(instance, _dataTypeSelectComponent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DataTypeListItem instance) {
    DataTypeListItem_setup(instance);
  }

  public native static void DataTypeListItem_setup(DataTypeListItem instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem::setup()();
  }-*/;
}