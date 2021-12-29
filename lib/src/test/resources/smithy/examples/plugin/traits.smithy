$version: "1.0"

namespace smithy.examples.traits

/// A structured trait that allows copying a shape's member to a new shape.
///
/// newShapeName - the name of the new shape to add to the Smithy model
/// newMemberName - the new name of the member
/// newType - the new type of the member, after a transformation
///
/// Example: @copy(newShapeName: "MyNewShapeName", type: "IntegerList")
@trait(selector: "structure > member")
structure copy {
    newShapeName: String,
    newMemberName: String,
    @idRef(
        // constraints on what the type can be
        selector: ":test(string, double, integer, [id = smithy.examples#IntegerList], [id = smithy.examples#DoubleList])"
    )
    newType: String
}
