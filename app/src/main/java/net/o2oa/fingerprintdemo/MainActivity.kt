package net.o2oa.fingerprintdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import android.util.Log
import android.hardware.fingerprint.FingerprintManager
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private val tag = "MainActivity"
    private var mBiometricPrompt: BiometricPrompt? = null
    private var mFingerprintManager: FingerprintManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            initBio()
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initFingerprintManager()
        }
        btn_click.setOnClickListener {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                    Log.i(tag, "开始28的验证。。。。。。。。。。。。。。。。")
                    authenticate28()
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    Log.i(tag, "开始23的验证。。。。。。。。。。。。。。。。")
                    authenticate23()
                }
                else -> Toast.makeText(this, "系统不支持", Toast.LENGTH_SHORT).show()
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initBio() {
        mBiometricPrompt = BiometricPrompt
            .Builder(this)
            .setTitle("指纹识别Demo")
            .setSubtitle("这里是subTitle")
            .setDescription("这里是描述。。。。。。。")
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initFingerprintManager() {
        mFingerprintManager = getSystemService(FingerprintManager::class.java)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun authenticate23() {
        val cancel = CancellationSignal()
        val mFingerprintDialog = FingerprintDialog()
        mFingerprintDialog.setOnBiometricPromptDialogActionCallback(object : FingerprintDialog.OnFingerprintDialogActionCallback{
            override fun onDialogDismiss() {
                Log.i(tag, "mFingerprintDialog。。。。 dialog dismiss。")
                if (!cancel.isCanceled) {
                    cancel.cancel()
                }
            }

            override fun onCancel() {
                Log.i(tag, "mFingerprintDialog。。。。 cancel。")
                if (!cancel.isCanceled) {
                    cancel.cancel()
                }
            }

        })
        mFingerprintDialog.show(supportFragmentManager, tag)
        mFingerprintDialog.refreshFingerprintState(FingerprintDialog.FINGERPRINT_CHECKING)
        mFingerprintManager?.authenticate(null, cancel, 0, object : FingerprintManager.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                Log.i(tag, "onAuthenticationError。。。code:$errorCode err:$errString。。")
                mFingerprintDialog.refreshFingerprintState(FingerprintDialog.FINGERPRINT_CHECK_ERROR)
            }

            override fun onAuthenticationFailed() {
                Log.i(tag, "onAuthenticationFailed。。。。。")
                mFingerprintDialog.refreshFingerprintState(FingerprintDialog.FINGERPRINT_CHECK_AGAIN)
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                Log.i(tag, "onAuthenticationHelp。。。help:$helpCode  $helpString。。")
            }

            override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
                Log.i(tag, "onAuthenticationSucceeded。。。。 result。")
                mFingerprintDialog.refreshFingerprintState(FingerprintDialog.FINGERPRINT_CHECK_SUCCESS)
            }
        }, null)
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private fun authenticate28() {
        val cancel = CancellationSignal()
        mBiometricPrompt?.authenticate(cancel, this@MainActivity.mainExecutor, object: BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                Log.i(tag, "onAuthenticationError。。。code:$errorCode err:$errString。。")
                cancel.cancel()
            }

            override fun onAuthenticationFailed() {
                Log.i(tag, "onAuthenticationFailed。。。。。")
            }

            override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                Log.i(tag, "onAuthenticationHelp。。。help:$helpCode  $helpString。。")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                Log.i(tag, "onAuthenticationSucceeded。。。。 result。")
            }
        })
    }

}
