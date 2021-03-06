package edu.berkeley.ground.api.usage.neo4j;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.berkeley.ground.api.Neo4jTest;
import edu.berkeley.ground.api.usage.LineageEdgeVersion;
import edu.berkeley.ground.api.models.Tag;
import edu.berkeley.ground.api.versions.GroundType;
import edu.berkeley.ground.exceptions.GroundException;
import static org.junit.Assert.*;

public class Neo4jLineageEdgeVersionFactoryTest extends Neo4jTest {

    public Neo4jLineageEdgeVersionFactoryTest() throws GroundException {
        super();
    }

    @Test
    public void testLineageEdgeVersionCreation() throws GroundException {
        String firstTestNode = "firstTestNode";
        String firstTestNodeId = super.factories.getNodeFactory().create(firstTestNode).getId();
        String firstNodeVersionId = super.factories.getNodeVersionFactory().create(new HashMap<>(),
                null, null, new HashMap<>(), firstTestNodeId, new ArrayList<>()).getId();

        String secondTestNode = "secondTestNode";
        String secondTestNodeId = super.factories.getNodeFactory().create(secondTestNode).getId();
        String secondNodeVersionId = super.factories.getNodeVersionFactory().create(new HashMap<>(),
                null, null, new HashMap<>(), secondTestNodeId, new ArrayList<>()).getId();

        String lineageEdgeName = "testLineageEdge";
        String lineageEdgeId = super.factories.getLineageEdgeFactory().create(lineageEdgeName).getId();

        String structureName = "testStructure";
        String structureId =  super.factories.getStructureFactory().create(structureName).getId();

        Map<String, GroundType> structureVersionAttributes = new HashMap<>();
        structureVersionAttributes.put("intfield", GroundType.INTEGER);
        structureVersionAttributes.put("boolfield", GroundType.BOOLEAN);
        structureVersionAttributes.put("strfield", GroundType.STRING);

        String structureVersionId = super.factories.getStructureVersionFactory().create(
                structureId, structureVersionAttributes, new ArrayList<>()).getId();

        Map<String, Tag> tags = new HashMap<>();
        tags.put("intfield", new Tag(null, "intfield", 1, GroundType.INTEGER));
        tags.put("strfield", new Tag(null, "strfield", "1", GroundType.STRING));
        tags.put("boolfield", new Tag(null, "boolfield", true, GroundType.BOOLEAN));

        String testReference = "http://www.google.com";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("http", "GET");

        String lineageEdgeVersionId = super.factories.getLineageEdgeVersionFactory().create(tags,
                structureVersionId, testReference, parameters, firstNodeVersionId,
                secondNodeVersionId, lineageEdgeId, new ArrayList<>()).getId();

        LineageEdgeVersion retrieved = super.factories.getLineageEdgeVersionFactory()
                .retrieveFromDatabase(lineageEdgeVersionId);

        assertEquals(lineageEdgeId, retrieved.getLineageEdgeId());
        assertEquals(structureVersionId, retrieved.getStructureVersionId());
        assertEquals(testReference, retrieved.getReference());
        assertEquals(retrieved.getFromId(), firstNodeVersionId);
        assertEquals(retrieved.getToId(), secondNodeVersionId);

        assertEquals(parameters.size(), retrieved.getParameters().size());
        assertEquals(tags.size(), retrieved.getTags().size());

        Map<String, String> retrievedParameters = retrieved.getParameters();
        Map<String, Tag> retrievedTags = retrieved.getTags();

        for (String key : parameters.keySet()) {
            assert(retrievedParameters).containsKey(key);
            assertEquals(parameters.get(key), retrievedParameters.get(key));
        }

        for (String key : tags.keySet()) {
            assert(retrievedTags).containsKey(key);
            assertEquals(tags.get(key), retrievedTags.get(key));
        }
    }
}
