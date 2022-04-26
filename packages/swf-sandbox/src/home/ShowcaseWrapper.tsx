import React, { useCallback, useMemo, useState } from "react";
import {
  Breadcrumb,
  BreadcrumbItem,
  Nav,
  NavItem,
  NavList,
  Page,
  PageSection,
  PageSidebar,
  SkipToContent,
  Text,
  TextContent,
} from "@patternfly/react-core";
import { ShowcaseHeader } from "./ShowcaseHeader";

export const DashboardBreadcrumb = (
  <Breadcrumb>
    <BreadcrumbItem>Section home</BreadcrumbItem>
    <BreadcrumbItem to="#">Section title</BreadcrumbItem>
    <BreadcrumbItem to="#">Section title</BreadcrumbItem>
    <BreadcrumbItem to="#" isActive>
      Section landing
    </BreadcrumbItem>
  </Breadcrumb>
);

export const PageTemplateTitle = (
  <PageSection variant="light">
    <TextContent>
      <Text component="h1">Main title</Text>
      <Text component="p">This is a full page demo.</Text>
    </TextContent>
  </PageSection>
);

export type ShowcaseWrapperProps = {
  children: React.ReactNode;
  mainContainerId: string;
  breadcrumb?: React.ReactNode;
  header?: React.ReactNode;
  sidebar?: React.ReactNode;
  sidebarNavOpen?: boolean;
  onPageResize?: (object: any) => void;
  hasNoBreadcrumb?: boolean;
  hasPageTemplateTitle?: boolean;
};

export function ShowcaseWrapper(props: ShowcaseWrapperProps) {
  const [activeItem, setActiveItem] = useState(0);

  const onNavSelect = useCallback((result) => {
    setActiveItem(result.itemId);
  }, []);

  const {
    children,
    mainContainerId,
    breadcrumb,
    header,
    sidebar,
    sidebarNavOpen,
    onPageResize,
    hasNoBreadcrumb,
    hasPageTemplateTitle,
  } = props;

  const renderedBreadcrumb = useMemo(() => {
    if (!hasNoBreadcrumb) {
      return breadcrumb !== undefined ? breadcrumb : DashboardBreadcrumb;
    }
    return undefined;
  }, [breadcrumb, hasNoBreadcrumb]);

  const PageNav = (
    <Nav onSelect={onNavSelect} aria-label="Nav">
      <NavList>
        <NavItem itemId={0} isActive={activeItem === 0} to="#system-panel">
          System Panel
        </NavItem>
        <NavItem itemId={1} isActive={activeItem === 1} to="#policy">
          Policy
        </NavItem>
        <NavItem itemId={2} isActive={activeItem === 2} to="#auth">
          Authentication
        </NavItem>
        <NavItem itemId={3} isActive={activeItem === 3} to="#network">
          Network Services
        </NavItem>
        <NavItem itemId={4} isActive={activeItem === 4} to="#server">
          Server
        </NavItem>
      </NavList>
    </Nav>
  );

  const _sidebar = <PageSidebar nav={PageNav} isNavOpen={sidebarNavOpen || false} />;
  const PageSkipToContent = (
    <SkipToContent href={`#${mainContainerId ? mainContainerId : "main-content-page-layout-default-nav"}`}>
      Skip to content
    </SkipToContent>
  );

  return (
    <Page
      header={header !== undefined ? header : <ShowcaseHeader />}
      sidebar={sidebar !== undefined ? sidebar : _sidebar}
      isManagedSidebar
      skipToContent={PageSkipToContent}
      breadcrumb={renderedBreadcrumb}
      mainContainerId={mainContainerId ? mainContainerId : "main-content-page-layout-default-nav"}
      onPageResize={onPageResize}
    >
      {hasPageTemplateTitle && PageTemplateTitle}
      {children}
    </Page>
  );
}
