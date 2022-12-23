package com.mostafan3ma.android.menupro10.oporations.utils

import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import javax.inject.Inject

class SimpleDrawerController
constructor(drawerLayout: DrawerLayout,navController: NavController) {
    private var drawerLayout: DrawerLayout
    private var navController: NavController?
    private val viewsList: MutableList<ClickableView> = mutableListOf<ClickableView>()
    private val _drawerEvent = MutableLiveData<String>()
    val drawerEvent: LiveData<String> get() = _drawerEvent


    companion object{
        const val TAG="SimpleDrawerController"
    }

    init {
        Log.d(TAG, "SimpleDrawerController: initiating")
        this.drawerLayout = drawerLayout
        this.navController=navController
        Log.d(TAG, "SimpleDrawerController: drawerLayout: ${drawerLayout.toString()}\n navController=${navController.toString()}")
    }



    /**
    *add the views that will used for clicking events in the drawer for navigation or any other actions
     **/
    fun addClickableViews(vararg clickableView: ClickableView) {
        Log.d(TAG, "addClickableViews: adding Clickable views")
        clickableView.map {
            viewsList.add(it)
            Log.d(TAG, "addClickableViews: adding view ${it.toString()}///viewsList.size=${viewsList.size}")
        }
        viewsList.map { clickableView: ClickableView ->
            clickableView.view.setOnClickListener {
                Log.d(TAG, "addClickableViews: setting clickListener to ${it.toString()}")
                setDrawerEvent(clickableView)
            }
        }
    }


    /**
     * set the event to from the clicked view to trigger the observer in the fragment and preform action.
     * if the clicked view hav no nul direction this class will navigate automatically to the given direction.
     */
    private fun setDrawerEvent(clickableView: ClickableView) {
        _drawerEvent.value = clickableView.actionCode
        Log.d(TAG, "setDrawerEvent: setting Event :${_drawerEvent.value}")
        if (navController!=null&&clickableView.direction!=null){
            closeDrawer()
            navController!!.navigate(clickableView.direction)
            Log.d(
                TAG,
                "setDrawerEvent: navController not null>> navigating to ${clickableView.direction}"
            )
        }else if (navController==null){
            Log.d(TAG, "setDrawerEvent: navController =null")
        }else if (clickableView.direction==null){
            Log.d(TAG, "setDrawerEvent: Direction for ${clickableView.toString()} =null")
        }
        _drawerEvent.value = ""
    }

    /**
     * open the drawer
     */
    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
        Log.d(TAG, "openDrawer: Drawer is opened")
    }


    /**
     * close the drawer
     */
     fun closeDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }


}

/**
 * a class that helped to save the view and the action or the direction that need to be performed or navigated to when the view is clicked
 */
class ClickableView constructor(ClickableView: View, action: String,navDirections: NavDirections?=null) {
    val view = ClickableView
    val actionCode = action
    val direction=navDirections
}