package smithy.examples.trait;

import com.google.auto.service.AutoService;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.StringNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitService;
import software.amazon.smithy.utils.ToSmithyBuilder;

import java.util.Optional;

/**
 * Copies a member to a new Shape, that may or may not exist in the model. If
 * not, the new Shape is created; otherwise, the member is added to the existing
 * shape.
 *
 * If a new type is defined, it should be defined either by complete ShapeId or
 * default to the current namespace.
 *
 * Example usage:
 *
 * <pre>
 * &#64;copy(
 *   newShapeName: "MyNewShapeName",
 *   newType: "IntegerList"
 *)
 * </pre>
 *
 * This is implemented as a concrete trait so that transformations can access
 * the data within the trait. Otherwise, it will be created as a
 * {@link DynamicTrait} which prevents access to the trait data.
 *
 * Follows the Builder design pattern.
 */
public class CopyTrait extends AbstractTrait
        implements ToSmithyBuilder<CopyTrait> {

    private static final ShapeId ID = ShapeId.fromParts("smithy.examples.main",
            "copy");

    private static final String NEW_SHAPE_NAME = "newShapeName";
    private static final String NEW_MEMBER_NAME = "newMemberName";
    private static final String NEW_TYPE = "newType";

    // Since this is a structured trait, the members of the trait are known, so
    // they
    // can be represented as Strings.
    //
    // However, there is no restriction on the format of the trait and the
    // internal
    // representation; this could be represented as a Map as well.
    private final String newShapeName;
    private final String newMemberName;
    private final String newType;

    private CopyTrait(Builder builder) {
        super(ID, builder.getSourceLocation());
        this.newShapeName = builder.newShapeName;
        this.newMemberName = builder.newMemberName;
        this.newType = builder.newType;
    }

    /**
     * The opposite logic of {@link Provider#createTrait}: create a {@link Node}
     * from the data in {@link CopyTrait}.
     */
    @Override
    protected Node createNode() {
        ObjectNode.Builder objectNodeBuilder = ObjectNode.objectNodeBuilder();
        objectNodeBuilder.withOptionalMember(NEW_SHAPE_NAME,
                Optional.ofNullable(newShapeName).map(StringNode::from));
        objectNodeBuilder.withOptionalMember(NEW_MEMBER_NAME,
                Optional.ofNullable(newMemberName).map(StringNode::from));
        objectNodeBuilder.withOptionalMember(NEW_TYPE,
                Optional.ofNullable(newType).map(StringNode::from));
        return objectNodeBuilder.build();
    }

    @Override
    public Builder toBuilder() {
        return new Builder().newShapeName(newShapeName)
                .newMemberName(newMemberName)
                .newType(newType);
    }

    /**
     * The builder used to create a {@link CopyTrait}.
     *
     * This Builder is used by the Provider to construct a {@link CopyTrait}
     * from a Node (which is the internal representation of a Smithy trait).
     */
    public static final class Builder
            extends AbstractTraitBuilder<CopyTrait, Builder> {
        // There are no restrictions for the Builder to have the same fields as
        // the trait.
        // The Builder should have fields to support a simple conversion from a
        // Smithy node.
        private String newShapeName;
        private String newMemberName;
        private String newType;

        /**
         * Sets the {@link newShapeName} property of the builder.
         *
         * @param newShapeName the name of the new shape to add to the Smithy
         *                     model
         * @return the builder
         */
        public Builder newShapeName(String newShapeName) {
            this.newShapeName = newShapeName;
            return this;
        }

        /**
         * Sets the {@link newMemberName} property of the builder.
         *
         * @param newMemberName the name of the member in the new shape
         * @return the builder
         */
        public Builder newMemberName(String newMemberName) {
            this.newMemberName = newMemberName;
            return this;
        }

        /**
         * Sets the {@link newType} property of the builder
         *
         * @param newType the type of the new member in the new shape
         * @return the builder
         */
        public Builder newType(String newType) {
            this.newType = newType;
            return this;
        }

        @Override
        public CopyTrait build() {
            return new CopyTrait(this);
        }

    }

    /**
     * A Provider for the {@link CopyTrait} so that it is discoverable via Java
     * SPI.
     *
     * {@link AutoService} annotation registers with Java SPI.
     */
    @AutoService(TraitService.class)
    public static final class Provider implements TraitService {

        @Override
        public ShapeId getShapeId() {
            return ID;
        }

        @Override
        public Trait createTrait(ShapeId target, Node value) {
            // because the copy trait is a StructureShape, it is represented as
            // an ObjectNode
            // the members of the ObjectNode are the members of the copy
            // structure
            ObjectNode traitData = value.expectObjectNode();

            // since a StructureShape has fixed fields, we can access them by
            // name
            Optional<String> newShapeName = traitData
                    .getStringMember(NEW_SHAPE_NAME)
                    .map(StringNode::getValue);
            Optional<String> newMemberName = traitData
                    .getStringMember(NEW_MEMBER_NAME)
                    .map(StringNode::getValue);
            Optional<String> newType = traitData.getStringMember(NEW_TYPE)
                    .map(StringNode::getValue);

            Builder copyTraitBuilder = new Builder().sourceLocation(value);
            newShapeName.ifPresent(copyTraitBuilder::newShapeName);
            newMemberName.ifPresent(copyTraitBuilder::newMemberName);
            newType.ifPresent(copyTraitBuilder::newType);

            return copyTraitBuilder.build();
        }

    }

}
