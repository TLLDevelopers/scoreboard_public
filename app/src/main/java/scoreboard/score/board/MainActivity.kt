package scoreboard.score.board

import android.content.*
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

var rewardedAd: RewardedAd? = null
var isShown = MutableLiveData<Boolean>()

class MainActivity : AppCompatActivity() {

    init {
        isShown.value = false
    }

    var adViewInterstitialMain: InterstitialAd? = null
    private val adRequest = AdRequest.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_ScoreBoard)

        loadInterstitial()

        MobileAds.initialize(this) {}

        isShown.observe(this) {
            RewardedAd.load(
                this,
                "ca-app-pub-2683992218327915/3314099172",
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                    }
                })
        }

        val prefs = getPreferences(Context.MODE_PRIVATE)
        val editor = prefs.edit();
        var totalCount = prefs.getInt("counter", 0)

        totalCount++;
        editor.putInt("counter", totalCount)
        if (!prefs.getBoolean("is_qualify", false)) {
            editor.putBoolean("is_qualify", false)
        }
        editor.apply()
        println("Total Application counter Reach to : $totalCount")

        if (totalCount % 15 == 0) {
            rateApp()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    var counter = 1
    var tiempoMilisegundos = 90000

    fun loadInterstitial() {
        InterstitialAd.load(this,
            "ca-app-pub-2683992218327915/7049916780",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    adViewInterstitialMain = interstitialAd
                }
            })

        when (counter) {
            1 -> counter += 1
            2 -> {
                tiempoMilisegundos += tiempoMilisegundos
                counter += 1
            }
            3 -> {
                tiempoMilisegundos += tiempoMilisegundos
                counter += 1
            }
            else -> counter += 1
        }
        var ticks = 0

        object : CountDownTimer(tiempoMilisegundos.toLong(), 1000) {
            override fun onFinish() {
                ticks = 0
                showInterstitialMain()
            }

            override fun onTick(millisUntilFinished: Long) {
                println(ticks)
                ticks+=1
            }
        }.start()

        Log.d("LOAD", "New Loaded")
    }

    fun showInterstitialMain() {
        adViewInterstitialMain?.show(this)
        loadInterstitial()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    val imm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                    v.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun rateApp() {
        val uri = Uri.parse("market://details?id=$packageName")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)

        val prefs = getPreferences(Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            editor.putBoolean("is_qualify", true)
            editor.apply()
            startActivity(myAppLinkToMarket)
        }

        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
            dialog.cancel()
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.qualify_title)
        builder.setMessage(R.string.qualify_text)
        builder.setPositiveButton(
            R.string.rate,
            DialogInterface.OnClickListener(function = positiveButtonClick)
        )
        builder.setNegativeButton(
            R.string.later,
            DialogInterface.OnClickListener(function = negativeButtonClick)
        )
        builder.setIcon(R.drawable.check)

        val dialog: AlertDialog = builder.create()
        dialog.setCancelable(false)

        if (!prefs.getBoolean("is_qualify", false)) {
            dialog.show()
        }
    }
}
