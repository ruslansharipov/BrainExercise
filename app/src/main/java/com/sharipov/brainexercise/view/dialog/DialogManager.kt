package com.sharipov.brainexercise.view.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.sharipov.brainexercise.R
import com.sharipov.brainexercise.model.firebase.TestResult
import com.sharipov.brainexercise.mvp.TestPresenter

class DialogManager {
    private var activity: FragmentActivity? = null
    private var presenter: TestPresenter? = null

    private lateinit var dialog: AlertDialog

    fun onAttach(activity: FragmentActivity?, presenter: TestPresenter?) {
        this.activity = activity
        this.presenter = presenter
    }

    fun onDetach() {
        activity = null
        presenter = null
    }

    fun showPauseDialog(score: Int) = showDialog(
        score,
        R.string.card_pause_dialog_title,
        R.string.card_dialog_positive_button,
        DialogInterface.OnClickListener { d, w -> presenter?.onResumeTest() },
        R.string.card_dialog_button_restart_card_test,
        DialogInterface.OnClickListener { d, w -> presenter?.onRestartTest() }
    )

    fun showFinishDialog(result: TestResult) {
        val fragment = FinishDialogFragment().apply {
            this.result = result
            title = R.string.card_finish_dialog_title
            positiveText = R.string.card_dialog_positive_button
            positiveAction = {
                presenter?.saveResults()
                navigateToExercises()
            }
            negativeText = R.string.card_dialog_button_restart_card_test
            negativeAction = { presenter?.onRestartTest() }
        } as DialogFragment
        fragment.show(activity?.supportFragmentManager, "Finish dialog")

    }

    private fun navigateToExercises() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.exercisesFragment, true)
            .setLaunchSingleTop(true)
            .build()
        activity?.findNavController(R.id.navHostFragment)
            ?.navigate(R.id.exercisesFragment, null, navOptions)
    }

    fun showLeaveDialog(score: Int) = showDialog(
        score,
        R.string.card_quit_dialog_title,
        R.string.card_quit_dialog_positive_button,
        DialogInterface.OnClickListener { d, w -> navigateToExercises() },
        R.string.card_quit_dialog_continue_button,
        DialogInterface.OnClickListener { d, w -> presenter?.onFragmentResume() }
    )

    private fun showDialog(
        score: Int,
        title: Int,
        positiveText: Int,
        positiveAction: DialogInterface.OnClickListener,
        negativeText: Int,
        negativeAction: DialogInterface.OnClickListener
    ) {
        val message = "${activity?.getString(R.string.card_dialog_message)} $score"
        val isNotInitializedAndNotShowing = !(::dialog.isInitialized && dialog.isShowing)
        if (isNotInitializedAndNotShowing) {
            dialog = AlertDialog.Builder(activity as Context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveText, positiveAction)
                .setNegativeButton(negativeText, negativeAction)
                .create()
                .also { it.show() }
        }
    }
}