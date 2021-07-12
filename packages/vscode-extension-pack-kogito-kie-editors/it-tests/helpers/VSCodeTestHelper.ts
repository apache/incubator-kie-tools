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

import { assert } from "chai";
import {
  ActivityBar,
  By,
  InputBox,
  SideBarView,
  until,
  ViewControl,
  ViewSection,
  VSBrowser,
  WebDriver,
  WebView,
  Workbench,
} from "vscode-extension-tester";
import { kogitoLoadingSpinner } from "./CommonLocators";

/**
 * Common test helper class for VSCode extension testing.
 * Provides common API to work with VSCode test instance.
 * Allows you  to open folders, files, close open editor.
 * Aquire notifications, input CLI commands etc.
 */
export default class VSCodeTestHelper {
  /**
   * Name of the root folder in workspace that contains all source files.
   */
  private readonly SRC_ROOT: string = "src";

  /**
   * Name of the roof folder of all testing resources.
   */
  private readonly RESOURCES_ROOT: string = "resources";

  /**
   * Loading timeout for editors.
   */
  private readonly EDITOR_LOADING_TIMEOUT: number = 60000;

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
  public openFolder = async (absolutePath: string): Promise<SideBarView> => {
    await sleep(5000);
    await this.workbench.executeCommand("Extest: Open Folder");
    const inputBox = await InputBox.create();
    await inputBox.setText(absolutePath);
    await inputBox.confirm();

    const control = (await new ActivityBar().getViewControl("Explorer")) as ViewControl;
    this.sidebarView = await control.openView();
    assert.isTrue(await this.sidebarView.isDisplayed(), "Explorer side bar view was not opened");

    this.workspaceSectionView = await this.sidebarView.getContent().getSection("Untitled (Workspace)");
    return this.sidebarView;
  };

  /**
   * Opens file from a sidebarview. Expects that the sidebarview will be defined and open.
   * Once the file is openned, it waits for the WebView to load and the returns it.
   *
   * If the file is not located in root of resources folder, specify a relative path to its
   * parent directory.
   * To open file in ".../resources/org/kie" call the method as openFileFromSidebar(fileName, "org/kie").
   * Always separate the directories in path by "/"
   *
   * @param fileName name of the file to open
   * @param fileParentPath optional, use when file is not in root of resources. This is the path of file's parent directory, relative to resources
   *                       if not used the file will be looked in root of resources.
   * @returns promise that resolves to WebView of the openned file.
   */
  public openFileFromSidebar = async (fileName: string, fileParentPath?: string): Promise<WebView> => {
    if (fileParentPath == undefined || fileParentPath == "") {
      await this.workspaceSectionView.openItem(this.RESOURCES_ROOT, fileName);
    } else {
      const pathPieces = fileParentPath.split("/");
      pathPieces.unshift(this.RESOURCES_ROOT);
      await this.workspaceSectionView.openItem(...pathPieces);
      // For some reason openItem() collapses the view it expands so we
      // click on src to reexpand the tree and click on desired item
      const srcItem = await this.workspaceSectionView.findItem(this.SRC_ROOT);
      if (srcItem != undefined) {
        await srcItem.click();
      }
      const fileItem = await this.workspaceSectionView.findItem(fileName);
      if (fileItem != undefined) {
        await fileItem.click();
      }
    }

    // In cases where you have multiple KIE editors installed in VSCode
    // uncomment this to run locally without issues.
    // const input = await InputBox.create();
    // await input.selectQuickPick('KIE Kogito Editors');
    const webview = new WebView(this.workbench.getEditorView(), By.linkText(fileName));
    await this.waitUntilKogitoEditorIsLoaded(webview);
    return webview;
  };

  /**
   * Closes all editor views that are open.
   * Resolves even if there are no open editor views.
   */
  public closeAllEditors = async (): Promise<void> => {
    try {
      await this.workbench.getEditorView().closeAllEditors();
    } catch (error) {
      // catch the error when there is nothing to close
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
      2000,
      "No iframe.webview.ready that was ready was located in webview under 2 seconds." +
        "This should not happen and is most probably issue of VSCode." +
        "In case this happens investigate vscode or vscode-extension-tester dependency."
    );
    await driver.switchTo().frame(await driver.findElement(By.className("webview ready")));
    await driver.wait(
      until.elementLocated(By.id("active-frame")),
      2000,
      "No iframe#active-frame located in webview under 2 seconds." +
        "This should not happen and is most probably issue of VSCode." +
        "In case this happens investigate vscode or vscode-extension-tester dependency."
    );
    await driver.switchTo().frame(await driver.findElement(By.id("active-frame")));
    await driver.wait(
      until.elementLocated(By.id("envelope-app")),
      this.EDITOR_LOADING_TIMEOUT,
      "No 'div#envelope-app' located in webview's active-frame in " +
        this.EDITOR_LOADING_TIMEOUT +
        "ms. Please investigate."
    );
    await driver.wait(
      async () => {
        const loadingSpinners = await webview.getDriver().findElements(kogitoLoadingSpinner());
        return !loadingSpinners || loadingSpinners.length <= 0;
      },
      this.EDITOR_LOADING_TIMEOUT,
      "Editor was still loading after " + this.EDITOR_LOADING_TIMEOUT + "ms. Please investigate."
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

  /**
   * Switches provided webview's context to iframe#active-frame within it.
   *
   * @param webview
   */
  public switchWebviewToFrame = async (webview: WebView): Promise<void> => {
    const driver = webview.getDriver();
    await driver.wait(
      until.elementLocated(By.className("webview ready")),
      2000,
      "No iframe.webview.ready that was ready was located in webview under 2 seconds." +
        "This should not happen and is most probably issue of VSCode." +
        "In case this happens investigate vscode or vscode-extension-tester dependency."
    );
    await driver.switchTo().frame(await driver.findElement(By.className("webview ready")));
    await driver.wait(
      until.elementLocated(By.id("active-frame")),
      2000,
      "No iframe#active-frame located in webview under 2 seconds." +
        "This should not happen and is most probably issue of VSCode." +
        "In case this happens investigate vscode or vscode-extension-tester dependency."
    );
    await driver.switchTo().frame(await driver.findElement(By.id("active-frame")));
  };
}

function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}
