package pl.applover.firebasechat.ui

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
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

abstract class FirebaseRecyclerAdapter<IH : ViewHolder, HH : ViewHolder, T>
@JvmOverloads constructor(
        private val mQuery: Query,
        private val modelClass: Class<T>,
        private val itemHolderClass: Class<IH>,
        private val headerHolderClass: Class<HH>,
        private val itemLayout: Int,
        private val headerLayout: Int,
        private val headerDecider: HeaderDecider<T>,
        items: ArrayList<T?>? = null,
        keys: ArrayList<String?>? = null)
    : RecyclerView.Adapter<ViewHolder>() {

    var items: ArrayList<T?>? = null
        private set

    var keys: ArrayList<String?>? = null
        private set

    init {
        if (items != null && keys != null) {
            this.items = items
            this.keys = keys
        } else {
            this.items = ArrayList<T?>()
            this.keys = ArrayList<String?>()
        }
        initListener()
        mQuery.addChildEventListener(mListener)
    }

    private lateinit var mListener: ChildEventListener

    private fun initListener() {
        mListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val key = dataSnapshot.key

                if (!keys!!.contains(key)) {
                    val item = getConvertedObject(dataSnapshot)
                    val insertedPosition: Int
                    if (previousChildName == null) {
                        items!!.add(0, item)
                        keys!!.add(0, key)
                        insertedPosition = 0
                    } else {

                        val previousIndex = keys!!.indexOf(previousChildName)
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
                            nextIndex++
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
                    itemAdded(item, key, insertedPosition)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {
                val key = dataSnapshot.key

                if (keys!!.contains(key)) {
                    val index = keys!!.indexOf(key)
                    val oldItem = items!![index]
                    val newItem = getConvertedObject(dataSnapshot)

                    items!![index] = newItem

                    notifyItemChanged(index)
                    itemChanged(oldItem, newItem, key, index)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val key = dataSnapshot.key

                if (keys!!.contains(key)) {
                    val index = keys!!.indexOf(key)
                    val item = items!![index]

                    keys!!.removeAt(index)
                    items!!.removeAt(index)

                    notifyItemRemoved(index)
                    itemRemoved(item, key, index)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val key = dataSnapshot.key

                val index = keys!!.indexOf(key)
                val item = getConvertedObject(dataSnapshot)
                items!!.removeAt(index)
                keys!!.removeAt(index)
                val newPosition: Int
                if (previousChildName == null) {
                    items!!.add(0, item)
                    keys!!.add(0, key)
                    newPosition = 0
                } else {
                    val previousIndex = keys!!.indexOf(previousChildName)
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
                itemMoved(item, key, index, newPosition)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur.")
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
            val next = if (position < items!!.size) items!![position + 1] else null
            if (getItemViewType(position) == HEADER)
                populateHeader(holder as HH, previous, next, position)
            else
                populateItem(holder as IH, previous, items!![position]!!, next, position)
        }
    }

    abstract fun populateItem(holder: IH, previous: T?, model: T, next: T?, position: Int)

    abstract fun populateHeader(holder: HH, previous: T?, next: T?, position: Int)

    override fun getItemCount(): Int = if (items != null) items!!.size else 0

    fun destroy() {
        mQuery.removeEventListener(mListener)
    }

    fun getItem(position: Int): T? {
        return items!![position]
    }

    fun getPositionForItem(item: T): Int {
        return if (items != null && items!!.size > 0) items!!.indexOf(item) else -1
    }


    operator fun contains(item: T): Boolean {
        return items != null && items!!.contains(item)
    }

    protected fun itemAdded(item: T?, key: String, position: Int) {}
    protected fun itemChanged(oldItem: T?, newItem: T?, key: String, position: Int) {}
    protected fun itemRemoved(item: T?, key: String, position: Int) {}
    protected fun itemMoved(item: T?, key: String, oldPosition: Int, newPosition: Int) {}

    protected fun getConvertedObject(snapshot: DataSnapshot): T {
        return snapshot.getValue(modelClass)!!
    }

    companion object {
        private val HEADER = 0
        private val MODEL = 1
    }

    interface HeaderDecider<T> {
        fun getHeader(previous: T?, next: T?): String?
    }
}