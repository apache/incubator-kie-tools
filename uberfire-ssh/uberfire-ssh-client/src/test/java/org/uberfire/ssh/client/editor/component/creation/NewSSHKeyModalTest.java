/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ssh.client.editor.component.creation;

import org.assertj.core.api.Assertions;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.CallerMock;
import org.uberfire.ssh.client.resources.i18n.AppformerSSHConstants;
import org.uberfire.ssh.service.backend.keystore.util.PublicKeyConverter;
import org.uberfire.ssh.service.shared.editor.SSHKeyEditorService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class NewSSHKeyModalTest {

    private static final String NAME = "name";
    private static final String EXISTING_KEY_NAME = "existing name";

    private static final String WRONG_KEY = "wrong key";
    private static final String WRONG_KEY_FORMAT = "ssh-rsa wrong key";
    private static final String VALID_KEY = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDNsKIMkhaI8iX69IKsux/LdgG3zP1wW5RNJz" +
            "bopy7BLqZEmqTZMIfaWEisuH5WZquG3tZ//yNrVNd0Jw5RYQ/fwkyVmmJi9Ir2bo5rex4jbkNwcWb8U57UpIt38JCjjKLCkiYSKNXzrJOm" +
            "tFsMOuHukoGJbSRLDV3VSmQVIbtrysz8CRCCg2bv2KZkTmKa50O4S0UpjEVeyuy/+sDqbKl9Jrhj0i7PFrB1hJhN4+7SnNDAr6OpdZd0EU" +
            "Ua1TNdDISsdetq9vWMnuYBQPlxHxXnJsJhvdIlLXW6ZfZpsjqxe8jfHsJtmFvD032w/B4kBfGZxQXbPoUUBdrGyrKb2FyAypdDxAotA1Rl" +
            "sq3S6PWBlp7RjpMYWZb02XqNrN6g6AJCh0uuWCK/jxO6S96MYFyJj7rqUgaRg7SEKwR2lhwWTzUxb5bxbNxsA4eUXnvSr0lqCwcjw3M5WQ" +
            "HocGn4VPjKZl7Jhqxu9evwF5siuZEDL4oK8NgPwAZxMYcFuefdPgpxA/wmqWAh6JPbXLstQlG24bTrxCIzsx7qEfhU65KQJaLi3kso4LA/" +
            "IDmPRHIFGNUbY3YOwfDpmH/fHFQNY/5uy5/0oICAv9M3QBEMvB2rWpWJT8j2CkISCSjzPNnB490uUv9cxNnLs8tDrOHlAnm+k0iXyJ4hjq" +
            "tXqSbLCLz2Jw== katy";

    private static final String EXISTING_KEY = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDmak4Wu23RZ6XmN94bOsqecZxuTa4RRhh" +
            "QmHmTZjMB7HM57/90u/B/gB/GhsPEu1nAXL0npY56tT/MPQ8vRm2C2W9A7CzN5+z5yyL3W01YZy3kzslk77CjULjfhrcfQSL3b2sPG5jv5" +
            "E5/nyC/swSytucwT/PE7aXTS9H6cHIKUdYPzIt94SHoBxWRIK7PJi9d+eLB+hmDzvbVa1ezu5a8yu2kcHi6NxxfI5iRj2rsceDTp0imC1j" +
            "MoC6ZDfBvZSxL9FXTMwFdNnmTlJveBtv9nAbnAvIWlilS0VOkdj1s3GxBxeZYAcKbcsK9sJzusptk5dxGsG2Z8vInaglN6OaOQ7b7tcomz" +
            "CYYwviGQ9gRX8sGsVrw39gsDIGYP2tA4bRr7ecHnlNg1b0HCchA5+QCDk4Hbz1UrnHmPA2Lg9c3WGm2qedvQdVJXuS3mlwYOqL40aXPs68" +
            "90PvFJUlpiVSznF50djPnwsMxJZEf1HdTXgZD1Bh54ogZf7czyUNfkNkE69yJDbTHjpQd0cKUQnu9tVxqmBzhX31yF4VcsMeADcf2Z8wlA" +
            "3n4LZnC/GwonYlq5+G93zJpFOkPhme8c2XuPuCXF795lsxyJ8SB/AlwPJAhEtm0y0s0l1l4eWqxsDxkBOgN+ivU0czrVMssHJEJb4o0FLf" +
            "7iHhOW56/iMdD9w== katy";

    @Mock
    private NewSSHKeyModalView view;

    @Mock
    private SSHKeyEditorService sshKeyEditorService;

    private CallerMock<SSHKeyEditorService> serviceCaller;

    @Mock
    private TranslationService translationService;

    @Mock
    private NewSSHKeyModalHandler handler;

    private NewSSHKeyModal modal;

    @Before
    public void init() {
        when(handler.existsKeyName(eq(EXISTING_KEY_NAME))).thenReturn(true);
        when(handler.existsKey(eq(EXISTING_KEY))).thenReturn(true);

        when(translationService.format(eq(AppformerSSHConstants.ValidationCannotBeEmpty), any())).thenReturn("Field cannot be empty");

        doAnswer((Answer<Void>) invocationOnMock -> {
            String keyContent = (String) invocationOnMock.getArguments()[1];

            PublicKeyConverter.fromString(keyContent);
            return null;
        }).when(sshKeyEditorService).addKey(anyString(), anyString());

        serviceCaller = new CallerMock<>(sshKeyEditorService);

        modal = new NewSSHKeyModal(view, serviceCaller, translationService);

        verify(view).init(modal);
    }

    @Test
    public void testBasicFunctions() {

        modal.init(handler);

        modal.show();

        verify(view).show();

        modal.hide();

        verify(view).hide();
    }

    @Test
    public void testNotifyCancel() {

        modal.notifyCancel();

        verify(view).hide();
    }

    @Test
    public void testAddKeyNullValidationFailure() {
        testBasicFunctions();

        modal.notifyAdd(null, null);

        verify(view).resetValidation();

        verify(translationService).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplName));
        verify(translationService).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplKey));
        verify(translationService, times(2)).format(eq(AppformerSSHConstants.ValidationCannotBeEmpty), any());
        verify(translationService, never()).getTranslation(AppformerSSHConstants.ValidationKeyFormatError);
        verify(translationService, never()).getTranslation(AppformerSSHConstants.ValidationKeyAlreadyExists);
        verify(translationService, never()).format(eq(AppformerSSHConstants.ValidationKeyNameAlreadyExists), any());

        verify(view).setNameValidationError(any());
        verify(view).setKeyValidationError(any());

        verify(sshKeyEditorService, never()).addKey(anyString(), anyString());
        verify(handler, never()).onAddKey();
    }

    @Test
    public void testAddKeyWrongKeyValidationFailure() {
        testBasicFunctions();

        modal.notifyAdd(NAME, WRONG_KEY);

        verify(view).resetValidation();

        verify(translationService, never()).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplName));
        verify(translationService, never()).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplKey));
        verify(translationService, never()).format(eq(AppformerSSHConstants.ValidationCannotBeEmpty), any());
        verify(translationService).getTranslation(AppformerSSHConstants.ValidationKeyFormatError);
        verify(translationService, never()).getTranslation(AppformerSSHConstants.ValidationKeyAlreadyExists);
        verify(translationService, never()).format(eq(AppformerSSHConstants.ValidationKeyNameAlreadyExists), any());

        verify(view, never()).setNameValidationError(any());
        verify(view).setKeyValidationError(any());

        verify(sshKeyEditorService, never()).addKey(anyString(), anyString());
        verify(handler, never()).onAddKey();
    }

    @Test
    public void testAddKeyWrongKeyFormatValidationFailure() {
        testBasicFunctions();

        modal.notifyAdd(NAME, WRONG_KEY_FORMAT);

        verify(view).resetValidation();

        verify(translationService, never()).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplName));
        verify(translationService, never()).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplKey));
        verify(translationService, never()).format(eq(AppformerSSHConstants.ValidationCannotBeEmpty), any());
        verify(translationService, never()).getTranslation(AppformerSSHConstants.ValidationKeyAlreadyExists);
        verify(translationService, never()).format(eq(AppformerSSHConstants.ValidationKeyNameAlreadyExists), any());

        verify(view, never()).setNameValidationError(any());

        verify(sshKeyEditorService).addKey(anyString(), anyString());
        verify(translationService).getTranslation(AppformerSSHConstants.ValidationKeyFormatError);
        verify(view).setKeyValidationError(any());
        verify(handler, never()).onAddKey();
    }

    @Test
    public void testAddKeyExistingKeyNameValidationFailure() {
        testBasicFunctions();

        modal.notifyAdd(EXISTING_KEY_NAME, VALID_KEY);

        verify(view).resetValidation();

        verify(translationService, never()).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplName));
        verify(translationService, never()).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplKey));
        verify(translationService, never()).format(eq(AppformerSSHConstants.ValidationCannotBeEmpty), any());
        verify(translationService, never()).getTranslation(AppformerSSHConstants.ValidationKeyAlreadyExists);
        verify(translationService).format(eq(AppformerSSHConstants.ValidationKeyNameAlreadyExists), any());

        verify(view).setNameValidationError(any());

        verify(sshKeyEditorService, never()).addKey(anyString(), anyString());
        verify(translationService, never()).getTranslation(AppformerSSHConstants.ValidationKeyFormatError);
        verify(view, never()).setKeyValidationError(any());
        verify(handler, never()).onAddKey();
    }

    @Test
    public void testAddKeyExistingKeyValidationFailure() {
        testBasicFunctions();

        modal.notifyAdd(NAME, EXISTING_KEY);

        verify(view).resetValidation();

        verify(translationService, never()).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplName));
        verify(translationService, never()).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplKey));
        verify(translationService, never()).format(eq(AppformerSSHConstants.ValidationCannotBeEmpty), any());
        verify(translationService).getTranslation(AppformerSSHConstants.ValidationKeyAlreadyExists);
        verify(translationService, never()).format(eq(AppformerSSHConstants.ValidationKeyNameAlreadyExists), any());

        verify(view, never()).setNameValidationError(any());

        verify(sshKeyEditorService, never()).addKey(anyString(), anyString());
        verify(translationService, never()).getTranslation(AppformerSSHConstants.ValidationKeyFormatError);
        verify(view).setKeyValidationError(any());
        verify(handler, never()).onAddKey();
    }

    @Test
    public void testAddKey() {
        testBasicFunctions();

        modal.notifyAdd(NAME, VALID_KEY);

        verify(view).resetValidation();

        verify(translationService, never()).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplName));
        verify(translationService, never()).getTranslation(eq(AppformerSSHConstants.NewSSHKeyModalViewImplKey));
        verify(translationService, never()).format(eq(AppformerSSHConstants.ValidationCannotBeEmpty), any());
        verify(translationService, never()).getTranslation(AppformerSSHConstants.ValidationKeyFormatError);
        verify(translationService, never()).getTranslation(AppformerSSHConstants.ValidationKeyAlreadyExists);
        verify(translationService, never()).format(eq(AppformerSSHConstants.ValidationKeyNameAlreadyExists), any());

        verify(view, never()).setNameValidationError(any());
        verify(view, never()).setKeyValidationError(any());

        verify(sshKeyEditorService).addKey(anyString(), anyString());
        verify(handler).onAddKey();
    }

    @Test
    public void testInitNull() {
        Assertions.assertThatThrownBy(() -> modal.init(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'handler' should be not null!");
    }
}
