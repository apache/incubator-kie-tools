package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PerformAutomaticLayoutCommand;
import org.uberfire.client.mvp.LockRequiredEvent;

public class Type_factory__o_k_w_c_s_c_c_s_c_i_PerformAutomaticLayoutCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<PerformAutomaticLayoutCommand> { public Type_factory__o_k_w_c_s_c_c_s_c_i_PerformAutomaticLayoutCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PerformAutomaticLayoutCommand.class, "Type_factory__o_k_w_c_s_c_c_s_c_i_PerformAutomaticLayoutCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PerformAutomaticLayoutCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public PerformAutomaticLayoutCommand createInstance(final ContextManager contextManager) {
    final LayoutHelper _layoutHelper_0 = (LayoutHelper) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_l_LayoutHelper__quals__j_e_i_Any_j_e_i_Default");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_1 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<LockRequiredEvent> _locker_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LockRequiredEvent.class }, new Annotation[] { });
    final PerformAutomaticLayoutCommand instance = new PerformAutomaticLayoutCommand(_layoutHelper_0, _sessionCommandManager_1, _locker_2);
    registerDependentScopedReference(instance, _layoutHelper_0);
    registerDependentScopedReference(instance, _locker_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}