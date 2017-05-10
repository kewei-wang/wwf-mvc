package com.chitucode.hrsm.entity;

import java.util.Date;
import java.util.List;

/**
 * Created by kowaywang on 17/4/10.
 */
public class Person {

    private Long id;

    private String userName;

    private List<Long> deptIds;

    private Integer[] raits;

    private Date birthday;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Long> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(List<Long> deptIds) {
        this.deptIds = deptIds;
    }

    public Integer[] getRaits() {
        return raits;
    }

    public void setRaits(Integer[] raits) {
        this.raits = raits;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
