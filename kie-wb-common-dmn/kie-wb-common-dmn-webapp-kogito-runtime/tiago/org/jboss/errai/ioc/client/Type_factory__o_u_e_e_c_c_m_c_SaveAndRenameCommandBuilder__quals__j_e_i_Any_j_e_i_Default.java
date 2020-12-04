package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.RenameInProgressEvent;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.menu.common.SaveAndRenameCommandBuilder;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_u_e_e_c_c_m_c_SaveAndRenameCommandBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<SaveAndRenameCommandBuilder> { public Type_factory__o_u_e_e_c_c_m_c_SaveAndRenameCommandBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SaveAndRenameCommandBuilder.class, "Type_factory__o_u_e_e_c_c_m_c_SaveAndRenameCommandBuilder__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SaveAndRenameCommandBuilder.class, Object.class });
  }

  public SaveAndRenameCommandBuilder createInstance(final ContextManager contextManager) {
    final RenamePopUpPresenter _renamePopUpPresenter_0 = (RenamePopUpPresenter) contextManager.getInstance("Type_factory__o_u_e_e_c_c_f_p_RenamePopUpPresenter__quals__j_e_i_Any_j_e_i_Default");
    final BusyIndicatorView _busyIndicatorView_1 = (BusyIndicatorView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_c_BusyIndicatorView__quals__j_e_i_Any_j_e_i_Default");
    final Event<NotificationEvent> _notification_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    final Event<RenameInProgressEvent> _renameInProgressEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RenameInProgressEvent.class }, new Annotation[] { });
    final SaveAndRenameCommandBuilder instance = new SaveAndRenameCommandBuilder(_renamePopUpPresenter_0, _busyIndicatorView_1, _notification_2, _renameInProgressEvent_3);
    registerDependentScopedReference(instance, _renamePopUpPresenter_0);
    registerDependentScopedReference(instance, _busyIndicatorView_1);
    registerDependentScopedReference(instance, _notification_2);
    registerDependentScopedReference(instance, _renameInProgressEvent_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}