package com.gakshintala.mylabspace.familyconnectdb.api.model;

import jooq.db.gen.tables.records.MemberRecord;

public class Member {
    private String name;
    Short age;

    public Member(MemberRecord member) {
        this.name = member.getName();
        this.age = member.getAge();
    }

    public Member() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getAge() {
        return age;
    }

    public void setAge(Short age) {
        this.age = age;
    }
}
