package com.localfootball.page.your_events

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.localfootball.R
import com.localfootball.model.PlayerEventRole
import com.localfootball.model.YourEventResponse
import com.localfootball.page.event_preview.EventPreviewActivity
import com.localfootball.service.AnimationService
import com.localfootball.service.VibrationService
import com.localfootball.util.setSafeOnClickListener

class YourEventsRecyclerViewAdapter(
    private val context: Context,
    private val yourEventsList: List<YourEventResponse>
) : RecyclerView.Adapter<YourEventsRecyclerViewHolder>() {

    private val vibrationService = VibrationService()
    private val animationService = AnimationService()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YourEventsRecyclerViewHolder {
        return YourEventsRecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_your_events_recycler_view_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return yourEventsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: YourEventsRecyclerViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.yourEventsListViewRowEventNameTextView).text =
            yourEventsList[position].event.name

        if(yourEventsList[position].playerRole == PlayerEventRole.ORGANIZER) {
            holder.view.findViewById<TextView>(R.id.textView7).text =
                context.getString(R.string.your_events_organizer)

            holder.view.findViewById<ConstraintLayout>(R.id.yourEventsRecyclerViewRowMainConstraintLayout)
                .background = context.getDrawable(R.drawable.your_events_row_as_organizer_shape)

            holder.view.findViewById<ImageView>(R.id.imageView3).setImageDrawable(context.getDrawable(R.drawable.ogranizer_green_black_icon))
        }
        if(yourEventsList[position].playerRole == PlayerEventRole.PARTICIPANT) {
            holder.view.findViewById<TextView>(R.id.textView7).text =
                context.getString(R.string.your_events_participant)

            holder.view.findViewById<ConstraintLayout>(R.id.yourEventsRecyclerViewRowMainConstraintLayout)
                .background = context.getDrawable(R.drawable.your_events_row_as_participant_shape)

            holder.view.findViewById<ImageView>(R.id.imageView3).setImageDrawable(context.getDrawable(R.drawable.green_ball))
        }

        holder.view.findViewById<TextView>(R.id.textView6).text = "${yourEventsList[position].event.participantsNumber} / ${yourEventsList[position].event.maxPlayers}"
        holder.view.findViewById<TextView>(R.id.textView8).text = yourEventsList[position].event.startAt.toLocalDate().toString()
        holder.view.findViewById<TextView>(R.id.textView9).text = yourEventsList[position].event.startAt.toLocalTime().toString()

        configureClickListener(holder.view)
    }

    private fun configureClickListener(view: View) {
        view.setSafeOnClickListener {
            vibrationService.clickVibration(context)
            val intent = Intent(context, EventPreviewActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }
    }

}

class YourEventsRecyclerViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class YourEventsRecyclerViewDecorator(
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