package com.gakshintala.mylabspace.familyconnectdb.api.db;

import com.gakshintala.mylabspace.familyconnectdb.api.model.Member;
import jooq.db.gen.tables.records.MemberRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static jooq.db.gen.tables.Member.MEMBER;

@Repository
@Transactional
public class MemberRepo {

    @Autowired
    private DSLContext dsl;

    public void createUser(Member member) {
        dsl.insertInto(MEMBER)
                .columns(MEMBER.NAME, MEMBER.AGE)
                .values(member.getName(), member.getAge())
                .execute();
    }

    public Member getUser(String email) {
        MemberRecord user = dsl.select()
                .from(MEMBER)
                .where(MEMBER.NAME.eq(email))
                .fetchOne()
                .into(MemberRecord.class);

        return new Member(user);
    }

}
