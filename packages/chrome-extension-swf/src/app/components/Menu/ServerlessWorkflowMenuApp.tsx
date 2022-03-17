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

import * as React from "react";
import { useEffect, useState, useCallback, useMemo } from "react";
import * as ReactDOM from "react-dom";
import { SWF_MENU_ITEM_CONTAINER_CLASS, SWF_PAGE_CONTAINER_CLASS } from "../../constants";
import { RoutesSwitch } from "../../navigation/RoutesSwitch";
import { useGlobals } from "../../common/GlobalContext";
import { Main } from "../../common/Main";

interface CreateServerlessWorkflowAppProps {
  id: string;
}

const SWF_URL_PATH = "/application-services/swf/#/";

let lastUrl = window.location.href;

const urlPathObserver = (callback: () => void) => {
  new MutationObserver(() => {
    const url = window.location.href;
    if (url !== lastUrl) {
      if (!url.includes(SWF_URL_PATH)) {
        callback();
      }
      lastUrl = url;
    }
  }).observe(document, { subtree: true, childList: true });
};

export function ServerlessWorkflowMenuApp(props: CreateServerlessWorkflowAppProps) {
  const globals = useGlobals();

  const dependencies = useMemo(() => globals.dependencies!, [globals.dependencies]);

  const [menuLoaded, setMenuLoaded] = useState(false);
  const [container, setContainer] = useState<HTMLElement | null>(null);
  const [showPage, setShowPage] = useState(false);

  const openPage = useCallback(() => {
    setShowPage(true);
    document.querySelectorAll("pf-c-nav__item").forEach((element) => element.classList.remove("pf-m-current"));

    history.replaceState(
      {
        id: "swf",
        source: "web",
      },
      "Serverless Workflow",
      "https://console.redhat.com/application-services/swf/#/"
    );

    document.title = "Serverless Workflow";

    const page = dependencies.applicationServices.page();
    const mainContainer = dependencies.applicationServices.mainContainer();

    if (page && mainContainer) {
      page.style.display = "none";
      mainContainer.classList.remove("pf-u-h-100vh");
    }
  }, [dependencies.applicationServices]);

  useEffect(() => {
    if (window.location.pathname.includes(SWF_URL_PATH) && !showPage) {
      openPage();
    }
  }, [openPage, showPage]);

  useEffect(() => {
    urlPathObserver(() => {
      setShowPage(false);
      dependencies.applicationServices.page()!.style.display = "";
    });
  }, [dependencies.applicationServices]);

  useEffect(() => {
    if (menuLoaded) {
      return;
    }

    const menuLoadedTask = window.setInterval(() => {
      setMenuLoaded(
        !!Array.from(document.querySelectorAll("div")).find((el) => el.textContent === "Application Services")
      );
    }, 500);
    return () => window.clearInterval(menuLoadedTask);
  }, [menuLoaded]);

  useEffect(() => {
    if (container || !menuLoaded) {
      return;
    }

    const resolveContainerTask = window.setInterval(() => {
      setContainer(dependencies.applicationServices.menu());
    }, 500);
    return () => window.clearInterval(resolveContainerTask);
  }, [container, dependencies.applicationServices, menuLoaded]);

  return (
    <>
      {container &&
        ReactDOM.createPortal(
          <ServerlessWorkflowMenuItem openPage={openPage} selected={showPage} />,
          createMenuItemContainer(props.id, dependencies.applicationServices.menu()!)
        )}
      {showPage &&
        ReactDOM.createPortal(
          <Main>
            <RoutesSwitch />
          </Main>,
          createPageContainer(
            props.id,
            dependencies.applicationServices.main()!,
            dependencies.applicationServices.page()!
          )
        )}
    </>
  );
}

interface ServerlessWorkflowMenuItemProps {
  openPage: () => void;
  selected: boolean;
}

function ServerlessWorkflowMenuItem(props: ServerlessWorkflowMenuItemProps) {
  return (
    <li className={`pf-c-nav__item ${props.selected ? "pf-m-current" : ""}`}>
      <a className="pf-c-nav__link" onClick={props.openPage}>
        Serverless Workflow
      </a>
    </li>
  );
}

function createMenuItemContainer(id: string, container: HTMLElement) {
  const element = () => document.querySelector(`.${SWF_MENU_ITEM_CONTAINER_CLASS}.${id}`)!;

  if (!element()) {
    container.insertAdjacentHTML("beforeend", `<div class="${SWF_MENU_ITEM_CONTAINER_CLASS} ${id}"></div>`);
  }

  return element();
}

function createPageContainer(id: string, container: HTMLElement, currentPage: HTMLElement) {
  const element = () => document.querySelector(`.${SWF_PAGE_CONTAINER_CLASS}.${id}`)!;

  if (!element()) {
    if (currentPage) {
      currentPage.style.display = "none";
    }
    container.insertAdjacentHTML(
      "afterbegin",
      `<div class="${SWF_PAGE_CONTAINER_CLASS} ${id}" style="height: 100%"></div>`
    );
  }

  return element();
}
