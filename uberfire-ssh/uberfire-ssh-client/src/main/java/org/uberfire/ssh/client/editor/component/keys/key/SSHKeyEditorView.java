package org.uberfire.ssh.client.editor.component.keys.key;

import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ssh.service.shared.editor.PortableSSHPublicKey;

public interface SSHKeyEditorView extends UberElemental<SSHKeyEditorView.Presenter> {

    void clear();

    void render(PortableSSHPublicKey key);

    interface Presenter {

        void notifyDelete();
    }
}
