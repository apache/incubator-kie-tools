package org.uberfire.client.views.pfly.widgets;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDocument;

@ApplicationScoped
public class Elemental2Producer {

    @Produces
    public HTMLDocument produceDocument() {
        return DomGlobal.document;
    }
}