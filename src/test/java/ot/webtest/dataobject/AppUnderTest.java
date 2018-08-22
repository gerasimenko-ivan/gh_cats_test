package ot.webtest.dataobject;

public class AppUnderTest {
    public String url;
    public String version;

    public AppUnderTest withUrl (String url) {
        this.url = url;
        return this;
    }

    public AppUnderTest withVersion (String version) {
        this.version = version;
        return this;
    }
}
