package com.khumakers.studystacker.ui.gallery

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.khumakers.studystacker.ListviewAdapter
import com.khumakers.studystacker.MainActivity
import com.khumakers.studystacker.R
import com.khumakers.studystacker.databinding.FragmentGalleryBinding
import com.khumakers.studystacker.toTimestamp
import java.util.Timer
import kotlin.concurrent.timer

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private var timerTask : Timer? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
                ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val houredit: EditText = binding.hourSetting
        val minuteedit: EditText = binding.minuteSetting

        val adapt = ListviewAdapter()

        houredit.setText((adapt.getAimTime()/60).toString())
        minuteedit.setText((adapt.getAimTime()%60).toString())

        timerTask = timer(period = 300) {
            activity?.runOnUiThread{
                MainActivity.mainActivity?.galleryStart()
                timerTask?.cancel()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}