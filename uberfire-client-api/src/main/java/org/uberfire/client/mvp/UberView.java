/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.mvp;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Interface to inject a Presenter into a View for MVP-based Widget
 * implementations. Due to limitations with CDI it is not possible to {@code @Inject}
 * the correct instance of a Presenter into a View.
 * <p>
 * Developers wishing to implement MVP-based Widgets are encouraged to have
 * their View implement this interface if they require access to the appropriate
 * Presenter.
 * 
 * @param <T> The Presenter type
 */
public interface UberView<T> extends IsWidget {

    void init( T presenter );
}
