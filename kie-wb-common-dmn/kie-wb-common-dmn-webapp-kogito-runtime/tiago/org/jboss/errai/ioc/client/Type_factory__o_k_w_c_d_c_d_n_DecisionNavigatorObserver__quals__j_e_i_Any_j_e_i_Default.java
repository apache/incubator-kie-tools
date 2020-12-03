package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;

public class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorObserver> { private class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DecisionNavigatorObserver implements Proxy<DecisionNavigatorObserver> {
    private final ProxyHelper<DecisionNavigatorObserver> proxyHelper = new ProxyHelperImpl<DecisionNavigatorObserver>("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DecisionNavigatorObserver instance) {

    }

    public DecisionNavigatorObserver asBeanType() {
      return this;
    }

    public void setInstance(final DecisionNavigatorObserver instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public void init(DecisionNavigatorPresenter presenter) {
      if (proxyHelper != null) {
        final DecisionNavigatorObserver proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(presenter);
      } else {
        super.init(presenter);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DecisionNavigatorObserver proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionNavigatorObserver.class, "Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionNavigatorObserver.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent", new AbstractCDIEventCallback<CanvasClearEvent>() {
      public void fireEvent(final CanvasClearEvent event) {
        final DecisionNavigatorObserver instance = Factory.maybeUnwrapProxy((DecisionNavigatorObserver) context.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default"));
        DecisionNavigatorObserver_onCanvasClear_CanvasClearEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent", new AbstractCDIEventCallback<CanvasElementAddedEvent>() {
      public void fireEvent(final CanvasElementAddedEvent event) {
        final DecisionNavigatorObserver instance = Factory.maybeUnwrapProxy((DecisionNavigatorObserver) context.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default"));
        DecisionNavigatorObserver_onCanvasElementAdded_CanvasElementAddedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent", new AbstractCDIEventCallback<CanvasElementUpdatedEvent>() {
      public void fireEvent(final CanvasElementUpdatedEvent event) {
        final DecisionNavigatorObserver instance = Factory.maybeUnwrapProxy((DecisionNavigatorObserver) context.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default"));
        DecisionNavigatorObserver_onCanvasElementUpdated_CanvasElementUpdatedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent", new AbstractCDIEventCallback<CanvasElementRemovedEvent>() {
      public void fireEvent(final CanvasElementRemovedEvent event) {
        final DecisionNavigatorObserver instance = Factory.maybeUnwrapProxy((DecisionNavigatorObserver) context.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default"));
        DecisionNavigatorObserver_onCanvasElementRemoved_CanvasElementRemovedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.events.EditExpressionEvent", new AbstractCDIEventCallback<EditExpressionEvent>() {
      public void fireEvent(final EditExpressionEvent event) {
        final DecisionNavigatorObserver instance = Factory.maybeUnwrapProxy((DecisionNavigatorObserver) context.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default"));
        DecisionNavigatorObserver_onNestedElementSelected_EditExpressionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.events.EditExpressionEvent []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged", new AbstractCDIEventCallback<ExpressionEditorChanged>() {
      public void fireEvent(final ExpressionEditorChanged event) {
        final DecisionNavigatorObserver instance = Factory.maybeUnwrapProxy((DecisionNavigatorObserver) context.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default"));
        DecisionNavigatorObserver_onNestedElementAdded_ExpressionEditorChanged(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected", new AbstractCDIEventCallback<DMNDiagramSelected>() {
      public void fireEvent(final DMNDiagramSelected event) {
        final DecisionNavigatorObserver instance = Factory.maybeUnwrapProxy((DecisionNavigatorObserver) context.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_Default"));
        DecisionNavigatorObserver_onDMNDiagramSelected_DMNDiagramSelected(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected []";
      }
    });
  }

  public DecisionNavigatorObserver createInstance(final ContextManager contextManager) {
    final DecisionNavigatorObserver instance = new DecisionNavigatorObserver();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DecisionNavigatorObserver> proxyImpl = new Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorObserver__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void DecisionNavigatorObserver_onCanvasElementUpdated_CanvasElementUpdatedEvent(DecisionNavigatorObserver instance, CanvasElementUpdatedEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver::onCanvasElementUpdated(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CanvasElementUpdatedEvent;)(a0);
  }-*/;

  public native static void DecisionNavigatorObserver_onCanvasElementRemoved_CanvasElementRemovedEvent(DecisionNavigatorObserver instance, CanvasElementRemovedEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver::onCanvasElementRemoved(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CanvasElementRemovedEvent;)(a0);
  }-*/;

  public native static void DecisionNavigatorObserver_onDMNDiagramSelected_DMNDiagramSelected(DecisionNavigatorObserver instance, DMNDiagramSelected a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver::onDMNDiagramSelected(Lorg/kie/workbench/common/dmn/client/docks/navigator/drds/DMNDiagramSelected;)(a0);
  }-*/;

  public native static void DecisionNavigatorObserver_onCanvasClear_CanvasClearEvent(DecisionNavigatorObserver instance, CanvasClearEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver::onCanvasClear(Lorg/kie/workbench/common/stunner/core/client/canvas/event/CanvasClearEvent;)(a0);
  }-*/;

  public native static void DecisionNavigatorObserver_onCanvasElementAdded_CanvasElementAddedEvent(DecisionNavigatorObserver instance, CanvasElementAddedEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver::onCanvasElementAdded(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CanvasElementAddedEvent;)(a0);
  }-*/;

  public native static void DecisionNavigatorObserver_onNestedElementSelected_EditExpressionEvent(DecisionNavigatorObserver instance, EditExpressionEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver::onNestedElementSelected(Lorg/kie/workbench/common/dmn/client/events/EditExpressionEvent;)(a0);
  }-*/;

  public native static void DecisionNavigatorObserver_onNestedElementAdded_ExpressionEditorChanged(DecisionNavigatorObserver instance, ExpressionEditorChanged a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorObserver::onNestedElementAdded(Lorg/kie/workbench/common/dmn/client/widgets/grid/model/ExpressionEditorChanged;)(a0);
  }-*/;
}