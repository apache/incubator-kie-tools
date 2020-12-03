package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverImpl;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView.Presenter;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls.Editor;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.PopupEditorControls;

public class Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ValueAndDataTypePopoverImpl> { public Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ValueAndDataTypePopoverImpl.class, "Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ValueAndDataTypePopoverImpl.class, Object.class, Presenter.class, Editor.class, PopupEditorControls.class, IsElement.class, CanBeClosedByKeyboard.class });
  }

  public ValueAndDataTypePopoverImpl createInstance(final ContextManager contextManager) {
    final ValueAndDataTypePopoverView _view_0 = (ValueAndDataTypePopoverViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_ValueAndDataTypePopoverViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ValueAndDataTypePopoverImpl instance = new ValueAndDataTypePopoverImpl(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onDataTypePageNavTabActiveEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent", new AbstractCDIEventCallback<DataTypePageTabActiveEvent>() {
      public void fireEvent(final DataTypePageTabActiveEvent event) {
        instance.onDataTypePageNavTabActiveEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ValueAndDataTypePopoverImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ValueAndDataTypePopoverImpl instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onDataTypePageNavTabActiveEventSubscription", Subscription.class)).remove();
  }
}