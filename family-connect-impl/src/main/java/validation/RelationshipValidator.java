package validation;

import core.Family;
import entities.ConnectionEdge;
import entities.Member;
import relationship.GenericRelation;
import relationship.SpecificRelation;

/**
 * Class to validate Possible relationship
 */
public class RelationshipValidator implements Validator {
    private Validator nextValidator;

    @Override
    public void setNextValidatorInChain(Validator validator) {
        this.nextValidator = validator;
    }

    @Override
    public boolean validate(Member p1, GenericRelation genericRelation, Member p2, int relationLevel, Family family) {
        // It's Ok to compare generic relations as it has already passed the gender validation.
        ConnectionEdge possibleConnection = family.getConnection(p1, p2, false);
        boolean isValid;
        if (possibleConnection == null) {
            // Which means these two Persons are not connected at all, directly or indirectly.
            isValid = true;
        } else {
            boolean isRelationLevelValid;
            switch (genericRelation) {
                case GRANDPARENT:
                    isRelationLevelValid = relationLevel >= possibleConnection.relationLevel();
                    break;
                case GRANDCHILD:
                    isRelationLevelValid = relationLevel <= possibleConnection.relationLevel();
                    break;
                default:
                    isRelationLevelValid = relationLevel == possibleConnection.relationLevel();
            }
            isValid = isRelationLevelValid &&
                    (genericRelation.equals(possibleConnection.relation())
                            || genericRelation.getAlternateRelation().equals(possibleConnection.relation()));
        }
        return (nextValidator == null) ? isValid : isValid && nextValidator.validate(p1, genericRelation, p2, relationLevel,
                family);
    }

    @Override
    public boolean validate(Member p1, SpecificRelation specificRelation, Member p2, int relationLevel, Family family) {
        return this.validate(p1, specificRelation.getGenericRelation(), p2, relationLevel, family);
    }
}
