package pl.applover.firebasechat.ui

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query

/**
 * Created by sp0rk on 17/08/17.
 */

abstract class HeaderedFirebaseAdapter<in IH : ViewHolder, in HH : ViewHolder, M>
@JvmOverloads constructor(
        private val query: Query,
        private val modelClass: Class<M>,
        private val itemHolderClass: Class<IH>,
        private val headerHolderClass: Class<HH>,
        private val itemLayout: Int,
        private val headerLayout: Int,
        private val headerDecider: HeaderDecider<M>,
        items: ArrayList<M?>? = null,
        keys: ArrayList<String?>? = null)
    : RecyclerView.Adapter<ViewHolder>() {

    var items: ArrayList<M?>? = null
        private set

    var keys: ArrayList<String?>? = null
        private set

    init {
        if (items != null && keys != null) {
            this.items = items
            this.keys = keys
        } else {
            this.items = ArrayList<M?>()
            this.keys = ArrayList<String?>()
        }
        initListener()
        query.addChildEventListener(mListener)
    }

    private lateinit var mListener: ChildEventListener

    private fun initListener() {
        mListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousKey: String?) {
                val key = dataSnapshot.key

                if (!keys!!.contains(key)) {
                    val item = castToModel(dataSnapshot)
                    val insertedPosition: Int
                    if (previousKey == null) {
                        items!!.add(0, item)
                        keys!!.add(0, key)
                        insertedPosition = 0
                    } else {
                        val previousIndex = keys!!.indexOf(previousKey)
                        val previousItem = items!![previousIndex]
                        var nextIndex = previousIndex + 1
                        val header = headerDecider.getHeader(previousItem, item)
                        if (header != null) {
                            if (nextIndex == items!!.size) {
                                items!!.add(null)
                                keys!!.add(null)
                            } else {
                                items!!.add(nextIndex, null)
                                keys!!.add(nextIndex, null)
                            }
                            notifyItemInserted(nextIndex)
                            onItemAdded(item, key, nextIndex++)
                        }
                        if (nextIndex == items!!.size) {
                            items!!.add(item)
                            keys!!.add(key)
                        } else {
                            items!!.add(nextIndex, item)
                            keys!!.add(nextIndex, key)
                        }
                        insertedPosition = nextIndex
                    }
                    notifyItemInserted(insertedPosition)
                    onItemAdded(item, key, insertedPosition)
                }
            }

            override fun onChildChanged(snap: DataSnapshot, previousKey: String) {
                val key = snap.key
                if (keys!!.contains(key)) {
                    val index = keys!!.indexOf(key)
                    val oldItem = items!![index]
                    val newItem = castToModel(snap)
                    items!![index] = newItem
                    notifyItemChanged(index)
                    onItemChanged(oldItem, newItem, key, index)
                }
            }

            override fun onChildRemoved(snap: DataSnapshot) {
                val key = snap.key
                if (keys!!.contains(key)) {
                    val index = keys!!.indexOf(key)
                    val item = items!![index]
                    keys!!.removeAt(index)
                    items!!.removeAt(index)
                    notifyItemRemoved(index)
                    onItemRemoved(item, key, index)
                }
            }

            override fun onChildMoved(snap: DataSnapshot, previousKey: String?) {
                val key = snap.key
                val index = keys!!.indexOf(key)
                val item = castToModel(snap)
                items!!.removeAt(index)
                keys!!.removeAt(index)
                val newPosition: Int
                if (previousKey == null) {
                    items!!.add(0, item)
                    keys!!.add(0, key)
                    newPosition = 0
                } else {
                    val previousIndex = keys!!.indexOf(previousKey)
                    val nextIndex = previousIndex + 1
                    if (nextIndex == items!!.size) {
                        items!!.add(item)
                        keys!!.add(key)
                    } else {
                        items!!.add(nextIndex, item)
                        keys!!.add(nextIndex, key)
                    }
                    newPosition = nextIndex
                }
                notifyItemMoved(index, newPosition)
                onItemMoved(item, key, index, newPosition)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onFirebaseCancelled(databaseError)
            }

        }
    }

    override fun getItemViewType(position: Int) = if (keys?.get(position) == null) HEADER else MODEL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = when (viewType) {
        MODEL -> itemHolderClass.getConstructor(View::class.java).newInstance(LayoutInflater.from(parent.context).inflate(itemLayout, parent, false))
        HEADER -> headerHolderClass.getConstructor(View::class.java).newInstance(LayoutInflater.from(parent.context).inflate(headerLayout, parent, false))
        else -> throw IllegalStateException()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items?.let {
            val previous = if (position > 0 && position < items!!.size) items!![position - 1] else null
            val next = if (position < items!!.size-1) items!![position + 1] else null
            if (getItemViewType(position) == HEADER)
                populateHeader(holder as HH, previous, next, position)
            else
                populateItem(holder as IH, previous, items!![position]!!, next, position)
        }
    }

    abstract fun populateItem(holder: IH, previous: M?, model: M, next: M?, position: Int)

    abstract fun populateHeader(holder: HH, previous: M?, next: M?, position: Int)

    override fun getItemCount(): Int = if (items != null) items!!.size else 0

    fun destroy() {
        query.removeEventListener(mListener)
    }

    fun getItem(position: Int): M? {
        return items?.get(position)
    }

    fun getIndexOf(item: M, valIfAbsent: Int = -1): Int {
        return if (items?.isNotEmpty()?:false) items!!.indexOf(item) else valIfAbsent
    }

    operator fun contains(item: M): Boolean {
        return items != null && item in items!!
    }

    protected fun onItemAdded(item: M?, key: String, position: Int) {}
    protected fun onItemChanged(oldItem: M?, newItem: M?, key: String, position: Int) {}
    protected fun onItemRemoved(item: M?, key: String, position: Int) {}
    protected fun onItemMoved(item: M?, key: String, oldPosition: Int, newPosition: Int) {}
    protected fun onFirebaseCancelled(databaseError: DatabaseError) {}

    protected fun castToModel(snap: DataSnapshot): M {
        return snap.getValue(modelClass)!!
    }

    companion object {
        private val HEADER = 0
        private val MODEL = 1
    }

    interface HeaderDecider<in T> {
        fun getHeader(previous: T?, next: T?): String?
    }
}