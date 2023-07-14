package org.gwtbootstrap3.extras.select.client.ui.event;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2016 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Convenience interface used to implement all select handlers at once.
 *
 * @param <T> select value type
 */
public interface HasAllSelectHandlers<T> extends HasLoadedHandlers, HasValueChangeHandlers<T>,
        HasShowHandlers, HasShownHandlers, HasHideHandlers, HasHiddenHandlers,
        HasRenderedHandlers, HasRefreshedHandlers {

    /**
     * The {@link LoadedEvent} name
     */
    static final String LOADED_EVENT = "loaded.bs.select";

    /**
     * The {@link ChangedEvent} name
     */
    static final String CHANGED_EVENT = "changed.bs.select";

    /**
     * The {@link ShowEvent} name
     */
    static final String SHOW_EVENT = "show.bs.select";

    /**
     * The {@link ShownEvent} name
     */
    static final String SHOWN_EVENT = "shown.bs.select";

    /**
     * The {@link HideEvent} name
     */
    static final String HIDE_EVENT = "hide.bs.select";

    /**
     * The {@link HiddenEvent} name
     */
    static final String HIDDEN_EVENT = "hidden.bs.select";

    /**
     * The {@link RenderedEvent} name
     */
    static final String RENDERED_EVENT = "rendered.bs.select";

    /**
     * The {@link RefreshedEvent} name
     */
    static final String REFRESHED_EVENT = "refreshed.bs.select";

}
