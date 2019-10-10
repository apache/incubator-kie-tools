import React from 'react';
import { Avatar } from '@patternfly/react-core';

const userImage = require('../../static/user.png');

export interface IOwnProps {}
export interface IStateProps {}
class AvatarComponent extends React.Component<IOwnProps, IStateProps> {
  constructor(props) {
    super(props);
    this.state = {};
  }
  render() {
    return <Avatar src={userImage} alt="Kogito Logo"></Avatar>;
  }
}

export default AvatarComponent;
