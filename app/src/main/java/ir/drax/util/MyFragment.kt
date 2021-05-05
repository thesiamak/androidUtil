package ir.drax.util

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

class MyFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



//        Permissioner.bind(this)
//        MyFragmentWithPermission.showSnackBar(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_my, container, false)
    }

//    @WithPermission(Manifest.permission.INTERNET,grantPermission = false)
    @Suppress("s")
     fun showSnackBar(name:Boolean):String {
        Toast.makeText(requireContext(), "Raised", Toast.LENGTH_LONG).show()
        return "true"
    }
}