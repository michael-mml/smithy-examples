$version: "1.0"

namespace smithy.examples.main

// import traits from traits namespace
use smithy.examples.traits#copy

/// Represents the response for numeric data
/// data - the data to be sent in the response
///
/// Example: { data: { double: 1 } }
structure NumericResponse {
    @required
    data: Number
}

/// Unions must be tagged, resulting in a extra layer of indirection
union Number {
    double: Double,
    integer: Integer,
    doubleList: DoubleList,
    integerList: IntegerList
}

list IntegerList {
    member: Integer
}

list DoubleList {
    member: Double
}

structure SomeStruct {
    @copy(
        newShapeName: "AnotherStruct"
    )
    originalField: String
}
