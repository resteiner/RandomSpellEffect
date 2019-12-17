package com.ryansteiner.randomspelleffect.presenters

import android.content.Context
import com.ryansteiner.randomspelleffect.contracts.BaseContract
import java.lang.ref.WeakReference

/**
 * Created by Ryan Steiner on 2019/11/06.
 */

open class BasePresenter<V : BaseContract.View>(context: Context) : BaseContract.Presenter {

    private val TAG = "BasePresenter"

    private var mViewWeakRef: WeakReference<V>? = null
    internal var mContext: Context = context.applicationContext // Long life context

    fun bindView(view: V) {
        mViewWeakRef = WeakReference(view)
    }

    fun unbindView() {
        mViewWeakRef = null
    }

    fun getView(): V? {
        return if (mViewWeakRef != null) {
            mViewWeakRef!!.get()
        } else null
    }

    override fun loadingViewToggle(visible: Boolean) {
        val view: V? = getView()
        view?.onLoadingViewToggle(visible)
    }

}