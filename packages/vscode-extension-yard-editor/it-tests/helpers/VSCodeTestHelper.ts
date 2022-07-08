/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { assert } from "chai";
import {
  ActivityBar,
  By,
  InputBox,
  ModalDialog,
  SideBarView,
  TextEditor,
  until,
  ViewControl,
  ViewSection,
  VSBrowser,
  WebDriver,
  WebView,
  Workbench,
} from "vscode-extension-tester";

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
   * Instance of VSBrowser.
   */
  private browser: VSBrowser;

  /**
   * WebDriver of currently used VSBrowser.
   */
  private driver: WebDriver;

  /**
   * Handle for workspace section with directory tree and files that
   * is located in sidebarview. Used to lookup resources and open them.
   */
  private workspaceSectionView: ViewSection;

  /**
   * Handle for sidebarview. Usually used to work with
   * open folder.
   * When you open folder sidebarview is initialized.
   */
  private sidebarView: SideBarView;

  constructor() {
    this.workbench = new Workbench() as Workbench;
    this.browser = VSBrowser.instance;
    this.driver = this.browser.driver;
  }

  /**
   * Opens folder using commmand suplied by vscode-extension-tester
   * and open the dedicated SideBarView with the folder.
   *
   * @param absolutePath absolute path to the folder that needs to be openned
   * @returns a promise that resolves to a SideBarView of the openned folder
   */
  public openFolder = async (absolutePath: string, folderName: string): Promise<SideBarView> => {
    await this.browser.openResources(absolutePath);

    const control = (await new ActivityBar().getViewControl("Explorer")) as ViewControl;
    this.sidebarView = await control.openView();
    assert.isTrue(await this.sidebarView.isDisplayed(), "Explorer side bar view was not opened");

    this.workspaceSectionView = await this.sidebarView.getContent().getSection(folderName);
    return this.sidebarView;
  };

  /**
   * Opens serverless logic file from a sidebarview. Expects that the sideBarView will be defined and open.
   * To define sideBarView a folder needs to be opened using openFolder function.
   * Once the file is opened using a click, function assert existence of two
   * editor groups and assigns each group to Webview. Both webviews are confirmed loaded and returned
   * in an array on predefined indexes - see returns definition.
   *
   * If the file is not located in root of resources folder, specify a relative path to its
   * parent directory.
   * To open file in ".../resources/org/kie" call the method as openFileFromSidebar(fileName, "org/kie").
   * Always separate the directories in path by "/"
   *
   * @param fileName name of the file to open
   * @param fileParentPath optional, use when file is not in root of resources. This is the path of file's parent directory, relative to resources
   *                       if not used the file will be looked in root of resources.
   * @returns promise that resolves to an array of WebViews of the openned serverless worklow file.
   *          The length of the array is always 2 and there is guaranteed TextEditor as webview on O index.
   *          Custom kogito swf editor as webview is always on index 1
   */
  public openFileFromSidebar = async (fileName: string, fileParentPath?: string): Promise<[TextEditor, WebView]> => {
    if (fileParentPath == undefined || fileParentPath == "") {
      await this.workspaceSectionView.openItem(fileName);
    } else {
      const pathPieces = fileParentPath.split("/");
      await this.workspaceSectionView.openItem(...pathPieces);
      const fileItem = await this.workspaceSectionView.findItem(fileName);
      if (fileItem != undefined) {
        await fileItem.click();
      }
    }

    await sleep(3000);
    const editorGroups = await this.workbench.getEditorView().getEditorGroups();
    // should be always two groups, one text editor and one swf editor
    assert.equal(editorGroups.length, 2);

    const textEditor = new TextEditor(editorGroups[0]);
    const webView = new WebView(editorGroups[1], By.linkText(fileName));

    // right webview has the custom kogito editor, wait for it to load
    await this.waitUntilKogitoEditorIsLoaded(webView);

    return Promise.resolve([textEditor, webView]);
  };

  /**
   * Closes all editor views that are open.
   * Resoxlves even if there are no open editor views.
   */
  public closeAllEditors = async (): Promise<void> => {
    try {
      await this.workbench.getEditorView().closeAllEditors();
    } catch (error) {
      // catch the error when there is nothing to close
      // or the Save Dialog appears
      const dialog = new ModalDialog();
      if (dialog != null && (await dialog.isDisplayed())) {
        await dialog.pushButton("Don't Save");
      }
    }
  };

  /**
   * Closes all notifications that can be found using {@see Workbench}.
   */
  public closeAllNotifications = async (): Promise<void> => {
    const activeNotifications = await this.workbench.getNotifications();
    for (const notification of activeNotifications) {
      await notification.dismiss();
    }
  };

  /**
   * Waits until the provided webview has fully loaded kogito editor.
   * Method will look in the webview for active iframe and switches to the
   * iframe if located.
   * After that it looks for div#envelope-app in the iframe#active-frame and if found,
   * waits for kogito editor loading spinner to not be present.
   * Returns void promise if the loading spinner disappears in timeout that is
   * set in {@see this.EDITOR_LOADING_TIMEOUT} property.
   *
   * @param webview {@see WebView} that contains the kogito editor with envelope-app
   */
  public waitUntilKogitoEditorIsLoaded = async (webview: WebView): Promise<void> => {
    const driver = webview.getDriver();
    await driver.wait(
      until.elementLocated(By.className("webview ready")),
      10000,
      "No iframe.webview.ready that was ready was located in webview under 2 seconds." +
        "This should not happen and is most probably issue of VSCode." +
        "In case this happens investigate vscode or vscode-extension-tester dependency."
    );
    await driver.switchTo().frame(await driver.findElement(By.className("webview ready")));
    await driver.wait(
      until.elementLocated(By.id("active-frame")),
      10000,
      "No iframe#active-frame located in webview under 2 seconds." +
        "This should not happen and is most probably issue of VSCode." +
        "In case this happens investigate vscode or vscode-extension-tester dependency."
    );
    await driver.switchTo().frame(await driver.findElement(By.id("active-frame")));
    await driver.wait(
      until.elementLocated(By.id("envelope-app")),
      60000,
      "No 'div#envelope-app' located in webview's active-frame in ms. Please investigate."
    );
    await driver.wait(
      async () => {
        const loadingSpinners = await webview
          .getDriver()
          .findElements(By.className("kie-tools--loading-screen-spinner"));
        return !loadingSpinners || loadingSpinners.length <= 0;
      },
      60000,
      "Editor was still loading after ms. Please investigate."
    );

    await sleep(2000);

    await driver.switchTo().frame(null);
  };

  /**
   * Opens commands prompt and select given command there
   */
  public executeCommandFromPrompt = async (command: string): Promise<void> => {
    const inputBox = (await this.workbench.openCommandPrompt()) as InputBox;
    await inputBox.setText(`>${command}`);

    const quickPicks = await inputBox.getQuickPicks();

    for (const quickPick of quickPicks) {
      const label = await quickPick.getLabel();
      if (label === command) {
        await quickPick.select();
        await sleep(1000);
        return;
      }
    }

    throw new Error(`'${command}' not found in prompt`);
  };
}

export function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}
