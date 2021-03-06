package com.napptilians.doy.view.chat.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.napptilians.commons.error.ErrorModel
import com.napptilians.domain.models.chat.ChatModel
import com.napptilians.doy.R
import com.napptilians.doy.base.BaseFragment
import com.napptilians.doy.view.customviews.DoyDialog
import com.napptilians.doy.view.customviews.DoyErrorDialog
import kotlinx.android.synthetic.main.chat_fragment.chatFragmentEditText
import kotlinx.android.synthetic.main.chat_fragment.chatFragmentHeaderTitle
import kotlinx.android.synthetic.main.chat_fragment.chatFragmentSendButton
import kotlinx.android.synthetic.main.chat_fragment.fireBaseChatMessages
import javax.inject.Inject


// TODO: Re do this view after MVP.
class ChatFragment : BaseFragment() {

    private val auth: FirebaseAuth? get() = FirebaseAuth.getInstance()
    private val user: FirebaseUser? get() = FirebaseAuth.getInstance().currentUser

    private var databaseReference: DatabaseReference? = null

    private val args: ChatFragmentArgs by navArgs()

    private lateinit var firebaseListener: ValueEventListener

    @Inject
    lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.chat_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: In progress dialog
        activity?.let { activity ->
            DoyDialog(activity).apply {
                setPopupIcon(R.drawable.ic_rocket)
                setPopupTitle(context.resources.getString(R.string.wip))
                setPopupSubtitle(context.resources.getString(R.string.chat_in_progress))
                show()
            }
        }

        chatFragmentHeaderTitle.text = args.serviceTitle

        val chatId = args.serviceId.toString()
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseListener = createFireBaseListener()
        databaseReference?.child(MESSAGE_TABLE_NAME)?.child(chatId)
            ?.addValueEventListener(firebaseListener)

        fireBaseChatMessages.adapter = adapter

        chatFragmentSendButton.setOnClickListener {
            if (chatFragmentEditText.text.isNotEmpty()) {
                sendData(chatFragmentEditText.text.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseReference?.removeEventListener(firebaseListener)
    }

    private fun sendData(text: String) {
        val senderName = args.senderName
        val chatId = args.serviceId.toString()

        val message = ChatModel(chatId, text, senderName, args.userId.toString())
        databaseReference?.child(MESSAGE_TABLE_NAME)?.child(chatId)?.push()?.setValue(message)
        chatFragmentEditText.setText("")
    }

    private fun refreshRecycler(messages: List<ChatModel>) {
        adapter.updateItems(messages)
        adapter.notifyDataSetChanged()
        fireBaseChatMessages?.scrollToPosition(messages.size - 1)
    }

    override fun onError(error: ErrorModel) {
        activity?.let { DoyErrorDialog(it).show() }
    }

    override fun onLoading() {
    }

    private fun createFireBaseListener() = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val toReturn: MutableList<ChatModel> = mutableListOf()
            for (data in dataSnapshot.children) {
                val messageData = data.getValue<ChatModel>(ChatModel::class.java)
                val message = messageData?.let { it } ?: continue

                toReturn.add(message)
            }
            toReturn.sortBy { message -> message.timeStamp }

            refreshRecycler(toReturn.toList())
        }

        override fun onCancelled(p0: DatabaseError) {
        }
    }

    companion object {
        private const val MESSAGE_TABLE_NAME = "messages"
    }

}
