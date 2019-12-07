package com.ryansteiner.randomspelleffect.views.activities

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.contracts.BaseContract
import com.ryansteiner.randomspelleffect.presenters.BasePresenter

/**
 * Created by Ryan Steiner on 2019/11/06.
 */

abstract class BaseActivity : FragmentActivity(), BaseContract.View {

    private val TAG = "BaseActivity"

    override fun onShowToastMessage(message: String?) {
        val safeMessage = when {
            message.isNullOrBlank() -> {resources.getString(R.string.generic_error_message)}
            else -> {message}
        }

        runOnUiThread {
            Toast.makeText(this, safeMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onShowToastMessage(messageResId: Int) {
        val safeMessage = when {
            messageResId < 0 -> {resources.getString(R.string.generic_error_message)}
            else -> {getString(messageResId)}
        }

        runOnUiThread {
            Toast.makeText(this, safeMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onProgressViewToggle(visible: Boolean) {
        onShowToastMessage("onToggleProgressView was called")
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

