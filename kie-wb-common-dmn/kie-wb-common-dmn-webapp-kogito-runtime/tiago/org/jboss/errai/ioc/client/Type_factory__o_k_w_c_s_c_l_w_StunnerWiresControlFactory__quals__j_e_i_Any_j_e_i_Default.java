package org.jboss.errai.ioc.client;

import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectionControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresConnectorControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresControlFactory;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresLayerIndex;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHighlight;
import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.lienzo.wires.StunnerWiresControlFactory;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasUnhighlightEvent;

public class Type_factory__o_k_w_c_s_c_l_w_StunnerWiresControlFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerWiresControlFactory> { private class Type_factory__o_k_w_c_s_c_l_w_StunnerWiresControlFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends StunnerWiresControlFactory implements Proxy<StunnerWiresControlFactory> {
    private final ProxyHelper<StunnerWiresControlFactory> proxyHelper = new ProxyHelperImpl<StunnerWiresControlFactory>("Type_factory__o_k_w_c_s_c_l_w_StunnerWiresControlFactory__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_l_w_StunnerWiresControlFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final StunnerWiresControlFactory instance) {

    }

    public StunnerWiresControlFactory asBeanType() {
      return this;
    }

    public void setInstance(final StunnerWiresControlFactory instance) {
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

    @Override public WiresShapeControl newShapeControl(WiresShape shape, WiresManager wiresManager) {
      if (proxyHelper != null) {
        final StunnerWiresControlFactory proxiedInstance = proxyHelper.getInstance(this);
        final WiresShapeControl retVal = proxiedInstance.newShapeControl(shape, wiresManager);
        return retVal;
      } else {
        return super.newShapeControl(shape, wiresManager);
      }
    }

    @Override public WiresConnectorControl newConnectorControl(WiresConnector connector, WiresManager wiresManager) {
      if (proxyHelper != null) {
        final StunnerWiresControlFactory proxiedInstance = proxyHelper.getInstance(this);
        final WiresConnectorControl retVal = proxiedInstance.newConnectorControl(connector, wiresManager);
        return retVal;
      } else {
        return super.newConnectorControl(connector, wiresManager);
      }
    }

    @Override public WiresConnectionControl newConnectionControl(WiresConnector connector, boolean headNotTail, WiresManager wiresManager) {
      if (proxyHelper != null) {
        final StunnerWiresControlFactory proxiedInstance = proxyHelper.getInstance(this);
        final WiresConnectionControl retVal = proxiedInstance.newConnectionControl(connector, headNotTail, wiresManager);
        return retVal;
      } else {
        return super.newConnectionControl(connector, headNotTail, wiresManager);
      }
    }

    @Override public WiresCompositeControl newCompositeControl(com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl.Context context, WiresManager wiresManager) {
      if (proxyHelper != null) {
        final StunnerWiresControlFactory proxiedInstance = proxyHelper.getInstance(this);
        final WiresCompositeControl retVal = proxiedInstance.newCompositeControl(context, wiresManager);
        return retVal;
      } else {
        return super.newCompositeControl(context, wiresManager);
      }
    }

    @Override public WiresShapeHighlight newShapeHighlight(WiresManager wiresManager) {
      if (proxyHelper != null) {
        final StunnerWiresControlFactory proxiedInstance = proxyHelper.getInstance(this);
        final WiresShapeHighlight retVal = proxiedInstance.newShapeHighlight(wiresManager);
        return retVal;
      } else {
        return super.newShapeHighlight(wiresManager);
      }
    }

    @Override public WiresLayerIndex newIndex(WiresManager manager) {
      if (proxyHelper != null) {
        final StunnerWiresControlFactory proxiedInstance = proxyHelper.getInstance(this);
        final WiresLayerIndex retVal = proxiedInstance.newIndex(manager);
        return retVal;
      } else {
        return super.newIndex(manager);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final StunnerWiresControlFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_l_w_StunnerWiresControlFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerWiresControlFactory.class, "Type_factory__o_k_w_c_s_c_l_w_StunnerWiresControlFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StunnerWiresControlFactory.class, Object.class, WiresControlFactory.class });
  }

  public StunnerWiresControlFactory createInstance(final ContextManager contextManager) {
    final Event<CanvasUnhighlightEvent> _unhighlightEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasUnhighlightEvent.class }, new Annotation[] { });
    final StunnerWiresControlFactory instance = new StunnerWiresControlFactory(_unhighlightEvent_0);
    registerDependentScopedReference(instance, _unhighlightEvent_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_l_w_StunnerWiresControlFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.client.lienzo.wires.StunnerWiresControlFactory an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.client.lienzo.wires.StunnerWiresControlFactory ([javax.enterprise.event.Event])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<StunnerWiresControlFactory> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}