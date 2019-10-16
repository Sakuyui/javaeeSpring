package com.akb.springsql.pojo;

import java.io.Serializable;

public class FaceSearchResult implements Comparable, Serializable{
    private String PersonId;
    private String Score;
    private Object entity;
    private static final long serialVersionUID=1L;
    public FaceSearchResult(String fid,String score){
        this.PersonId=fid;
        this.Score=score;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public Object getEntity() {
        return entity;
    }

    public void setPersonId(String personId) {
        PersonId = personId;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getPersonId() {
        return PersonId;
    }

    public String getScore() {
        return Score;
    }

    @Override
    public int compareTo(Object obj){
        if(!(obj instanceof FaceSearchResult)) return -1;
        return
                (Double.parseDouble(this.Score)-Double.parseDouble(((FaceSearchResult) obj).Score))<0?-1:1;
    }
}
