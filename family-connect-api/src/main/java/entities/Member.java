package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

/**
 * Class to represent Member.
 */
@AllArgsConstructor
public final class Member {
    @NonNull
    @Getter
    private final String id;
    @NonNull
    @Getter
    private final String name;
    @Getter
    private final int age;
    @Getter
    private final boolean isGenderMale; // Male-true, Female-false

    public Member(String id, String name, String age, String isGenderMale) {
        this(id, name, Integer.parseInt(age), Boolean.parseBoolean(isGenderMale));
    }

    /* No setters written, to achieve Immutability */

    public boolean areAllAttributesMatching(Member member) {
        return this.id.equalsIgnoreCase(member.id)
                && this.name.equalsIgnoreCase(member.name)
                && this.age == member.age
                && this.isGenderMale == member.isGenderMale;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Member)
                && ((Member) obj).id.equals(this.id);
    }

    @Override
    public String toString() {
        return "(" + this.id + ")" + this.name;
    }
}
