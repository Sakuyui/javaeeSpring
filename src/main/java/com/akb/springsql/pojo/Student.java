package com.akb.springsql.pojo;

import java.io.Serializable;

public class Student implements Serializable,Comparable {
    private String id;
    private String name;
    private String qq;
    private String phone;
    private String email;
    private String img;
    private String ckey;
    private static final long serialVersionUID=1L;


    public Student(){

    }

    public Student(String id,String name,String phone,String qq,String email){
        this.id=id;
        this.name=name;
        this.phone=phone;
        this.qq=qq;
        this.email=email;
    }

    public String getCkey() {
        return ckey;
    }

    public void setCkey(String ckey) {
        this.ckey = ckey;
    }

    public String getImg(){
        return this.img;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getQq() {
        return qq;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString(){
        return id+","+name;
    }


    public boolean isMatchee_V(String str){
        if(id!=null && this.id.trim().indexOf(str)>=0) return true;
        if(name!=null && this.name.trim().indexOf(str)>=0) return true;
        if(phone!=null && this.phone.trim().indexOf(str)>=0) return true;
        if(qq!=null && this.qq.trim().indexOf(str)>=0) return true;
        if(email!=null && this.email.trim().indexOf(str)>=0) return true;
        return false;
    }

    public int compareTo(Object obj){
        if(!(obj instanceof Student)){
            return -1;
        }else{
            return this.id.compareTo(((Student)obj).getId());
        }
    }

}
