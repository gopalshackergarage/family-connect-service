package core;

import entities.ConnectionEdge;
import entities.Person;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import relationship.GenericRelation;
import relationship.Relation;
import relationship.SpecificRelation;
import validation.Validator;

import java.util.*;
import java.util.stream.Collectors;

import static utils.FilterUtils.*;
import static utils.RelationUtils.parseToGenericRelation;

/**
 * This is the central Data Structure that holds all the Persons in the family and their corresponding connections.
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FamilyGraph implements Family {
    private final Map<String, Person> personIdMap = new HashMap<>(); // Represents all the persons put into the graph.
    private final Map<Person, Set<ConnectionEdge>> relationMap = new HashMap<>();
    @NonNull
    @Getter
    private final Validator validator;

    /**
     * Returns all the neighbour direct relations of a persons
     *
     * @param person Person to find neighbours
     * @return neighbours of the person
     */
    public Set<ConnectionEdge> getAllNeighbourConnections(Person person) {
        return relationMap.get(person);
    }

    /**
     * Adds a Person to family graph, ignores if the person is already present
     *
     * @param person Person to add
     */
    public void addPerson(Person person) {
        if (!relationMap.containsKey(person)) {
            personIdMap.put(person.getId(), person);
            relationMap.put(person, new HashSet<>());
        }
    }

    /**
     * Adds a Person through Person attributes
     *
     * @param id     Id of the person
     * @param name   Name of the person
     * @param age    Age of the person
     * @param isMale Gender of the person
     */
    public void addPerson(String id, String name, String age, String isMale) {
        this.addPerson(new Person(id, name, age, isMale));
    }

    /**
     * Connects two persons in family, this method wrote to ease unit testing
     *
     * @param p1Id     From Person Id
     * @param relation Relation between Persons as String
     * @param p2Id     To Person Id
     */
    @Override
    public void connectPersons(String p1Id, String relation, String p2Id) {
        var p1 = personIdMap.get(p1Id);
        var p2 = personIdMap.get(p2Id);
        // If we are given only ID, we can't create a person object without other attributes, so we check his existence 
        // instead of adding him to family if he is new.
        if (p1 == null) {
            throw new IllegalArgumentException("Person with Id: " + p1Id + " not found in family to connect");
        }
        if (p2 == null) {
            throw new IllegalArgumentException("Person with Id: " + p2Id + " not found in family to connect");
        }
        // relation string parameter can either be generic or specific
        GenericRelation GenericRelation = parseToGenericRelation(relation);
        connectPersons(p1, GenericRelation, p2, GenericRelation.getRelationLevel(), true);
    }


    /**
     * Connects two persons in the family with Generic relation
     *
     * @param p1              From Person
     * @param GenericRelation Generic Relation between Persons
     * @param p2              To Person
     * @param doValidate      Switch to turn validation on or off
     */
    public void connectPersons(Person p1, GenericRelation GenericRelation, Person p2, int relationLevel, boolean
            doValidate) {
        addPerson(p1);
        addPerson(p2);
        if (doValidate && !validator.validate(p1, GenericRelation, p2, relationLevel, this)) {
            throw new IllegalArgumentException(new ConnectionEdge(p1, GenericRelation, p2) + " is NOT a valid Relation");
        }
        relationMap.get(p1).add(new ConnectionEdge(p1, GenericRelation, p2, relationLevel));
        relationMap.get(p2).add(new ConnectionEdge(p2, GenericRelation.getReverseRelation(), p1, -relationLevel));
    }

    /**
     * Connects two persons in the family with Specific relation
     *
     * @param p1               From Person Id
     * @param SpecificRelation Specific Relation between Persons
     * @param p2               To Person Id
     */
    public void connectPersons(Person p1, SpecificRelation SpecificRelation, Person p2, int relationLevel, boolean
            doValidate) {
        this.connectPersons(p1, SpecificRelation.getGenericRelation(), p2, relationLevel, doValidate);
    }

    /**
     * Batch connects multiple pairs of persons
     *
     * @param connections List of connections to connect
     */
    public void batchConnectPersons(Set<ConnectionEdge> connections) {
        connections.forEach(connection -> {
            Person from = connection.from();
            Person to = connection.to();
            if (!arePersonsDirectlyConnected(from, to)) {
                // No need of validation as the connections are already validated while initially connecting.
                connectPersons(from, connection.relation(), to, connection.relationLevel(), false);
            }
        });
    }

    /**
     * Disconnects persons
     *
     * @param p1 From person
     * @param p2 To Person
     */
    public void removeDirectConnection(Person p1, Person p2) {
        if (!arePersonsDirectlyConnected(p1, p2)) {
            throw new IllegalArgumentException(p1 + " is NOT directly connected to " + p2);
        }
        for (Iterator<ConnectionEdge> iterator = getAllNeighbourConnections(p1).iterator(); iterator.hasNext(); ) {
            ConnectionEdge connection = iterator.next();
            if (connection.to().equals(p2)) {
                iterator.remove();
                return;
            }
        }
    }

    /**
     * Checks if two person are directly connected
     *
     * @param p1 Person 1
     * @param p2 Person 2
     * @return True if directly Connected
     */
    public boolean arePersonsDirectlyConnected(Person p1, Person p2) {
        for (ConnectionEdge connection : getAllNeighbourConnections(p1)) {
            if (p2.equals(connection.to()))
                return true;
        }
        return false;
    }

    /**
     * Returns Person with that Id.
     *
     * @param pId Id of person
     * @return Person with Id
     */
    @Override
    public Person getPersonById(String pId) {
        Person person = personIdMap.get(pId);
        if (person == null) {
            throw new IllegalArgumentException("Person Id: " + pId + " NOT present in family");
        }
        return person;
    }

    /**
     * Returns all Persons in family
     *
     * @return Collection of all persons in family
     */
    public Collection<Person> getAllPersonsInFamily() {
        return personIdMap.values();
    }

    /**
     * Returns the direct/indirect connection between two persons
     *
     * @param p1             From Person
     * @param p2             To Person
     * @param doBatchConnect
     * @return Connection
     */
    public ConnectionEdge getConnection(Person p1, Person p2, boolean doBatchConnect) {
        var connection = bfsTraverseFamilyGraph(p1, p2, null, doBatchConnect);
        // If p2 is not reached, both are not connected
        return (connection != null && connection.to().equals(p2)) ? connection : null;
    }

    /**
     * Returns all connections the person have with all other persons in family
     *
     * @param person                              Person for whom the graph is queried
     * @param makeNewConnectionsFoundDuringSearch Boolean to indicate if establish all new connections found during search
     * @return List of all Connections the person have with all other persons in family
     */
    public Collection<ConnectionEdge> getAllConnectionsInFamilyForPerson(Person person, boolean makeNewConnectionsFoundDuringSearch) {
        Set<ConnectionEdge> connectionsToPopulate = new HashSet<>();
        bfsTraverseFamilyGraph(person, null, connectionsToPopulate, makeNewConnectionsFoundDuringSearch);
        return connectionsToPopulate;
    }

    /**
     * Traverse Family graph in Breadth-First way, to populate connectionsToPopulate and returns connection with aggregate
     * relation. This is used by both getAllConnectionsInFamilyForPerson and getConnection
     *
     * @param p1                                  From Person
     * @param p2                                  To Person
     * @param connectionsToPopulate               Connections to be populated for family graph
     * @param makeNewConnectionsFoundDuringSearch Boolean to indicate if establish all new connections found during search
     * @return Connection with aggregate relation
     */
    private ConnectionEdge bfsTraverseFamilyGraph(Person p1, Person p2, Set<ConnectionEdge> connectionsToPopulate,
                                                  boolean makeNewConnectionsFoundDuringSearch) {
        if (p1 == null || personIdMap.get(p1.getId()) == null) {
            throw new IllegalArgumentException("Person " + p1 + " not found in family");
        }
        if (p2 != null && personIdMap.get(p2.getId()) == null) {
            throw new IllegalArgumentException("Person " + p2 + " not found in family");
        }

        Queue<Person> queue = new LinkedList<>();
        Map<Person, Boolean> visited = new HashMap<>();
        Map<Person, ConnectionEdge> relationMap = new HashMap<>();
        ConnectionEdge previousConnection = null;
        Person neighbourRelative;
        GenericRelation currentRelation, nextRelation;

        boolean isGettingFamilyGraphForPerson = (p2 == null);
        if (isGettingFamilyGraphForPerson && connectionsToPopulate == null) {
            connectionsToPopulate = new HashSet<>();
        }

        queue.add(p1);
        visited.put(p1, true);
        loop:
        while (!queue.isEmpty()) {
            Person p = queue.poll();
            for (ConnectionEdge edge : getAllNeighbourConnections(p)) {
                if (visited.get(edge.to()) == null) {
                    neighbourRelative = edge.to();
                    previousConnection = relationMap.get(edge.from());
                    if (previousConnection == null) {
                        previousConnection = edge;
                    } else {
                        currentRelation = edge.relation();
                        nextRelation = currentRelation.getNextGenericRelation(previousConnection.relation());
                        previousConnection = new ConnectionEdge(p1, nextRelation, neighbourRelative,
                                previousConnection.relationLevel() + currentRelation.getRelationLevel());
                    }

                    if (isGettingFamilyGraphForPerson) {
                        connectionsToPopulate.add(previousConnection);
                    } else if (neighbourRelative.equals(p2)) {
                        break loop;
                    }
                    relationMap.put(neighbourRelative, previousConnection);
                    queue.add(neighbourRelative);
                    visited.put(neighbourRelative, true);
                }
            }
        }
        if (makeNewConnectionsFoundDuringSearch) {
            // Adding connection results as we find, to improve future searches
            batchConnectPersons(connectionsToPopulate);
        }
        return previousConnection;
    }

    /**
     * Returns map containing path from one Person to another
     *
     * @param p1 From Person
     * @param p2 To Person
     * @return Path map.
     */
    private Map<Person, ConnectionEdge> getConnectionPath(Person p1, Person p2) {
        if (p1 == null || personIdMap.get(p1.getId()) == null) {
            throw new IllegalArgumentException("Person " + p1 + " not found in family");
        }
        if (p2 == null || personIdMap.get(p2.getId()) == null) {
            throw new IllegalArgumentException("Person " + p2 + " not found in family");
        }
        var connectionPathMap = new HashMap<Person, ConnectionEdge>();
        var queue = new LinkedList<Person>();
        var visited = new HashMap<Person, Boolean>();

        queue.add(p1);
        visited.put(p1, true);
        while (!queue.isEmpty()) {
            Person p = queue.poll();
            for (ConnectionEdge edge : getAllNeighbourConnections(p)) {
                if (visited.get(edge.to()) == null) {
                    Person neighbourRelative = edge.to();
                    connectionPathMap.put(neighbourRelative, edge);
                    if (neighbourRelative.equals(p2)) {
                        break;
                    }
                    queue.add(neighbourRelative);
                    visited.put(p, true);
                }
            }
        }
        return connectionPathMap;
    }

    public List<ConnectionEdge> getShortestRelationChain(Person p1, Person p2) {
        List<ConnectionEdge> connections = new ArrayList<>();
        getAggregateRelationWithRelationChain(p1, p2, getConnectionPath(p1, p2), connections);
        return connections;
    }

    public ConnectionEdge getAggregateConnection(Person p1, Person p2) {
        return getAggregateRelationWithRelationChain(p1, p2, getConnectionPath(p1, p2), null);
    }

    /**
     * Returns aggregate relation and also populates connections chain that led to that relation
     *
     * @param p1             From Person
     * @param p2             To Person
     * @param connectionPath Map having path from p1 to p2
     * @param connections    List to be populated with connection chain
     * @return Aggregate relation
     */
    private ConnectionEdge getAggregateRelationWithRelationChain(Person p1, Person p2,
                                                                 Map<Person, ConnectionEdge> connectionPath, List<ConnectionEdge> connections) {
        ConnectionEdge nextEdge, aggregateConnection = null;
        GenericRelation nextRelation, aggregateRelation = null;
        Person nextPerson = p2;

        while (!nextPerson.equals(p1)) {
            nextEdge = connectionPath.get(nextPerson);
            nextPerson = nextEdge.from();
            nextRelation = nextEdge.relation();
            if (aggregateRelation == null) {
                aggregateRelation = nextRelation;
                aggregateConnection = nextEdge;
            } else {
                aggregateRelation = aggregateRelation.getNextGenericRelation(nextRelation);
                aggregateConnection = new ConnectionEdge(nextPerson, aggregateRelation, p2,
                        nextRelation.getRelationLevel() + aggregateConnection.relationLevel());
            }
            if (connections != null) {
                connections.add(nextEdge);
            }
        }
        if (connections != null) {
            Collections.reverse(connections);
        }
        return aggregateConnection;
    }

    public Collection<ConnectionEdge> getAllMembersFromGenerationLevel(Person person, int generationLevel) {
        // Need to check relations in reverse, so taking inverse of generationLevel
        return filterConnectionsByGenerationLevel(person, -generationLevel, getAllConnectionsInFamilyForPerson(person, false));
    }

    public Collection<Person> getFamilyInOrderOfAge(boolean isOrderAscending) {
        Comparator<Person> ascendingAgeComparator = (p1, p2) -> {
            if (p1.getAge() == p2.getAge()) return 0;
            return (p1.getAge() > p2.getAge()) ? 1 : -1;
        };
        var sortedPersons = new ArrayList(personIdMap.values());
        if (isOrderAscending) {
            sortedPersons.sort(ascendingAgeComparator);
        } else {
            sortedPersons.sort(Collections.reverseOrder(ascendingAgeComparator));
        }
        return sortedPersons;
    }

    public Collection<Person> getAllFamilyMembersOfGender(Boolean isMale) {
        return filterPersonsByGender(isMale, new ArrayList<>(personIdMap.values()));
    }

    public Collection<Person> getAllPersonsByRelation(Person person, Relation relation, int relationLevel) {
        if (relation instanceof GenericRelation) {
            return this.getAllPersonsByRelation(person, (GenericRelation) relation, relationLevel);
        } else {
            return this.getAllPersonsByRelation(person, (SpecificRelation) relation, relationLevel);
        }
    }

    private Collection<Person> getAllPersonsByRelation(Person person, GenericRelation genericRelation, int relationLevel) {
        return this.getAllPersonsByRelation(person, genericRelation, null, relationLevel);
    }

    /**
     * Returns all the Person who are related with that Specific relation
     *
     * @param person
     * @param specificRelation
     * @param relationLevel
     * @return
     */
    public Collection<Person> getAllPersonsByRelation(Person person, SpecificRelation specificRelation, int
            relationLevel) {
        return this.getAllPersonsByRelation(person, specificRelation.getGenericRelation(), specificRelation
                .isRelationMale(), relationLevel);
    }

    private Collection<Person> getAllPersonsByRelation(Person person, GenericRelation genericRelation,
                                                       Boolean isRelationMale, int relationLevel) {
        GenericRelation reverseRelation = genericRelation.getReverseRelation();
        return filterConnectionsBySpecificRelation(reverseRelation, isRelationMale, -relationLevel,
                getAllConnectionsInFamilyForPerson(person, false))
                .stream()
                .map(ConnectionEdge::to)
                .collect(Collectors.toList());
    }

    public boolean isPersonRelatedWithRelation(Person person, Relation relation, int relationLevel) {
        if (relation instanceof GenericRelation) {
            return this.isPersonRelatedWithRelation(person, (GenericRelation) relation, relationLevel);
        } else {
            return this.isPersonRelatedWithRelation(person, (SpecificRelation) relation, relationLevel);
        }
    }

    private boolean isPersonRelatedWithRelation(Person person, SpecificRelation specificRelation, int relationLevel) {
        return this.isPersonRelatedWithRelation(person, specificRelation.getGenericRelation(),
                specificRelation.isRelationMale(), relationLevel, getAllNeighbourConnections(person))
                || this.isPersonRelatedWithRelation(person, specificRelation.getGenericRelation(),
                specificRelation.isRelationMale(), relationLevel, getAllConnectionsInFamilyForPerson(person, false));
    }

    private boolean isPersonRelatedWithRelation(Person person, GenericRelation genericRelation, int relationLevel) {
        return this.isPersonRelatedWithRelation(person, genericRelation, null, relationLevel,
                getAllNeighbourConnections(person))
                || this.isPersonRelatedWithRelation(person, genericRelation, null, relationLevel,
                getAllConnectionsInFamilyForPerson(person, false));
    }

    private boolean isPersonRelatedWithRelation(Person person, GenericRelation genericRelation,
                                                Boolean isRelationMale, int relationLevel, Collection<ConnectionEdge> allConnections) {
        if (isRelationMale != null && person.isGenderMale() != isRelationMale) {
            return false;
        }
        for (ConnectionEdge connection : allConnections) {
            if (connection.relationLevel() == relationLevel && connection.relation().equals(genericRelation)) {
                return true;
            }
        }
        return false;
    }

}
