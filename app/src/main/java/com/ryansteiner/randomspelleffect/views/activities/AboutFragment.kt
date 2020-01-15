package com.ryansteiner.randomspelleffect.views.activities

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.ryansteiner.randomspelleffect.R
import com.ryansteiner.randomspelleffect.data.*
import com.ryansteiner.randomspelleffect.data.models.FullCard
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.ryansteiner.randomspelleffect.utils.MyMathUtils
import com.ryansteiner.randomspelleffect.utils.PreferencesManager
import com.ryansteiner.randomspelleffect.utils.TextSpanUtils
import kotlinx.android.synthetic.main.fragment_faq.view.*
import kotlin.math.roundToInt

class AboutFragment(private val c: Context)// Required empty public constructor
    : Fragment() {

    private val TAG = "AboutFragment"

    private val mContext: Context = c
    private var mFullCard: FullCard? = null
    private var mIsFaq: Boolean = false
    private var mFullText: String? = null
    private var mCallback: AboutActivity? = null
    private var mView: View? = null
    private var mDamagePreferences: List<Int>? = null
    private var mYouTubePlayerView: YouTubePlayerView? = null
    private var mYouTubePlayer: YouTubePlayer? = null
    private var mPreferencesManager: PreferencesManager? = null


    //private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferencesManager = PreferencesManager(mContext)
        Log.d(TAG, "onCreate  [${mPreferencesManager?.getCurrentLifeTime()}]")
        if (arguments != null) {
            mIsFaq = arguments!!.getBoolean(EXTRA_IS_FAQ)
        }
    }

    private fun initializeView(v: View) {
        Log.d(TAG, "init  [${mPreferencesManager?.getCurrentLifeTime()}]")
        mView = v

        if (mIsFaq) {
            val textView = v.tFaqDescription06
            val startString = getString(R.string.faq_did_you_steal_a)
            val stringList = startString.split(";")
            //DEBUG
            stringList.forEach {
                Log.d(TAG, "forEach in stringList it = $it")
            }
            //
            val builder = SpannableStringBuilder()
            val preString = stringList.firstOrNull() ?: "Something went terribly wrong getting the URL for The Net Libram... There were no strings in the list..."
            val urlString = if (stringList.count() > 1) {
                getString(R.string.net_libram_link)
            } else {
                "Something went terribly wrong getting the URL for The Net Libram... There was only one..."
            }

            val postString = if (stringList.count() > 2) {
                stringList[2]
            } else {
                "Something went terribly wrong getting the URL for The Net Libram... There were only two..."
            }
            builder.append(preString)

            val colorStartPosition = builder.length
            builder.append(urlString)

            /*
            If I ever need to add an inline image, this is how that's done:
            val imageSpan = CenteredImageSpan(this, R.drawable.green_deal)
            builder.setSpan(imageSpan, builder.length - 1, builder.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
            */

            val colorEndPosition = builder.length
            builder.append(postString)

            val coloredString: String = builder.toString()

            TextSpanUtils().setTextClickable(textView, coloredString, colorStartPosition, colorEndPosition, {onNetLibramClick()}, drawUnderline = true)
            val finalString = builder

            //textView.text = finalString
        }

    }

    private fun onNetLibramClick() {
        val urlString = getString(R.string.net_libram_url)
        val uri = Uri.parse(urlString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var varV: View? = null
        Log.d(TAG, "onCreateView  [Pre-inflate - ${mPreferencesManager?.getCurrentLifeTime()}]")
        varV = if (mIsFaq) {
            inflater.inflate(R.layout.fragment_faq, container, false)
        } else {
            inflater.inflate(R.layout.fragment_about, container, false)
        }
        Log.d(TAG, "onCreateView  [Post-inflate ${mPreferencesManager?.getCurrentLifeTime()}]")
        val v = varV ?: inflater.inflate(R.layout.fragment_about, container, false)
        initializeView(v)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*Log.d(TAG, "onAttach  [${mPreferencesManager?.getCurrentLifeTime()}]")
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }*/
    }

    override fun onDetach() {
        super.onDetach()
        //mListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        //mYouTubePlayerView?.release()
    }

    fun setCallback(callback: AboutActivity) {
        mCallback = callback

    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        fun newInstance(c: Context, isFaq: Boolean): AboutFragment {
            val fragment = AboutFragment(c)
            val args = Bundle()
            args.putSerializable(EXTRA_IS_FAQ, isFaq)
            fragment.arguments = args
            return fragment
        }
    }
}

class AboutFragmentPageViewAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val TAG = "MainCardPageViewAdapter"
    val fragments: ArrayList<Fragment> = ArrayList()
    val titles: ArrayList<String> = ArrayList()

    override fun getCount(): Int = fragments?.count()

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun addFragment(fragment: Fragment, title: String) {
        Log.d(TAG, "addFragment - fragment = $fragment")
        Log.d(TAG, "addFragment - title = $title")
        fragments.add(fragment)
        titles.add(title)
    }

    fun clearFragments() {
        fragments.clear()
        titles.clear()
    }
}