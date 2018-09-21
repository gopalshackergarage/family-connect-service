package relationship;

import lombok.Getter;

/**
 * Class representing Generic Relations
 */
public enum GenericRelation implements Relation {
    PARENT(1, SpecificRelation.FATHER, SpecificRelation.MOTHER) {
        @Override
        public GenericRelation getNextGenericRelation(GenericRelation curRelation) {
            switch (curRelation) {
                case PARENT:
                case KIN:
                    return GRANDPARENT;
                case CHILD:
                    return SIBLING;
                case NIBLING:
                    return COUSIN;
                case SPOUSE:
                    return PARENT;
                case SIBLING:
                case COUSIN:
                    return KIN;
                case GRANDPARENT:
                    return GRANDPARENT;
                case GRANDCHILD:
                    return NIBLING;
            }
            return null;
        }
    },
    KIN(1, SpecificRelation.UNCLE, SpecificRelation.AUNT) {
        @Override
        public GenericRelation getNextGenericRelation(GenericRelation curRelation) {
            switch (curRelation) {
                case PARENT:
                case KIN:
                    return GRANDPARENT;
                case CHILD:
                case NIBLING:
                    return COUSIN;
                case SPOUSE:
                case SIBLING:
                case COUSIN:
                    return KIN;
                case GRANDPARENT:
                    return GRANDPARENT;
                case GRANDCHILD:
                    return NIBLING;
            }
            return null;
        }
    },
    CHILD(-1, SpecificRelation.SON, SpecificRelation.DAUGHTER) {
        @Override
        public GenericRelation getNextGenericRelation(GenericRelation curRelation) {
            switch (curRelation) {
                case PARENT:
                    return SPOUSE;
                case CHILD:
                case NIBLING:
                    return GRANDCHILD;
                case KIN:
                    return COUSIN; // If no direct connection between the two, No way to figure out if COUSIN OR SIBLING
                case SPOUSE:
                case COUSIN:
                    return NIBLING;
                case SIBLING:
                    return CHILD;
                case GRANDPARENT:
                    return KIN;
                case GRANDCHILD:
                    return GRANDCHILD;
            }
            return null;
        }
    },
    NIBLING(-1, SpecificRelation.NEPHEW, SpecificRelation.NIECE) {
        @Override
        public GenericRelation getNextGenericRelation(GenericRelation curRelation) {
            switch (curRelation) {
                case PARENT:
                case KIN:
                    return COUSIN;
                case CHILD:
                case NIBLING:
                    return GRANDCHILD;
                case SPOUSE:
                case SIBLING:
                case COUSIN:
                    return NIBLING;
                case GRANDPARENT:
                    return KIN;
                case GRANDCHILD:
                    return GRANDCHILD;
            }
            return null;
        }
    },
    GRANDPARENT(2, SpecificRelation.GRANDFATHER, SpecificRelation.GRANDMOTHER) {
        @Override
        public GenericRelation getNextGenericRelation(GenericRelation curRelation) {
            switch (curRelation) {
                case PARENT:
                case KIN:
                case GRANDPARENT:
                    return GRANDPARENT;
                case SIBLING:
                case SPOUSE:
                case COUSIN:
                    return GRANDPARENT;
                case CHILD:
                case NIBLING:
                    return KIN;
                case GRANDCHILD:
                    return COUSIN;
            }
            return null;
        }
    },
    GRANDCHILD(-2, SpecificRelation.GRANDSON, SpecificRelation.GRANDDAUGHTER) {
        @Override
        public GenericRelation getNextGenericRelation(GenericRelation curRelation) {
            switch (curRelation) {
                case PARENT:
                case KIN:
                    return NIBLING;
                case CHILD:
                case NIBLING:
                case GRANDCHILD:
                    return GRANDCHILD;
                case SPOUSE:
                case SIBLING:
                case COUSIN:
                    return GRANDCHILD;
                case GRANDPARENT:
                    return COUSIN;
            }
            return null;
        }
    },

    SPOUSE(0, SpecificRelation.HUSBAND, SpecificRelation.WIFE) {
        @Override
        public GenericRelation getNextGenericRelation(GenericRelation curRelation) {
            switch (curRelation) {
                case PARENT:
                    return KIN;
                case CHILD:
                case NIBLING:
                case GRANDPARENT:
                case GRANDCHILD:
                case KIN:
                case COUSIN:
                    return curRelation;
                case SPOUSE:
                case SIBLING:
                    return COUSIN;
            }
            return null;
        }
    },
    SIBLING(0, SpecificRelation.BROTHER, SpecificRelation.SISTER) {
        @Override
        public GenericRelation getNextGenericRelation(GenericRelation curRelation) {
            switch (curRelation) {
                case PARENT:
                case NIBLING:
                case KIN:
                case GRANDPARENT:
                case GRANDCHILD:
                case COUSIN:
                case SIBLING:
                    return curRelation;
                case CHILD:
                    return NIBLING;
                case SPOUSE:
                    return COUSIN;

            }
            return null;
        }
    },
    COUSIN(0, SpecificRelation.COUSIN, SpecificRelation.COUSIN) {
        @Override
        public GenericRelation getNextGenericRelation(GenericRelation curRelation) {
            switch (curRelation) {
                case GRANDPARENT:
                case GRANDCHILD:
                case KIN:
                case NIBLING:
                case COUSIN:
                    return curRelation;
                case PARENT:
                    return KIN;
                case CHILD:
                    return NIBLING;
                case SPOUSE:
                case SIBLING:
                    return COUSIN;
            }
            return null;
        }
    };

    static {
        PARENT.reverseRelation = CHILD;
        KIN.reverseRelation = NIBLING;
        CHILD.reverseRelation = PARENT;
        NIBLING.reverseRelation = KIN;
        GRANDPARENT.reverseRelation = GRANDCHILD;
        GRANDCHILD.reverseRelation = GRANDPARENT;
        SPOUSE.reverseRelation = SPOUSE;
        SIBLING.reverseRelation = SIBLING;
        COUSIN.reverseRelation = COUSIN;
    }

    static {
        PARENT.alternateRelation = KIN.alternateRelation = PARENT;
        CHILD.alternateRelation = NIBLING.alternateRelation = CHILD;
        SIBLING.alternateRelation = COUSIN.alternateRelation = COUSIN;

        GRANDPARENT.alternateRelation = GRANDPARENT;
        GRANDCHILD.alternateRelation = GRANDCHILD;
        SPOUSE.alternateRelation = SPOUSE;
    }

    @Getter
    private final int relationLevel;
    @Getter
    private final SpecificRelation maleRelation;
    @Getter
    private final SpecificRelation femaleRelation;
    @Getter
    private GenericRelation reverseRelation;
    @Getter
    private GenericRelation alternateRelation;

    GenericRelation(int relationLevel, SpecificRelation maleRelation, SpecificRelation femaleRelation) {
        this.relationLevel = relationLevel;
        // In case parameters are provided in reverse order of gender, store them in proper order.
        // Null means neutral, used for COUSIN
        if (maleRelation.isRelationMale() == null || maleRelation.isRelationMale()) {
            this.maleRelation = maleRelation;
            this.femaleRelation = femaleRelation;
        } else {
            this.maleRelation = femaleRelation;
            this.femaleRelation = maleRelation;
        }
        this.maleRelation.setGenericRelation(this);
        this.femaleRelation.setGenericRelation(this);
    }

    public SpecificRelation getGenderSpecificRelation(boolean isMale) {
        return isMale ? this.maleRelation : this.femaleRelation;
    }

    public GenericRelation getNextGenericRelation(GenericRelation relation) {
        return null;
    }
    
}
