package org.uberfire.ssh.client.editor.component.keys.key;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.mvp.Command;
import org.uberfire.ssh.service.shared.editor.PortableSSHPublicKey;

@Dependent
public class SSHKeyEditor implements IsElement,
                                     SSHKeyEditorView.Presenter {

    private final SSHKeyEditorView view;

    private PortableSSHPublicKey key;
    private Command onDelete;

    @Inject
    public SSHKeyEditor(SSHKeyEditorView view) {
        this.view = view;

        this.view.init(this);
    }

    public void render(PortableSSHPublicKey key, Command onDelete) {

        PortablePreconditions.checkNotNull("key", key);
        PortablePreconditions.checkNotNull("onDelete", onDelete);

        this.key = key;
        this.onDelete = onDelete;

        view.clear();

        view.render(key);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void notifyDelete() {
        if (onDelete != null) {
            onDelete.execute();
        }
    }

    @PreDestroy
    public void clear() {
        view.clear();
    }
}
