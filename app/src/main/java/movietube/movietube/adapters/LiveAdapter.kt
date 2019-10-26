package movietube.movietube.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.live_item.view.*
import movietube.movietube.R
import movietube.movietube.pojo.LiveTv
import java.util.*
import kotlin.random.*

class LiveAdapter(var context: Context, var list: MutableList<LiveTv>, var listener: ItemClickListener) :
    RecyclerView.Adapter<LiveAdapter.LV>() {

    interface ItemClickListener {
        fun onItemClick(view: View, liveTv: LiveTv)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LV {
        return LV(LayoutInflater.from(parent.context).inflate(R.layout.live_item, parent, false))
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(holder: LV, position: Int) {
        var liveTv = list[position]
        Glide.with(holder.itemView.context).load(liveTv.Poster).into(holder.itemView.live_thumb)
        holder.itemView.live_title.text = liveTv.Title
        holder.itemView.live_info.text = "Live • " + Random(2).nextInt(1000).toString() + " watching"
        holder.itemView.setOnClickListener {
            listener.onItemClick(holder.itemView, liveTv)
        }
    }

    fun addLives(newlist: MutableList<LiveTv>) {
        this.list.clear()
        this.list.addAll(newlist)
        this.notifyDataSetChanged()
    }

    class LV(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}