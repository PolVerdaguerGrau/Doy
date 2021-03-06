package com.napptilians.doy.view.selectduration

import android.view.ViewGroup
import com.napptilians.domain.models.service.DurationModel
import com.napptilians.doy.R
import com.napptilians.doy.base.BaseViewHolder
import com.napptilians.doy.extensions.gone
import com.napptilians.doy.extensions.visible
import kotlinx.android.synthetic.main.duration_item.view.*

class DurationListViewHolder(private val parent: ViewGroup) :
    BaseViewHolder<DurationModel>(parent, R.layout.duration_item) {

    override fun update(model: DurationModel) {
        itemView.durationText.text = model.numberOfHours.toString()
        if (model.numberOfHours == 1) {
            itemView.durationText.append(" " + parent.context.getString(R.string.hour))
        } else {
            itemView.durationText.append(" " + parent.context.getString(R.string.hours))
        }
        if (model.isSelected) {
            itemView.tickImageView.visible()
            itemView.durationText.alpha = 1f
        } else {
            itemView.tickImageView.gone()
            itemView.durationText.alpha = 0.5f
        }
    }
}
