package ot.webtest.framework.testrail.dataobject;

public class TestRailCredentials {
    public String username;
    public String password;
    public String railsEngineUrl;

    public String proxyHost;
    public Integer proxyPort;

    public TestRailCredentials withUsername(String username) {
        this.username = username;
        return this;
    }
    public TestRailCredentials withPassword(String password) {
        this.password = password;
        return this;
    }
    public TestRailCredentials withRailsEngineUrl(String railsEngineUrl) {
        this.railsEngineUrl = railsEngineUrl;
        return this;
    }

    public TestRailCredentials withProxyHost(String railsEngineUrl) {
        this.proxyHost = railsEngineUrl;
        return this;
    }

    public TestRailCredentials withProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }
}
