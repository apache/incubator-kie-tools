import * as React from 'react';
import LoginPage from '../LoginComponent/LoginPage';
import Dashboard from '../DashboardComponent/Dashboard';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
export interface IOwnProps {}

interface IStateProps {}

export default class BaseLayout extends React.Component<IOwnProps, IStateProps> {
  render() {
    return (
      <div>
        <LoginPage>
          <BrowserRouter>
            <Switch>
              <Route path="/" component={Dashboard} />
            </Switch>
          </BrowserRouter>
        </LoginPage>
      </div>
    );
  }
}
