package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServiceImpl;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.AbstractExportSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToRawFormatSessionCommand;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.exports.TextFileExport;

public class Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToRawFormatSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ExportToRawFormatSessionCommand> { public Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToRawFormatSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExportToRawFormatSessionCommand.class, "Type_factory__o_k_w_c_s_c_c_s_c_i_ExportToRawFormatSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExportToRawFormatSessionCommand.class, AbstractExportSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public ExportToRawFormatSessionCommand createInstance(final ContextManager contextManager) {
    final ClientDiagramService _clientDiagramService_0 = (ClientDiagramServiceImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_ClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    final ErrorPopupPresenter _errorPopupPresenter_1 = (ErrorPopupPresenter) contextManager.getInstance("Type_factory__o_u_c_w_w_c_ErrorPopupPresenter__quals__j_e_i_Any_j_e_i_Default");
    final TextFileExport _textFileExport_2 = (TextFileExport) contextManager.getInstance("Producer_factory__o_u_e_e_c_c_f_e_TextFileExport__quals__j_e_i_Any_j_e_i_Default");
    final ExportToRawFormatSessionCommand instance = new ExportToRawFormatSessionCommand(_clientDiagramService_0, _errorPopupPresenter_1, _textFileExport_2);
    registerDependentScopedReference(instance, _clientDiagramService_0);
    registerDependentScopedReference(instance, _textFileExport_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}