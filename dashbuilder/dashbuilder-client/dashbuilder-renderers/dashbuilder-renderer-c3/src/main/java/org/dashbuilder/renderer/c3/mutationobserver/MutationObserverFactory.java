package org.dashbuilder.renderer.c3.mutationobserver;

import javax.enterprise.context.ApplicationScoped;

import elemental2.dom.DomGlobal;
import elemental2.dom.MutationObserver;
import elemental2.dom.MutationObserver.MutationObserverCallbackFn;
import elemental2.dom.MutationObserverInit;
import elemental2.dom.Node;
import jsinterop.base.Js;

@ApplicationScoped
public class MutationObserverFactory {
    
    public MutationObserverInit mutationObserverInit() {
        return MutationObserverInitWrapper.create();
    }
    
    public MutationObserver mutationObserver(MutationObserverCallbackFn callback) {
        return new MutationObserver(callback);
    }

}
