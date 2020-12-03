package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.MouseRequestLifecycle;
import org.kie.workbench.common.stunner.core.client.command.RegistryAwareCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;

public class Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ApplicationCommandManager> { private class Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ApplicationCommandManager implements Proxy<ApplicationCommandManager> {
    private final ProxyHelper<ApplicationCommandManager> proxyHelper = new ProxyHelperImpl<ApplicationCommandManager>("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null);
    }

    public void initProxyProperties(final ApplicationCommandManager instance) {

    }

    public ApplicationCommandManager asBeanType() {
      return this;
    }

    public void setInstance(final ApplicationCommandManager instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public void start() {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.start();
      } else {
        super.start();
      }
    }

    @Override public void rollback() {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.rollback();
      } else {
        super.rollback();
      }
    }

    @Override public void complete() {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.complete();
      } else {
        super.complete();
      }
    }

    @Override public CommandResult allow(Command command) {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        final CommandResult retVal = proxiedInstance.allow(command);
        return retVal;
      } else {
        return super.allow(command);
      }
    }

    @Override public CommandResult allow(AbstractCanvasHandler context, Command command) {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        final CommandResult retVal = proxiedInstance.allow(context, command);
        return retVal;
      } else {
        return super.allow(context, command);
      }
    }

    @Override public CommandResult execute(Command command) {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        final CommandResult retVal = proxiedInstance.execute(command);
        return retVal;
      } else {
        return super.execute(command);
      }
    }

    @Override public CommandResult execute(AbstractCanvasHandler context, Command command) {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        final CommandResult retVal = proxiedInstance.execute(context, command);
        return retVal;
      } else {
        return super.execute(context, command);
      }
    }

    @Override public CommandResult undo() {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        final CommandResult retVal = proxiedInstance.undo();
        return retVal;
      } else {
        return super.undo();
      }
    }

    @Override public CommandResult undo(AbstractCanvasHandler context, Command command) {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        final CommandResult retVal = proxiedInstance.undo(context, command);
        return retVal;
      } else {
        return super.undo(context, command);
      }
    }

    @Override public CommandResult undo(AbstractCanvasHandler context) {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        final CommandResult retVal = proxiedInstance.undo(context);
        return retVal;
      } else {
        return super.undo(context);
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ApplicationCommandManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ApplicationCommandManager.class, "Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionCommandManager.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent", new AbstractCDIEventCallback<SessionDestroyedEvent>() {
      public void fireEvent(final SessionDestroyedEvent event) {
        final ApplicationCommandManager instance = Factory.maybeUnwrapProxy((ApplicationCommandManager) context.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default"));
        ApplicationCommandManager_onSessionDestroyed_SessionDestroyedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent []";
      }
    });
  }

  public ApplicationCommandManager createInstance(final ContextManager contextManager) {
    final ManagedInstance<RegistryAwareCommandManager> _commandManagerInstances_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { RegistryAwareCommandManager.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final SessionManager _sessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final MouseRequestLifecycle _lifecycle_1 = (MouseRequestLifecycle) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_MouseRequestLifecycle__quals__j_e_i_Any_j_e_i_Default");
    final ApplicationCommandManager instance = new ApplicationCommandManager(_sessionManager_0, _lifecycle_1, _commandManagerInstances_2);
    registerDependentScopedReference(instance, _commandManagerInstances_2);
    registerDependentScopedReference(instance, _lifecycle_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ApplicationCommandManager) instance, contextManager);
  }

  public void destroyInstanceHelper(final ApplicationCommandManager instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final ApplicationCommandManager instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager ([org.kie.workbench.common.stunner.core.client.api.SessionManager, org.kie.workbench.common.stunner.core.client.command.MouseRequestLifecycle, org.jboss.errai.ioc.client.api.ManagedInstance])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ApplicationCommandManager> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void ApplicationCommandManager_onSessionDestroyed_SessionDestroyedEvent(ApplicationCommandManager instance, SessionDestroyedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager::onSessionDestroyed(Lorg/kie/workbench/common/stunner/core/client/session/event/SessionDestroyedEvent;)(a0);
  }-*/;
}