import React from 'react';
import { PageHeader } from '@patternfly/react-core';
import Avatar from '../AvatarComponent/AvatarComponent';
import PageToolbarComponent from '../PageToolbarComponent/PageToolbarComponent';
import { Link } from 'react-router-dom';
import BrandComponent from '../BrandComponent/BrandComponent';
export interface IOwnProps {}
export interface IStateProps {}
export default class HeaderComponent extends React.Component<IOwnProps, IStateProps> {
  constructor(props) {
    super(props);
    this.state = {};
  }
  render() {
    return (
      <PageHeader logo={<BrandComponent />} toolbar={<PageToolbarComponent />} avatar={<Avatar />} showNavToggle />
    );
  }
}
