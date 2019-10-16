package com.akb.springsql.pojo;

import java.io.Serializable;

public class Adminstrator implements Serializable {
    private String account;
    private String pwd;
    private String cookiesk;
    private String vaildtime;

    public String getAccount() {
        return account;
    }

    public String getCookiesk() {
        return cookiesk;
    }

    public String getPwd() {
        return pwd;
    }

    public String getVaildtime() {
        return vaildtime;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setCookiesk(String cookiesk) {
        this.cookiesk = cookiesk;
    }

    public void setVaildtime(String vaildtime) {
        this.vaildtime = vaildtime;
    }

    @Override
    public String toString(){
        return "["+account+","+pwd+","+cookiesk+","+vaildtime+"]";
    }
}
