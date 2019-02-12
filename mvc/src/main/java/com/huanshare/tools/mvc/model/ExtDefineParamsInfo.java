package com.huanshare.tools.mvc.model;

public class ExtDefineParamsInfo {
    private String fromSource;
    private String fromSourceId;
    private String requestIp;
    private String userAgent;

    public String getFromSource() {
        return fromSource;
    }

    public void setFromSource(String fromSource) {
        this.fromSource = fromSource;
    }

    public String getFromSourceId() {
        return fromSourceId;
    }

    public void setFromSourceId(String fromSourceId) {
        this.fromSourceId = fromSourceId;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
