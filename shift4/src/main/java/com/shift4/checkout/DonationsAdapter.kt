package com.shift4.checkout

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shift4.R
import com.shift4.data.model.pay.Donation

internal class DonationsAdapter(private val dataSet: Array<Donation>) :
    RecyclerView.Adapter<DonationsAdapter.ViewHolder>() {
    var onItemClick: ((Donation) -> Unit)? = null
    var current = 0

    class DonationItemDecoration(private val context: Context?) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val itemPosition = parent.getChildAdapterPosition(view)
            val totalCount = parent.adapter!!.itemCount
            if (itemPosition >= 0 && itemPosition < totalCount - 1) {
                outRect.right = context?.resources?.getDimensionPixelOffset(R.dimen.com_shift4_padding_standard) ?: 0
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.donationItemTextViewAmount)
        val layout: LinearLayout = view.findViewById(R.id.donationItemLinearLayout)

        init {
            onItemClick?.invoke(dataSet.first())
            view.setOnClickListener {
                current = adapterPosition
                onItemClick?.invoke(dataSet[adapterPosition])
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.com_shift4_donation_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position].readable
        if (position == current) {
            viewHolder.layout.setBackgroundResource(R.drawable.com_shift4_rounded_edge_primary)
        } else {
            viewHolder.layout.setBackgroundResource(R.drawable.com_shift4_rounded_edge)
        }
    }

    override fun getItemCount() = dataSet.size
}
