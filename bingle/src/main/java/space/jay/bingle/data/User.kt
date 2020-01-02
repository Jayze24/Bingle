package space.jay.bingle.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index

@Entity
data class User(
    @Id var id: Long = 0,
    @Index var uid: String = "", //인증업체 uid
    var name: String = "",
    var email: String = ""
)