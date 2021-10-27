package smithy.trait.examples.traits;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.StringNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(Lifecycle.PER_CLASS)
public class CopyTraitTest {

    private static final ShapeId COPY_TRAIT_SHAPE_ID = ShapeId
            .fromParts("smithy.examples.main", "copy");

    private static final ShapeId SHAPE_ID_FOR_ANNOTATIONS = ShapeId
            .fromParts("a.test.shape.for.traits.to.annotate", "TestShape");

    private TraitFactory traitFactory;

    @BeforeAll
    public void init() {
        traitFactory = TraitFactory.createServiceFactory();
    }

    @Test
    public void loadsTrait() {
        // setup
        Map<StringNode, Node> copyTraitMemberToValueMap = new HashMap<>();
        copyTraitMemberToValueMap.put(StringNode.from("newType"),
                StringNode.from("IntegerList"));
        Node expectedNode = ObjectNode.objectNode(copyTraitMemberToValueMap);

        // trigger
        Optional<Trait> trait = traitFactory.createTrait(COPY_TRAIT_SHAPE_ID,
                SHAPE_ID_FOR_ANNOTATIONS,
                expectedNode);

        // verify
        assertTrue(trait.isPresent());

        CopyTrait copyTrait = (CopyTrait) trait.get();
        assertEquals(CopyTrait.class, copyTrait.getClass());

        assertEquals(expectedNode,
                copyTrait.toNode(),
                "Test toNode re-constructs node");

        assertEquals(copyTrait,
                copyTrait.toBuilder().build(),
                "Test build re-constructs trait");
    }

}
