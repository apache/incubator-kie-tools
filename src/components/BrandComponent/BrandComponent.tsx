import React from 'react';
import { Brand } from '@patternfly/react-core';
import { withRouter } from 'react-router-dom';
import { RouteComponentProps } from 'react-router';
type combinedProps = RouteComponentProps & IOwnProps;
export interface IOwnProps {}
export interface IStateProps {}
const logo = require('../../static/kogito_logo_rgb.png');
class BrandComponent extends React.Component<combinedProps, IStateProps> {
  constructor(props: combinedProps) {
    super(props);
    this.state = {};
  }

  onLogoClick = () => {
    this.props.history.push('/');
  };
  render() {
    return <Brand src={logo} alt="Kogito Logo" onClick={this.onLogoClick}></Brand>;
  }
}

export default withRouter(BrandComponent);
