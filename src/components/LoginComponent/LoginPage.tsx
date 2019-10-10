import React from 'react';
import { LoginForm, LoginMainFooterBandItem, LoginPage, BackgroundImageSrc } from '@patternfly/react-core';
import { ExclamationCircleIcon } from '@patternfly/react-icons';
const brandImage = require('../../static/kogito_logo.png');
const backgroundImage = require('../../static/black_background.png');
import './LoginPage.css';

export interface IOwnProps {}
export interface IStateprops {
  showHelperText: boolean;
  usernameValue: string;
  isValidUsername: boolean;
  passwordValue: string;
  isValidPassword: boolean;
  isRememberMeChecked: boolean;
  isAuthenticated: boolean;
}

const images = {
  [BackgroundImageSrc.lg]: backgroundImage,
  [BackgroundImageSrc.sm]: backgroundImage,
  [BackgroundImageSrc.sm2x]: backgroundImage,
  [BackgroundImageSrc.xs]: backgroundImage,
  [BackgroundImageSrc.xs2x]: backgroundImage,
  [BackgroundImageSrc.filter]: '/assets/images/background-filter.svg#image_overlay'
};

class Login extends React.Component<IOwnProps, IStateprops> {
  constructor(props: IOwnProps) {
    super(props);
    this.state = {
      showHelperText: false,
      usernameValue: '',
      isValidUsername: true,
      passwordValue: '',
      isValidPassword: true,
      isRememberMeChecked: false,
      isAuthenticated: false
    };
  }

  handleUsernameChange = value => {
    this.setState({ usernameValue: value });
  };

  handlePasswordChange = passwordValue => {
    this.setState({ passwordValue });
  };

  onRememberMeClick = () => {
    this.setState({ isRememberMeChecked: !this.state.isRememberMeChecked });
  };

  onLoginButtonClick = event => {
    event.preventDefault();
    this.setState({ isValidUsername: !!this.state.usernameValue });
    this.setState({ isValidPassword: !!this.state.passwordValue });
    this.setState({ showHelperText: !this.state.usernameValue || !this.state.passwordValue });
    if (this.state.isValidUsername && this.state.isValidPassword) {
      if (this.state.usernameValue == 'admin' && this.state.passwordValue == 'admin') {
        this.setState({ isAuthenticated: true });
      }
    }
  };

  render() {
    const helperText = (
      <React.Fragment>
        <ExclamationCircleIcon />
        &nbsp;Invalid login credentials.
      </React.Fragment>
    );

    const signUpForAccountMessage = (
      <LoginMainFooterBandItem>
        Need an account? <a href="#">Sign up.</a>
      </LoginMainFooterBandItem>
    );
    const forgotCredentials = (
      <LoginMainFooterBandItem>
        <a href="#">Forgot username or password?</a>
      </LoginMainFooterBandItem>
    );

    const loginForm = (
      <LoginForm
        showHelperText={this.state.showHelperText}
        helperText={helperText}
        usernameLabel="Username"
        usernameValue={this.state.usernameValue}
        onChangeUsername={this.handleUsernameChange}
        isValidUsername={this.state.isValidUsername}
        passwordLabel="Password"
        passwordValue={this.state.passwordValue}
        onChangePassword={this.handlePasswordChange}
        isValidPassword={this.state.isValidPassword}
        rememberMeLabel="Keep me logged in for 30 days."
        isRememberMeChecked={this.state.isRememberMeChecked}
        onChangeRememberMe={this.onRememberMeClick}
        onLoginButtonClick={this.onLoginButtonClick}
      />
    );
    if (this.state.isAuthenticated) {
      return this.props.children;
    }

    return (
      <LoginPage
        className="pf-login"
        footerListVariants="inline"
        brandImgSrc={brandImage}
        brandImgAlt="Kogito logo"
        backgroundImgSrc={images}
        backgroundImgAlt="Images"
        loginTitle="Log in to your account"
        loginSubtitle="Please use your single sign-on LDAP credentials"
        textContent="CLOUD-NATIVE BUSINESS AUTOMATION FOR BUILDING INTELLIGENT APPLICATIONS, BACKED BY BATTLE-TESTED CAPABILITIES."
        signUpForAccountMessage={signUpForAccountMessage}
        forgotCredentials={forgotCredentials}
      >
        {loginForm}
      </LoginPage>
    );
  }
}

export default Login;
