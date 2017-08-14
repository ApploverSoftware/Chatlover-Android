package pl.applover.firebasechatsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import pl.applover.firebasechat.model.Channel
import pl.applover.firebasechat.model.ChatUser
import pl.applover.firebasechat.model.Message
import pl.applover.firebasechat.ui.channel_list.ChannelListFragment
import pl.applover.firebasechat.ui.channel_list.ChannelListFragment.ChannelListListener
import pl.applover.firebasechat.ui.chat.ChatFragment
import pl.applover.firebasechat.ui.chat.ChatFragment.ChatListener

class MainActivity : AppCompatActivity(), ChannelListListener, ChatListener {
    override fun onChatRequested(channelId: String) {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.container, ChatFragment.newInstance(channelId, "-KrVgOzX-qdp7QwMDnrn").withListener(this))
                .addToBackStack("asdads")
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
//        addChannel()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
    }

    fun init() {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.container, ChannelListFragment.newInstance().withListener(this))
                .commit()
    }

    private fun addChannel() {
        val db = FirebaseDatabase.getInstance().reference
        val u1 = with(db.child("chat_users").push()) {
            ref.setValue(ChatUser(key, "Julian"))
            key
        }
        val u2 = with(db.child("chat_users").push()) {
            ref.setValue(ChatUser(key, "Janusz"))
            key
        }
        val c1 = with(db.child("channels").push()) {
            ref.setValue(Channel(key, "Bakłażany", listOf(u1,u2), HashMap<String, Message>()))
            key
        }
        with(db.child("channels").child(c1).child("messages").push()) {
            ref.setValue(Message(key, u2, System.currentTimeMillis(), "Zero"))
            key
        }
        with(db.child("channels").child(c1).child("messages").push()) {
            ref.setValue(Message(key, u2, System.currentTimeMillis(), "Spicy jalapeno bacon ipsum dolor amet shank meatloaf ham andouille. Cow jowl bacon brisket tri-tip frankfurter beef ribs. Jowl jerky cupim salami chuck tenderloin bacon, venison pancetta corned beef drumstick shankle. Picanha jowl shank bacon ham doner. Meatloaf sausage tri-tip, beef turkey pastrami jerky pig salami prosciutto. Tail meatball biltong bresaola."))
            key
        }
        with(db.child("channels").child(c1).child("messages").push()) {
            ref.setValue(Message(key, u1, System.currentTimeMillis(), ":eggplant:"))
            key
        }
        with(db.child("channels").child(c1).child("messages").push()) {
            ref.setValue(Message(key, u2, System.currentTimeMillis(), "Sausage chicken venison, hamburger boudin burgdoggen doner short ribs capicola meatloaf drumstick beef ribs fatback pancetta. Turkey pork chop frankfurter, jowl t-bone strip steak venison. Capicola brisket drumstick meatball bacon tongue doner. Bresaola brisket kevin hamburger kielbasa tenderloin prosciutto turkey chuck biltong filet mignon shank ball tip meatloaf. Drumstick meatball prosciutto chicken, corned beef ball tip hamburger pork loin bacon. Pastrami flank hamburger jowl bacon, kevin corned beef biltong pancetta tongue ribeye pig fatback. Corned beef bacon turkey salami chicken short ribs.\n" +
                    "\n" +
                    "Alcatra biltong spare ribs turkey. Tongue turkey doner cupim ball tip kevin rump. Boudin landjaeger tail corned beef. Tri-tip short ribs flank pancetta drumstick ham turkey t-bone tail. Brisket rump biltong t-bone, bresaola pork loin jerky bacon. Turducken leberkas turkey tongue fatback, ham hock pancetta pork chop."))
            key
        }
    }

}
