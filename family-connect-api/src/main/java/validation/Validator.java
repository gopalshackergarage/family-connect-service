package validation;

import core.Family;
import entities.Member;
import relationship.GenericRelation;
import relationship.SpecificRelation;

/**
 * Interface to abstract Validators
 */
public interface Validator {
    void setNextValidatorInChain(Validator validator);

    boolean validate(Member p1, GenericRelation genericRelation, Member p2, int relationLevel, Family family);

    boolean validate(Member p1, SpecificRelation specificRelation, Member p2, int relationLevel, Family family);

}
