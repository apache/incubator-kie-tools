/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ActivityBar, By, EditorView, InputBox, SideBarView, WebView, Workbench, Notification } from "vscode-extension-tester";
import { DefaultWait } from "vscode-uitests-tooling";

/**
 * Common test helper class for VSCode extension testing.
 * Provides common API to work with VSCode test instance.
 * Allows you  to open folders, files, close open editor.
 * Aquire notifications, input CLI commands etc.
 */
export default class VSCodeTestHelper {
    
    /**
     * Handle for VSCode workbench.
     * Initialized in constructor.
     */
    private workbench: Workbench;

    /**
     * Handle for sidebarview. Usually used to work with
     * open folder.
     * When you open folder sidebarview is initialized.
     */
    private sidebarView: SideBarView;

    constructor() {
        this.workbench = new Workbench() as Workbench;
    }

    /**
     * Opens folder using commmand suplied by vscode-extension-tester
     * and open the dedicated SideBarView with the folder.
     * 
     * @param absolutePath absolute path to the folder that needs to be openned
     * @returns a promise that resolves to a SideBarView of the openned folder
     */
    public openFolder = async (absolutePath: string): Promise<SideBarView> => {
        await this.workbench.executeCommand("Extest: Open Folder");
        const inputBox = await InputBox.create();
        await inputBox.setText(absolutePath);
        await inputBox.confirm();

        const control = new ActivityBar().getViewControl('Explorer');
        this.sidebarView = await control.openView();
        return this.sidebarView;
    }

    /**
     * Opens file from a sidebarview. Expects that the sidebarview will be defined and open.
     * Once the file is openned, it wait for the WebView to load and the returns it.
     * 
     * @param fileName name of the file to open
     * @returns promise that resolves to WebView of the openned file.
     */
    public openFileFromSidebar = async (fileName: string): Promise<WebView> => {
        const file = await this.sidebarView.findElement(By.linkText(fileName));
        await file.click();
        // In cases where you have multiple KIE editors installed in VSCode
        // uncomment this to run locally without issues. 
        // const input = await InputBox.create();
        // await input.selectQuickPick('KIE Kogito Editors');
        const webview = new WebView(new EditorView(), By.linkText(fileName));
        await DefaultWait.sleep(10000);
        return webview;
    }

    /**
     * Close all editor views that are open.
     */
    public closeAllEditors = async (): Promise<void> => {
        await new EditorView().closeAllEditors();
    }

    /**
     * Gets all currently displayed notifications.
     * 
     * @returns a promise that resolves to array of notifications.
     */
    public getNotifications = async (): Promise<Notification[]> => {
       return await this.workbench.getNotifications();
    }
}