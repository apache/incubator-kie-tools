/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.drools.lsp.server;

import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.RemoteEndpoint;

public class SocketLauncher<T> implements Launcher<T> {

	private final Launcher<T> launcher;

	public SocketLauncher(Object localService, Class<T> remoteInterface, Socket socket) {
		try {
			this.launcher = Launcher.createLauncher(localService, remoteInterface, socket.getInputStream(), socket.getOutputStream());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Future<Void> startListening() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.launcher.startListening().get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}, Executors.newSingleThreadExecutor());
	}

	public T getRemoteProxy() {
		return this.launcher.getRemoteProxy();
	}

    @Override
    public RemoteEndpoint getRemoteEndpoint() {
        return null;
    }
}