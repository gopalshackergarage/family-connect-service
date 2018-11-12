package validation;

import core.Family;
import entities.Member;
import relationship.GenericRelation;
import relationship.SpecificRelation;

/**
 * Class to validate Gender criteria for a relation
 */
public class GenderValidator implements Validator {
    private Validator nextValidator;

    @Override
    public void setNextValidatorInChain(Validator validator) {
        this.nextValidator = validator;
    }

    @Override
    public boolean validate(Member p1, GenericRelation genericRelation, Member p2, int relationLevel, Family family) {
        boolean isValid;
        switch (genericRelation) {
            case SPOUSE:
                isValid = (p1.isGenderMale() != p2.isGenderMale());
                break;
            default:
                isValid = true;
        }
        return (nextValidator == null) ? isValid : isValid && nextValidator.validate(p1, genericRelation, p2, relationLevel,
                family);
    }

    @Override
    public boolean validate(Member p1, SpecificRelation specificRelation, Member p2, int relationLevel, Family family) {
        boolean isValid = (specificRelation.isRelationMale() == p1.isGenderMale());

        switch (specificRelation) {
            case HUSBAND:
            case WIFE:
                isValid &= (p1.isGenderMale() != p2.isGenderMale());
        }
        return (nextValidator == null) ? isValid : isValid && nextValidator.validate(p1, specificRelation, p2,
                relationLevel, family);
    }
}
