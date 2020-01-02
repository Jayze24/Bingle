package space.jay.bingle.data

import space.jay.bingle.Constants

data class LiveDataObject(val event: Int) {

    var message: String = Constants.Init.STRING
    var result: Int = Constants.Init.INT

    constructor(event: Int, result: Int) : this(event) {
        this.result = result
    }

    constructor(event: Int, message: String, result: Int) : this(event) {
        this.message = message
        this.result = result
    }
}