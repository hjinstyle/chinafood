package naru.narucof.chinafood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.android.gms.ads.*
import naru.narucof.chinafood.databinding.ActivityMainBinding
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class MainActivity : AppCompatActivity() {
    
    var webView: WebView? = null
    private var backBtnTime: Long = 0
    lateinit var mAdView : AdView
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //애드몹 sdk초기화
        MobileAds.initialize(this) {}
        //애드몹 배너 광고 load
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        //애드몹 전면광고 넣기     테스트아이디 : ca-app-pub-3940256099942544/1033173712, 내아이디 : ca-app-pub-5915723186214751/6564949322
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        //애드몹 전면광고 콜백함수
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad was dismissed.")
            }
            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d(TAG, "Ad failed to show.")
            }
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
                mInterstitialAd = null
            }
        }




        webView= findViewById(R.id.webView)
        //자바스크립트 허용
        webView?.settings?.javaScriptEnabled = true 
        //웹뷰에서 새 창(다른브라우저)이 뜨지 않도록 방지하는 구문
        webView?.webViewClient = WebViewClient()
        webView?.webChromeClient = WebChromeClient()
        //url호출
        webView?.loadUrl("http://chinafood.netlify.app")
    }
    
    //뒤로가기버튼 : 한번누르면 전 화면, 두번눌르면 종료
    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        val gapTime: Long = curTime - backBtnTime

        if (webView?.canGoBack()!!) {
            webView?.goBack() //웹사이트 뒤로가기
            //뒤로가기 시 애드몹 전면 광고 노출
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this@MainActivity)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
        }else if (0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed(); //본래의 exit (안드로이드버전)
        } else {
            backBtnTime = curTime;
            Toast.makeText(this, "뒤로가기 두번 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

}