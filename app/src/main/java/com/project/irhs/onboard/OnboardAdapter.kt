import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.irhs.R
import com.project.irhs.onboard.PagerData

class OnboardAdapter(
    private val items: List<PagerData>
) : RecyclerView.Adapter<OnboardAdapter.ViewPagerViewHolder>() {

    class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val heading: TextView = itemView.findViewById(R.id.rainStatusTv)
        val description: TextView = itemView.findViewById(R.id.description)
        val imageView: ImageView = itemView.findViewById(R.id.rain_light)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.onboard_items_lyt, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val item = items[position]
        holder.heading.text = item.heading
        holder.description.text = item.description
        holder.imageView.setImageResource(item.imageResId)  // Set the image for each page
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
