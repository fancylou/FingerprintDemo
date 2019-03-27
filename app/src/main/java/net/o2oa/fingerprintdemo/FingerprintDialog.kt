package net.o2oa.fingerprintdemo

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.DialogFragment
import android.view.*
import android.widget.Button
import android.widget.TextView

/**
 * Created by fancyLou on 2019/3/27.
 */



class FingerprintDialog : DialogFragment() {

    companion object {
        const val FINGERPRINT_CHECKING = 1
        const val FINGERPRINT_CHECK_AGAIN = 2
        const val FINGERPRINT_CHECK_ERROR = 3
        const val FINGERPRINT_CHECK_SUCCESS = 4
    }

    interface OnFingerprintDialogActionCallback {
        fun onDialogDismiss()
        fun onCancel()
    }


    private var mDialogActionCallback: OnFingerprintDialogActionCallback? = null
    private var messageTV: TextView? = null
    private var cancelBtn: Button? = null

    fun setOnBiometricPromptDialogActionCallback(callback: OnFingerprintDialogActionCallback) {
        mDialogActionCallback = callback
    }
    fun refreshFingerprintState(state: Int) {
        when(state) {
            FINGERPRINT_CHECKING -> {
                messageTV?.text = "正在识别中..."
            }
            FINGERPRINT_CHECK_AGAIN -> {
                messageTV?.text = "识别失败，请重试."
                cancelBtn?.visibility = View.VISIBLE
            }
            FINGERPRINT_CHECK_ERROR -> {
                messageTV?.text = "验证失败"
                cancelBtn?.visibility = View.VISIBLE
                messageTV?.postDelayed({ dismiss() }, 500)
            }
            FINGERPRINT_CHECK_SUCCESS -> {
                messageTV?.text = "验证成功"
                messageTV?.postDelayed({ dismiss() }, 500)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val window = dialog.window
        if (window != null) {
            val lp = window.attributes
            lp.gravity = Gravity.CENTER
            lp.dimAmount = 0f
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
            window.attributes = lp
            window.setBackgroundDrawableResource(R.color.fingerprintBackgroundColor)
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog, container)

        val rootView = view.findViewById<ConstraintLayout>(R.id.root_view)
        rootView.isClickable = false
        messageTV = view.findViewById(R.id.message)
        cancelBtn = view.findViewById(R.id.cancel)
        cancelBtn!!.setOnClickListener {
            if (mDialogActionCallback != null) {
                mDialogActionCallback!!.onCancel()
            }
            dismiss()
        }
        return view
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        if (mDialogActionCallback != null) {
            mDialogActionCallback!!.onDialogDismiss()
        }
    }

}