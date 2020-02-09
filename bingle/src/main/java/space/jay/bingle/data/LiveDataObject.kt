package space.jay.bingle.data

import android.os.Bundle
import space.jay.bingle.Constants

data class LiveDataObject(val event: Int) {

    var message: String = Constants.Init.STRING
    var result: Int = Constants.Init.INT
    var bundle: Bundle? = null

    constructor(event: Int, result: Int) : this(event) {
        this.result = result
    }

    constructor(event: Int, message: String, result: Int) : this(event) {
        this.message = message
        this.result = result
    }

    constructor(event: Int, bundle: Bundle) : this(event) {
        this.bundle = bundle
    }
}