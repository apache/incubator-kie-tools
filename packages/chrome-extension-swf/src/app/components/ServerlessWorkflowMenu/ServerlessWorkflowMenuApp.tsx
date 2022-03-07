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
import { useEffect, useState } from "react";
import * as ReactDOM from "react-dom";
import { SWF_MENU_ITEM_CONTAINER_CLASS, SWF_PAGE_CONTAINER_CLASS } from "../../constants";
import { useGlobals } from "../common/GlobalContext";
import { ServerlessWorkflowList } from "./ServerlessWorkflowList";

interface CreateServerlessWorkflowAppProps {
  id: string;
}

export function ServerlessWorkflowMenuApp(props: CreateServerlessWorkflowAppProps) {
  const globals = useGlobals();

  const [menuLoaded, setMenuLoaded] = useState(false);
  const [container, setContainer] = useState<HTMLElement | null>(null);
  const [showPage, setShowPage] = useState(false);

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
      setContainer(globals.dependencies.applicationServices.menu());
    }, 500);
    return () => window.clearInterval(resolveContainerTask);
  }, [container, globals.dependencies.applicationServices, menuLoaded]);

  return (
    <>
      {container &&
        ReactDOM.createPortal(
          <ServerlessWorkflowMenuItem openPage={() => setShowPage(true)} />,
          createMenuItemContainer(props.id, globals.dependencies.applicationServices.menu()!)
        )}
      {showPage &&
        ReactDOM.createPortal(
          <ServerlessWorkflowList />,
          createPageContainer(
            props.id,
            globals.dependencies.applicationServices.main()!,
            globals.dependencies.applicationServices.page()!
          )
        )}
    </>
  );
}

interface ServerlessWorkflowMenuItemProps {
  openPage: () => void;
}

function ServerlessWorkflowMenuItem(props: ServerlessWorkflowMenuItemProps) {
  return (
    <li className="pf-c-nav__item">
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
    currentPage.style.display = "none";
    container.insertAdjacentHTML("beforeend", `<div class="${SWF_PAGE_CONTAINER_CLASS} ${id}"></div>`);
  }

  return element();
}
