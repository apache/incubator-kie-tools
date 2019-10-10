import React from 'react';
import { Page, SkipToContent } from '@patternfly/react-core';
import './Dashboard.css';
import HeaderComponent from '../PageHeaderComponent/HeaderComponent';
// import { Route } from 'react-router-dom';
{
  /* The below code is to be removed in future */
}
// import BreadcrumbComponent from '../PageBreadcrumbComponent/BreadcrumbComponent';
// import TabComponent from '../TestComponents/TabComponent/TabComponent';
// import InstanceDetailPage from '../InstanceDetails/InstanceDetailComponent';
// import Navbar from '../NavComponent/NavComponent';
// import Overview from '../OverviewComponent/OverviewComponent';

export interface IOwnProps {}
export interface IStateProps {}

class Dashboard extends React.Component<IOwnProps, IStateProps> {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const pageId = 'main-content-page-layout-default-nav';
    const PageSkipToContent = <SkipToContent href={`#${pageId}`}>Skip to Content</SkipToContent>;

    return (
      <React.Fragment>
        <Page
          header={<HeaderComponent />}
          skipToContent={PageSkipToContent}
          mainContainerId={pageId}
          className="page"
        ></Page>
        {/* <Route exact path="/instanceDetail/:processInstanceID" component={InstanceDetailComponent} />
          <Route exact path="/" component={DataListComponent} /> */}

        {/* The below code is to be removed in future */}
        {/* <Page
          header={<HeaderComponent />}
          sidebar={<PageSidebar nav={<Navbar />} />}
          isManagedSidebar
          skipToContent={PageSkipToContent}
          breadcrumb={<BreadcrumbComponent />}
          mainContainerId={pageId}
          className="page"
        >
          <Route exact path="/instanceDetail/:processInstanceID" component={InstanceDetailPage} />
          <Route exact path="/" component={Overview} />
        </Page> */}
        {/* <InstanceDetailComponent /> */}
      </React.Fragment>
    );
  }
}

export default Dashboard;
