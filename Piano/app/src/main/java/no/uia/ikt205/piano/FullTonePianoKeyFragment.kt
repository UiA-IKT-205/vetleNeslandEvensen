package no.uia.ikt205.piano

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_full_tone_piano_key.view.*
import no.uia.ikt205.piano.databinding.FragmentFullTonePianoKeyBinding

class FullTonePianoKeyFragment : Fragment() {

    private var _binding:FragmentFullTonePianoKeyBinding? = null
    private val binding get() = _binding!!
    private lateinit var note:String

    var onKeyDown:((note:String) -> Unit)? = null
    var onKeyUp:((note:String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            note = it.getString("NOTE") ?: "?"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       _binding = FragmentFullTonePianoKeyBinding.inflate(inflater)
        val view = binding.root

        view.fullToneKey.setOnTouchListener(object : View.OnTouchListener{
            var startTime = 0
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when(event?.action){
                    MotionEvent.ACTION_DOWN -> this@FullTonePianoKeyFragment.onKeyDown?.invoke(note)
                    MotionEvent.ACTION_UP -> this@FullTonePianoKeyFragment.onKeyUp?.invoke(note)
                }
                return true
            }
        })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(note: String) =
            FullTonePianoKeyFragment().apply {
                arguments = Bundle().apply {
                    putString("NOTE", note)
                }
            }
    }
}