package no.uia.ikt205.piano

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.fragment_piano.view.*
import no.uia.ikt205.piano.data.Note
import no.uia.ikt205.piano.databinding.FragmentPianoBinding
import java.io.File
import java.io.FileOutputStream

class PianoLayout : Fragment() {

    var onSave:((file: Uri) -> Unit)? = null

    private var _binding:FragmentPianoBinding? = null
    private val binding get() = _binding!!

    private val fullTones = listOf("C", "D", "E", "F", "G", "A", "B","C2", "D2", "E2", "F2", "G2")
    private val semiTones = listOf("C#", "D#", "F#", "G#", "A#", "C#2", "D#2", "F#2", "G#2", "A#2")

    private var score:MutableList<Note> = mutableListOf<Note>() // Score == Noteark?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPianoBinding.inflate(layoutInflater)
        val view = binding.root

        val fm = childFragmentManager
        val ft = fm.beginTransaction()

        fullTones.forEach { orgNoteValue ->
            val fullTonePianoKey = FullTonePianoKeyFragment.newInstance(orgNoteValue)
            var startPlay:Long = 0

            fullTonePianoKey.onKeyDown = { note ->
                startPlay = System.nanoTime()
                println("Piano key down $note")
            }

            fullTonePianoKey.onKeyUp = {
                var endPlay = System.nanoTime()
                val note = Note(it, startPlay, endPlay)
                score.add(note)
                println("Piano key up $it")
            }

            ft.add(view.pianoKeys.id,fullTonePianoKey, "note $orgNoteValue")
        }

        semiTones.forEach { orgNoteValue ->
            val semiTonePianoKey = SemiTonePianoKeyFragment.newInstance(orgNoteValue)

            semiTonePianoKey.onKeyDown = {
                println("Black piano key down $it")
            }

            semiTonePianoKey.onKeyUp = {
                println("Black piano key up $it")
            }

            ft.add(view.pianoKeys.id,semiTonePianoKey, "note $orgNoteValue")
        }

        ft.commit()

       view.saveScoreBt.setOnClickListener {
           var fileName = view.fileNameTextEdit.text.toString()
           if (score.count() > 0 && fileName.isNotEmpty()){
               fileName = "$fileName.music"
               var content:String = score.map{ it.toString()}.reduce { acc, s -> acc + s + "\n"}
               saveFile(fileName,content)
           } else {
               /// TODO: No music or missing file name
           }
       }
        return view
    }

    private fun saveFile(fileName:String,content:String){
        val path = this.activity?.getExternalFilesDir(null)
        if (path != null){
            val file = File(path,fileName)
            FileOutputStream(file,true).bufferedWriter().use { writer ->
                writer.write(content)
            }

            this.onSave?.invoke(file.toUri());
        } else {
            /// Could not get external path. Warn user?
        }
    }

}