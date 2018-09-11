package ot.webtest.dataobject;

public class User {
    public String login;
    public String password;
    public String popupLogin;
    public String popupPassword;
    public String surnameNM;

    public User withLogin(String login) {
        this.login = login;
        return this;
    }

    public User withPassword(String password) {
        this.password = password;
        return this;
    }

    public User withPopupLogin(String popupLogin) {
        this.popupLogin = popupLogin;
        return this;
    }

    public User withPopupPassword(String popupPassword) {
        this.popupPassword = popupPassword;
        return this;
    }

    public User withSurnameNM (String surnameNM) {
        this.surnameNM = surnameNM;
        return this;
    }
}
