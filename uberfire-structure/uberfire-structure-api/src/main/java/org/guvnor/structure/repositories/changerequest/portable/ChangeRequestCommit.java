package org.guvnor.structure.repositories.changerequest.portable;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.common.client.api.annotations.MapsTo;

@Portable
public class ChangeRequestCommit {

    private String id;
    private String message;

    public ChangeRequestCommit(@MapsTo("id") final String id,
                               @MapsTo("message") final String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}
