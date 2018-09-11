package coreGraph;

import entities.ConnectionEdge;
import entities.Person;
import relationship.GenericRelation;

public interface FamilyGraph {
    /**
     * Connects two persons in family, this method wrote to ease unit testing
     *
     * @param p1Id     From Person Id
     * @param relation Relation between Persons as String
     * @param p2Id     To Person Id
     */
    void connectPersons(String p1Id, String relation, String p2Id);

    /**
     * Connects two persons in the family with Generic relation
     *
     * @param p1               From Person
     * @param IGenericRelation Generic Relation between Persons
     * @param p2               To Person
     * @param doValidate       Switch to turn validation on or off
     */
    void connectPersons(Person p1, GenericRelation IGenericRelation, Person p2, int relationLevel, boolean doValidate);

    /**
     * Returns the direct/indirect connection between two persons
     *
     * @param p1             From Person
     * @param p2             To Person
     * @param doBatchConnect
     * @return Connection
     */
    ConnectionEdge getConnection(Person p1, Person p2, boolean doBatchConnect);

    Person getPersonById(String fromPid);
}
