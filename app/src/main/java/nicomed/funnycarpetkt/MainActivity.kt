package nicomed.funnycarpetkt

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.SparseIntArray
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import nicomed.funnycarpetkt.R.id.*
import java.io.IOException
import android.R.attr.name
import android.view.WindowManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    lateinit var dir: TextView
    lateinit var body: TextView
    lateinit var image: ImageView
    lateinit var imageBack: ImageView
    lateinit var btn: Button
    lateinit var swStart: Switch
    lateinit var swSnd: Switch
    lateinit var txtScds: TextView
    lateinit var mainHandler: Handler
    var isAuto: Boolean = false
    var seconds: Long = 30

    lateinit var soundPool: SoundPool
    lateinit var soundPoolMap: SparseIntArray
    var soundIdRight = 0
    var soundIdLeft = 0
    var soundIdHand = 0
    var soundIdLeg = 0
    var soundIdBlue = 0
    var soundIdRed = 0
    var soundIdGreen = 0
    var soundIdYellow = 0
    var soundIdEmpty = 0

    private fun initSound() {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()
        try {
            soundIdRight = soundPool.load(this, R.raw.rigth, 1)
            soundIdLeft = soundPool.load(this, R.raw.left, 1)
            soundIdHand = soundPool.load(this, R.raw.hand, 1)
            soundIdLeg = soundPool.load(this, R.raw.leg, 1)
            soundIdBlue = soundPool.load(this, R.raw.blue, 1)
            soundIdRed = soundPool.load(this, R.raw.red, 1)
            soundIdGreen = soundPool.load(this, R.raw.green, 1)
            soundIdYellow = soundPool.load(this, R.raw.yellow, 1)
            soundIdEmpty = soundPool.load(this, R.raw.empty, 1)
        } catch (e: IOException) {
            e.printStackTrace();
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findComponetsById()
        initTimer()
        initSound()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun initTimer() {
        mainHandler = Handler(Looper.getMainLooper())
        swStart.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                seconds = getSecondsFromUI()
                isAuto = true
                checkIsAuto()
                mainHandler.post(updateTextTask)
                roll()
            } else {
                isAuto = false
                checkIsAuto()
                mainHandler.removeCallbacks(updateTextTask)
            }
        }
    }

    private fun getSecondsFromUI(): Long {
        var sec: Long
        val s: String = txtScds.text.toString()
        try {
            sec = s.toLong()
        } catch (e: Exception) {
            sec = 1
            txtScds.text = sec.toString()
        }
        return sec
    }

    private fun findComponetsById() {
        dir = findViewById(txtDir)
        body = findViewById(txtBody)
        image = findViewById(im1)
        imageBack = findViewById(imBack)
        btn = findViewById(btnStart)
        swStart = findViewById(swAutoStart)
        swSnd = findViewById(swSound)
        txtScds = findViewById(txtSeconds)
    }

    private val updateTextTask = object : Runnable {
        override fun run() {
            roll()
            mainHandler.postDelayed(this, seconds * 1000)
        }
    }

    fun checkIsAuto() {
        btn.isEnabled = !isAuto
        txtScds.isEnabled = !isAuto
    }

    fun onClick(v: View) {
        if (!isAuto) {
            roll()
        }
    }

    fun roll() {
        val c: CarpetColor = CarpetColor.values().random()
        val b: Int = (1..2).random()
        val d: Int = (1..2).random()
        runBlocking {
            launch {
                body(b)
                setDirection(d)
                setImage(b)
                setColor(c.value)
            }
        }
        if (swSnd.isChecked) {
            runBlocking {
                launch {
                    (playSound(d, b, c))
                }
            }
        }
    }

    fun playSound(s1: Int, s2: Int, s3: CarpetColor) {
        var idS1: Int
        when (s1) {
            1 -> idS1 = soundIdRight
            2 -> idS1 = soundIdLeft
            else -> idS1 = soundIdEmpty
        }
        soundPool.play(idS1, 1f, 1f, 0, 0, 1f)
        try {
            Thread.sleep(780) // play twice
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        var idS2: Int
        when (s2) {
            1 -> idS2 = soundIdHand
            2 -> idS2 = soundIdLeg
            else -> idS2 = soundIdEmpty
        }
        soundPool.play(idS2, 1f, 1f, 0, 0, 1f)
        try {
            Thread.sleep(750) // play twice
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        var idS3: Int
        when (s3) {
            CarpetColor.Blue -> idS3 = soundIdBlue
            CarpetColor.Red -> idS3 = soundIdRed
            CarpetColor.Green -> idS3 = soundIdGreen
            CarpetColor.Yellow -> idS3 = soundIdYellow
        }
        soundPool.play(idS3, 1f, 1f, 0, 0, 1f)
    }

    fun body(b: Int) {
        when (b) {
            1 -> body.text = "рука"
            2 -> body.text = "нога"
            else -> body.text = ""
        }
    }

    fun setDirection(d: Int) {
        when (d) {
            1 -> dir.text = "Правая"
            2 -> dir.text = "Левая"
            else -> dir.text = ""
        }
    }

    fun setImage(b: Int) {
        when (b) {
            1 -> image.setImageResource(R.drawable.ic_hand_left)
            2 -> image.setImageResource(R.drawable.ic_leg)
            else -> image.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    fun setColor(color: Int) {
        image.setBackgroundColor(color)
        imageBack.setBackgroundColor(color)
    }

}