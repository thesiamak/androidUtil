package ir.drax.modal

import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import ir.drax.modal.model.MoButton

class ListAdapter(private var typeFace:Typeface ,private var medicines: List<MoButton>, val callback:(Int)->Unit) :
        RecyclerView.Adapter<ListAdapter.MedicineHolder>()  {

    override fun getItemCount() = medicines.size
    override fun onBindViewHolder(holder: MedicineHolder, position: Int) {
        val itemPhoto = medicines[position]
        holder.bindMedicine(itemPhoto,callback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineHolder {
        val textView=TextView(parent.context).apply {
            setTextColor(parent.context.resources.getColor(R.color.modal_ok_button))
            typeface=typeFace
            compoundDrawablePadding=8
            setPadding(32,16,32,16)
            layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        return MedicineHolder(textView)
    }

    class MedicineHolder(v: TextView) : RecyclerView.ViewHolder(v) {

        private var view: TextView = v

        fun bindMedicine(medicine: MoButton, callback: (Int) -> Unit) = with(view) {
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(this, 0, 0, medicine.iconResourceId, 0)
            text = medicine.displayText
            gravity = Gravity.CENTER_VERTICAL
            setOnClickListener {
                if (medicine.clickListener(this))
                    callback(adapterPosition)

            }
        }
    }

    fun setMedicines(newMedicine: List<MoButton>){
//        val diffResult=DiffUtil.calculateDiff(MedicineDiffCallback(this.medicines,newMedicine))
//        diffResult.dispatchUpdatesTo(this)
        this.medicines=newMedicine
        notifyDataSetChanged()
    }
}