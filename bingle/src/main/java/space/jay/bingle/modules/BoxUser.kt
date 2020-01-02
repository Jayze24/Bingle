package space.jay.bingle.modules

import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import space.jay.bingle.data.User
import space.jay.bingle.data.User_

class BoxUser(private val box: BoxStore) {

    private lateinit var mUser: User

    fun getData() = mUser

    fun setData(user: User) {
        mUser = box.boxFor<User>()
            .query { equal(User_.uid, user.uid) }
            .findFirst()
            ?: user
        if (mUser.id == 0L) {
            putData(user)
        }
    }

    private fun putData(user: User) {
        mUser.id = box.boxFor<User>().put(user)
    }
}