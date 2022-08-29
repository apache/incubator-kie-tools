/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.kogito.core.internal.handlers;

import java.util.Map;

import org.eclipse.lsp4j.CompletionItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kogito.core.internal.api.GetPublicResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.jdt.ls.core.internal.handlers.CompletionResolveHandler.DATA_FIELD_SIGNATURE;

class GetAccessorsHandlerTest {

    private GetAccessorsHandler getAccessorHandler;

    @BeforeEach
    public void setUp() {
        this.getAccessorHandler = new GetAccessorsHandler(null, null, null);
    }

    @Test
    void testEmptyGetAccessor() {
        CompletionItem completionItem = new CompletionItem();
        completionItem.setLabel("");
        GetPublicResult accessor = this.getAccessorHandler.getAccessor(completionItem, "org.kogito.Class");
        assertThat(accessor.getFqcn()).isEqualTo("org.kogito.Class");
        assertThat(accessor.getType()).isEmpty();
        assertThat(accessor.getAccessor()).isEmpty();
    }

    @Test
    void testWrongGetAccessor() {
        CompletionItem completionItem = new CompletionItem();
        completionItem.setLabel("aRandomLabel - MoreRandom");
        GetPublicResult accessor = this.getAccessorHandler.getAccessor(completionItem, "org.kogito.Class");
        assertThat(accessor.getFqcn()).isEqualTo("org.kogito.Class");
        assertThat(accessor.getType()).isEmpty();;
        assertThat(accessor.getAccessor()).isEmpty();
    }

    @Test
    void testCorrectGetAccessor() {
        CompletionItem completionItem = new CompletionItem();
        completionItem.setLabel("method() : String");
        GetPublicResult accessor = this.getAccessorHandler.getAccessor(completionItem, "org.kogito.Class");
        assertThat(accessor.getFqcn()).isEqualTo("org.kogito.Class");
        assertThat(accessor.getType()).isEqualTo("String");
        assertThat(accessor.getAccessor()).isEqualTo("method()");
    }

    @Test
    void testCorrectGetFQCNAccessor() {
        CompletionItem completionItem = new CompletionItem();
        completionItem.setLabel("getName() : String");
        Map<String, String> data = Map.of(DATA_FIELD_SIGNATURE, "()Ljava.lang.String;");
        completionItem.setData(data);
        GetPublicResult accessor = this.getAccessorHandler.getAccessor(completionItem, "org.kogito.Class");
        assertThat(accessor.getFqcn()).isEqualTo("org.kogito.Class");
        assertThat(accessor.getType()).isEqualTo("java.lang.String");
        assertThat(accessor.getAccessor()).isEqualTo("getName()");
    }

    @Test
    void testCorrectGetFQCListAccessor() {
        CompletionItem completionItem = new CompletionItem();
        completionItem.setLabel("getBooksList() : List<Book>");
        Map<String, String> data = Map.of(DATA_FIELD_SIGNATURE, "()Ljava.util.List<Lcom.Book;>");
        completionItem.setData(data);
        GetPublicResult accessor = this.getAccessorHandler.getAccessor(completionItem, "org.kogito.Class");
        assertThat(accessor.getFqcn()).isEqualTo("org.kogito.Class");
        assertThat(accessor.getType()).isEqualTo("java.util.List<Lcom.Book;>");
        assertThat(accessor.getAccessor()).isEqualTo("getBooksList()");
    }

    @Test
    void testCorrectGetFQCNMapAccessor() {
        CompletionItem completionItem = new CompletionItem();
        completionItem.setLabel("getBooksMap() : Map<String,Book>");
        Map<String, String> data = Map.of(DATA_FIELD_SIGNATURE, "()Ljava.util.Map<Ljava.lang.String;Lcom.Book;>;");
        completionItem.setData(data);
        GetPublicResult accessor = this.getAccessorHandler.getAccessor(completionItem, "org.kogito.Class");
        assertThat(accessor.getFqcn()).isEqualTo("org.kogito.Class");
        assertThat(accessor.getType()).isEqualTo("java.util.Map<Ljava.lang.String;Lcom.Book;>");
        assertThat(accessor.getAccessor()).isEqualTo("getBooksMap()");
    }
}