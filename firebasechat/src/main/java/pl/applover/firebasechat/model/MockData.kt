package pl.applover.firebasechat.model

/**
 * Created by sp0rk on 10/08/17.
 */
object MockData {
    val users = listOf(ChatUser("z"), ChatUser("x"))

    val messages = listOf(
            Message("1", users[0].uid, "${System.currentTimeMillis()}", "Test message"),
            Message("2", users[1].uid, "${System.currentTimeMillis()}", "Test response"))

    val channels = mutableListOf(
            Channel("a", "Test channel 1", users.map { it.uid }, messages),
            Channel("b", "Test channel 2", users.map { it.uid }, messages),
            Channel("c", "Test channel 3", users.map { it.uid }, messages),
            Channel("d", "Test channel 4", users.map { it.uid }, messages),
            Channel("e", "Test channel 5", users.map { it.uid }, messages),
            Channel("f", "Test channel 6", users.map { it.uid }, messages),
            Channel("g", "Test channel 7", users.map { it.uid }, messages)
    )
}