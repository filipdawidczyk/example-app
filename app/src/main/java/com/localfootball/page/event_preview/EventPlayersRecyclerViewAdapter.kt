package com.localfootball.page.event_preview

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.localfootball.R
import com.localfootball.model.Gender
import com.localfootball.model.Gender.FEMALE
import com.localfootball.model.Gender.MALE
import com.localfootball.model.Participation
import com.localfootball.model.PlayerEventRole
import de.hdodenhof.circleimageview.CircleImageView

class EventPlayersRecyclerViewAdapter(
    private val context: Context,
    private val participations: List<Participation>
) :
    RecyclerView.Adapter<EventPlayersViewHandler>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventPlayersViewHandler {
        return EventPlayersViewHandler(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_event_player_recycler_view_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return participations.size
    }

    override fun onBindViewHolder(holder: EventPlayersViewHandler, position: Int) {
        configureEventRoleTextView(participations[position].playerRole, holder.view)
        configureAvatarImageView(participations[position].player.gender, holder.view)
        holder.view.findViewById<TextView>(R.id.textView5).text =
            participations[position].player.nickname
    }

    private fun configureEventRoleTextView(playerEventRole: PlayerEventRole, view: View) {
        if (playerEventRole == PlayerEventRole.ORGANIZER) {
            view.findViewById<TextView>(R.id.textView16).text =
                context.getString(R.string.event_preview_organizer)
        }
        if (playerEventRole == PlayerEventRole.PARTICIPANT) {
            view.findViewById<TextView>(R.id.textView16).text =
                context.getString(R.string.event_preview_participant)
        }
    }

    private fun configureAvatarImageView(gender: Gender, view: View) {
        view.findViewById<CircleImageView>(R.id.profile_image).alpha = 0.2f
        when (gender) {
            FEMALE -> {
                view.findViewById<CircleImageView>(R.id.profile_image)
                    .setImageResource(R.drawable.female_default_user_icon)
            }
            MALE -> {
                view.findViewById<CircleImageView>(R.id.profile_image)
                    .setImageResource(R.drawable.male_default_user_icon)
            }
            else -> {
                view.findViewById<CircleImageView>(R.id.profile_image)
                    .setImageResource(R.drawable.not_specified_gender_icon)
            }
        }
    }
}

class EventPlayersViewHandler(val view: View) : RecyclerView.ViewHolder(view)

class EventPlayersDecorator(
    private val horizontalSpacing: Int,
    private val verticalSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = horizontalSpacing
        outRect.right = horizontalSpacing
        outRect.top = verticalSpacing
        outRect.bottom = verticalSpacing
    }
}