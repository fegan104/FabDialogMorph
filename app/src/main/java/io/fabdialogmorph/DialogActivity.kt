package io.fabdialogmorph

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.transition.ArcMotion
import android.util.TypedValue
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_dialog.*

fun Context.getAccentColor(): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
    return typedValue.data
}

class DialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        //option 1
//        setupSharedElementTransitions1()
        //option 2
        setupSharedElementTransitions2()
        container.setOnClickListener { dismiss() }
        close.setOnClickListener { dismiss() }
    }

    private fun setupSharedElementTransitions1() {
        val arcMotion = ArcMotion()
        arcMotion.minimumHorizontalAngle = 50f
        arcMotion.minimumVerticalAngle = 50f

        val easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in)

        val sharedEnter = MorphFabToDialog()
        sharedEnter.pathMotion = arcMotion
        sharedEnter.interpolator = easeInOut

        val sharedReturn = MorphDialogToFab()
        sharedReturn.pathMotion = arcMotion
        sharedReturn.interpolator = easeInOut

        if (container != null) {
            sharedEnter.addTarget(container)
            sharedReturn.addTarget(container)
        }
        window.sharedElementEnterTransition = sharedEnter
        window.sharedElementReturnTransition = sharedReturn
    }

    private fun setupSharedElementTransitions2() {
        val arcMotion = ArcMotion()
        arcMotion.minimumHorizontalAngle = 50f
        arcMotion.minimumVerticalAngle = 50f

        val easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in)

        val sharedEnter = MorphTransition(getAccentColor(),
                ContextCompat.getColor(this, R.color.dialog_background_color), 100, resources.getDimensionPixelSize(R.dimen.dialog_corners), true)
        sharedEnter.pathMotion = arcMotion
        sharedEnter.interpolator = easeInOut

        val sharedReturn = MorphTransition(ContextCompat.getColor(this, R.color.dialog_background_color),
                getAccentColor(), resources.getDimensionPixelSize(R.dimen.dialog_corners), 100, false)
        sharedReturn.pathMotion = arcMotion
        sharedReturn.interpolator = easeInOut

        if (container != null) {
            sharedEnter.addTarget(container)
            sharedReturn.addTarget(container)
        }
        window.sharedElementEnterTransition = sharedEnter
        window.sharedElementReturnTransition = sharedReturn
    }

    override fun onBackPressed() = dismiss()

    private fun dismiss() {
        setResult(Activity.RESULT_CANCELED)
        finishAfterTransition()
    }

}
