package core;

import entities.ConnectionEdge;
import entities.Member;
import relationship.GenericRelation;

public interface Family {
    /**
     * Connects two persons in family, this method wrote to ease unit testing
     *
     * @param p1Id     From Member Id
     * @param relation Relation between Persons as String
     * @param p2Id     To Member Id
     */
    void connectPersons(String p1Id, String relation, String p2Id);

    /**
     * Connects two persons in the family with Generic relation
     *
     * @param p1               From Member
     * @param IGenericRelation Generic Relation between Persons
     * @param p2               To Member
     * @param doValidate       Switch to turn validation on or off
     */
    void connectPersons(Member p1, GenericRelation IGenericRelation, Member p2, int relationLevel, boolean doValidate);

    /**
     * Returns the direct/indirect connection between two persons
     *
     * @param p1             From Member
     * @param p2             To Member
     * @param doBatchConnect
     * @return Connection
     */
    ConnectionEdge getConnection(Member p1, Member p2, boolean doBatchConnect);

    Member getPersonById(String fromPid);
}
