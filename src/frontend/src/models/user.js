export default class User {
  constructor(username, email, password, user_nickname, social_type, principal) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.user_nickname = user_nickname;
    this.social_type = social_type;
    this.principal = principal;
  }
}
