package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.graph.DMNElementsSynchronizer;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.session.NodeTextSetter;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;

public class Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNElementsSynchronizer> { private class Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNElementsSynchronizer implements Proxy<DMNElementsSynchronizer> {
    private final ProxyHelper<DMNElementsSynchronizer> proxyHelper = new ProxyHelperImpl<DMNElementsSynchronizer>("Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null);
    }

    public void initProxyProperties(final DMNElementsSynchronizer instance) {

    }

    public DMNElementsSynchronizer asBeanType() {
      return this;
    }

    public void setInstance(final DMNElementsSynchronizer instance) {
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

    @Override public void onExpressionEditorChanged(ExpressionEditorChanged event) {
      if (proxyHelper != null) {
        final DMNElementsSynchronizer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onExpressionEditorChanged(event);
      } else {
        super.onExpressionEditorChanged(event);
      }
    }

    @Override public void onPropertyChanged(FormFieldChanged event) {
      if (proxyHelper != null) {
        final DMNElementsSynchronizer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPropertyChanged(event);
      } else {
        super.onPropertyChanged(event);
      }
    }

    @Override public void synchronizeElementsFrom(DRGElement drgElement) {
      if (proxyHelper != null) {
        final DMNElementsSynchronizer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.synchronizeElementsFrom(drgElement);
      } else {
        super.synchronizeElementsFrom(drgElement);
      }
    }

    @Override public void synchronizeFromNode(Optional node) {
      if (proxyHelper != null) {
        final DMNElementsSynchronizer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.synchronizeFromNode(node);
      } else {
        super.synchronizeFromNode(node);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNElementsSynchronizer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNElementsSynchronizer.class, "Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNElementsSynchronizer.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged", new AbstractCDIEventCallback<ExpressionEditorChanged>() {
      public void fireEvent(final ExpressionEditorChanged event) {
        final DMNElementsSynchronizer instance = Factory.maybeUnwrapProxy((DMNElementsSynchronizer) context.getInstance("Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_Default"));
        instance.onExpressionEditorChanged(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged []";
      }
    });
    CDI.subscribeLocal("org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged", new AbstractCDIEventCallback<FormFieldChanged>() {
      public void fireEvent(final FormFieldChanged event) {
        final DMNElementsSynchronizer instance = Factory.maybeUnwrapProxy((DMNElementsSynchronizer) context.getInstance("Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_Default"));
        instance.onPropertyChanged(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged []";
      }
    });
  }

  public DMNElementsSynchronizer createInstance(final ContextManager contextManager) {
    final DMNGraphUtils _graphUtils_2 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final NodeTextSetter _nodeTextSetter_3 = (NodeTextSetter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_s_NodeTextSetter__quals__j_e_i_Any_j_e_i_Default");
    final Event<RefreshDecisionComponents> _refreshDecisionComponentsEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RefreshDecisionComponents.class }, new Annotation[] { });
    final DMNDiagramsSession _dmnDiagramsSession_0 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final DMNElementsSynchronizer instance = new DMNElementsSynchronizer(_dmnDiagramsSession_0, _refreshDecisionComponentsEvent_1, _graphUtils_2, _nodeTextSetter_3);
    registerDependentScopedReference(instance, _graphUtils_2);
    registerDependentScopedReference(instance, _nodeTextSetter_3);
    registerDependentScopedReference(instance, _refreshDecisionComponentsEvent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_g_DMNElementsSynchronizer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.graph.DMNElementsSynchronizer an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.graph.DMNElementsSynchronizer ([org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession, javax.enterprise.event.Event, org.kie.workbench.common.dmn.client.graph.DMNGraphUtils, org.kie.workbench.common.dmn.client.session.NodeTextSetter])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNElementsSynchronizer> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}