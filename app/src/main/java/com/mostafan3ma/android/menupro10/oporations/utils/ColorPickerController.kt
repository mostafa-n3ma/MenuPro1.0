package com.mostafan3ma.android.menupro10.oporations.utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import yuku.ambilwarna.AmbilWarnaDialog
import javax.inject.Inject

class ColorPickerController
@Inject
constructor() {


    /**
     * liveData to observed and receive the selected Color
     * لايف داتا يتم اختزالها لاستلام قيمة اللون المختار
     */
    private val _selectedColor=MutableLiveData<Int>()
    val selectedColor:LiveData<Int>get() = _selectedColor

    fun resetSelectedColor(){
        _selectedColor.value=0
    }




    init {
        _selectedColor.value= 0
    }

    /**
     * request code to be considered later when receiving the selected color and apply it to the applicant
     * كود طلب يتم اعتباره  عند قدوم نتيجة لمعرفة صاحب الطلب وتمرير اللون له
     */
    var requestCode = DefaultCode

    companion object{
        const val DefaultCode="default"
    }


    /**
     * show the color picker dialog .
     * parameters:
     * -default color : of the view that you want to change it's color.
     * -requestCode : code to be presented when asking for the color picker
     *
     * request code will be used to choose which view want to change it's color
     */
    fun showColorPicker(context: Context,defaultColor:Int,requestCode:String){
        this.requestCode=requestCode
        val colorPicker=AmbilWarnaDialog(context,defaultColor,object :AmbilWarnaDialog.OnAmbilWarnaListener{
            override fun onCancel(dialog: AmbilWarnaDialog?) {

            }
            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                _selectedColor.value=color

            }
        })
        colorPicker.show()
    }





}