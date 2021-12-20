/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.notification;

import java.util.Optional;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;

public final class ValidationSuccessNotification extends AbstractNotification<Void> {

    ValidationSuccessNotification(final String message,
                                  final NotificationContext context) {
        super(context,
              Type.INFO,
              message);
    }

    @Override
    public Optional<Void> getSource() {
        return Optional.empty();
    }

    public static class Builder {

        public static ValidationSuccessNotification build(final ClientTranslationService translationService,
                                                          final NotificationContext context) {
            final String message =
                    new SafeHtmlBuilder()
                            .appendEscaped(translationService
                                                   .getValue(CoreTranslationMessages.VALIDATION_SUCCESS))
                            .toSafeHtml().asString();
            return new ValidationSuccessNotification(message,
                                                     context);
        }
    }
}
