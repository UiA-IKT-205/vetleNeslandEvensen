package no.uia.ikt205.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import no.uia.ikt205.pomodoro.util.millisecondsToDescriptiveTime
import no.uia.ikt205.pomodoro.util.minutesToMilliseconds

class MainActivity : AppCompatActivity() {

    lateinit var timer:CountDownTimer
    lateinit var pauseTimer:CountDownTimer
    lateinit var startButton:Button
    lateinit var countdownDisplay:TextView
    lateinit var countDownDisplayPause:TextView
    lateinit var setTimeDurationSeekBar:SeekBar
    lateinit var setPauseDurationSeekBar:SeekBar
    lateinit var setQuantityOfRepetitons: EditText

    var timeToCountDownInMs = 5000L
    var timeTicks = 1000L
    var timeToCountDownInMinutes = 15L
    var QuantityOfRepetitons = 0
    var workCountDownRun = false
    var pauseCountDownRun = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setQuantityOfRepetitons = findViewById(R.id.editText)

        // Start button
        startButton = findViewById<Button>(R.id.startCountdownButton)
        startButton.setOnClickListener(){
            if (!workCountDownRun && !pauseCountDownRun){
                QuantityOfRepetitons = setQuantityOfRepetitons.text.toString().toInt()
                startCountDown(it)
                return@setOnClickListener}
        }

        countdownDisplay = findViewById<TextView>(R.id.countDownView)
        countDownDisplayPause = findViewById<TextView>(R.id.countDownViewPause)

        // Set time duration for work.
        setTimeDurationSeekBar = findViewById<SeekBar>(R.id.seekBar)
        setTimeDurationSeekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                    timeToCountDownInMs = minutesToMilliseconds(progress.toLong())
                    timeToCountDownInMinutes = progress.toLong()
                    updateCountDownDisplay(timeToCountDownInMs)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                Toast.makeText(this@MainActivity,
                        "Angitt minutter for arbeidstid er: " + seek.progress + " min",
                        Toast.LENGTH_SHORT).show()
            }
        })

        // Set time duration for pause.
        setPauseDurationSeekBar = findViewById<SeekBar>(R.id.seekBarPause)
        setPauseDurationSeekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                timeToCountDownInMs = minutesToMilliseconds(progress.toLong())
                timeToCountDownInMinutes = progress.toLong()
                updateCountDownDisplayPause(timeToCountDownInMs)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                Toast.makeText(this@MainActivity,
                        "Angitt minutter for pause er: " + seek.progress + " min",
                        Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function which starts the countdown for work time
    fun startCountDown(v: View){

        timer = object : CountDownTimer(timeToCountDownInMs,timeTicks) {
            override fun onFinish() {
                v.isEnabled = true
                Toast.makeText(this@MainActivity,"Arbeidsøkt er ferdig", Toast.LENGTH_SHORT).show()
                if (!pauseCountDownRun && QuantityOfRepetitons > 0){
                    startCountDownPause(v)
                }
                QuantityOfRepetitons --
                workCountDownRun = false
            }

            override fun onTick(millisUntilFinished: Long) {
                updateCountDownDisplay(millisUntilFinished)
            }
        }
        v.isEnabled = false
        timer.start()
    }

    // Update countdown display for work time
    fun updateCountDownDisplay(timeInMs:Long){
        countdownDisplay.text = millisecondsToDescriptiveTime(timeInMs)
    }

    // Function which starts the countdown for pause time
    fun startCountDownPause(v: View){

        pauseTimer = object : CountDownTimer(timeToCountDownInMs,timeTicks) {
            override fun onFinish() {
                v.isEnabled = true
                Toast.makeText(this@MainActivity,"Pausen er ferdig", Toast.LENGTH_SHORT).show()
                if (!workCountDownRun && QuantityOfRepetitons > 0){
                    startCountDown(v)
                }
               pauseCountDownRun = false
            }

            override fun onTick(millisUntilFinished: Long) {
                updateCountDownDisplayPause(millisUntilFinished)
            }
        }
        v.isEnabled = false
        pauseTimer.start()
    }

    // Update countdown display for pause time
    fun updateCountDownDisplayPause(timeInMs:Long){
        countDownDisplayPause.text = millisecondsToDescriptiveTime(timeInMs)
    }

}


