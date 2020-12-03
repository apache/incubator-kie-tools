package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNLayoutHelper;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNPerformAutomaticLayoutCommand;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PerformAutomaticLayoutCommand;
import org.uberfire.client.mvp.LockRequiredEvent;

public class Type_factory__o_k_w_c_d_c_w_t_DMNPerformAutomaticLayoutCommand__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNPerformAutomaticLayoutCommand> { public Type_factory__o_k_w_c_d_c_w_t_DMNPerformAutomaticLayoutCommand__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNPerformAutomaticLayoutCommand.class, "Type_factory__o_k_w_c_d_c_w_t_DMNPerformAutomaticLayoutCommand__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNPerformAutomaticLayoutCommand.class, PerformAutomaticLayoutCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DMNPerformAutomaticLayoutCommand createInstance(final ContextManager contextManager) {
    final Event<LockRequiredEvent> _locker_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { LockRequiredEvent.class }, new Annotation[] { });
    final DMNLayoutHelper _layoutHelper_0 = (DMNLayoutHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_t_DMNLayoutHelper__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_1 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNPerformAutomaticLayoutCommand instance = new DMNPerformAutomaticLayoutCommand(_layoutHelper_0, _sessionCommandManager_1, _locker_2);
    registerDependentScopedReference(instance, _locker_2);
    registerDependentScopedReference(instance, _layoutHelper_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}