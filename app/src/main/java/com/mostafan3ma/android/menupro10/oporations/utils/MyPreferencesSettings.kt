package com.mostafan3ma.android.menupro10.oporations.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Color.parseColor
import com.google.gson.Gson
import com.mostafan3ma.android.menupro10.oporations.utils.ChosenStyle.*

const val STYLES_PREFERENCES_FILE = "stylePreferencesFile"
const val STYLE_KEY = "style"


enum class ChosenStyle(val styleCode: Int) {
    DEFAULT(0),
    BLUR_PRO(1),
    BAKERY_BLACK(2),
    STANDARD_MATERIAL_DETAILS_ITEMS(3),
    COLORIZES_CATEGORIES_DETAILS_ITEMS(4),
    CATEGORIES_LIST_SWEETS_DETAILS_ITEMS(5)
}

enum class ProductListItemStyle(val itemStyleCode: Int) {
    DEFAULT_ITEM(0),
    MATERIAL_ITEM(1),
    BAKERY_ITEM(2),
    BLUR_ITEM(3),
    DEFAULT_CATEGORY_ITEM(4),
    COLORIZES_CATEGORY_ITEM(5)
}


data class ProductListItem(
    var style: ProductListItemStyle = ProductListItemStyle.DEFAULT_ITEM,
    var item_size: String = DEFAULT_ITEM_SIZE,
    var background_color: Int = DEFAULT_COLOR_VALUE,
    var name_text_size: String = DEFAULT_SIZE,
    var name_text_color: Int = DEFAULT_COLOR_VALUE,
    var description_text_color: Int = DEFAULT_COLOR_VALUE,
    var description_text_size: String = DEFAULT_SIZE,
    var concurrency_type_text: String = DEFAULT_CONCURRENCY,
    var concurrency_text_size: String = DEFAULT_SIZE,
    var concurrency_text_Color: Int = DEFAULT_COLOR_VALUE,
    var size_text_color: Int = DEFAULT_COLOR_VALUE,
    var size_text_size: String = DEFAULT_SIZE
) {
    companion object {
        const val DEFAULT_CONCURRENCY = "$"
        const val DEFAULT_VALUE = "default"
        const val DEFAULT_COLOR_VALUE = 0
        const val DEFAULT_SIZE = "20dp"
        const val DEFAULT_ITEM_SIZE = "default size"
    }
}


data class Style(
    var styleCode: ChosenStyle = DEFAULT,
    var attributes: String = DEFAULT_VALUE,
    var backgroundChoice: Int = BACKGROUND_IMAGE_CHOICE,
    var mainBackgroundImage: String = DEFAULT_VALUE,
    var mainBackgroundColor: Int? = null,
    var welcomeText: String = DEFAULT_VALUE,
    var welcomeTextAlign: String = ALIGN_LEFT,
    var welcomeTextSize: String = DEFAULT_TEXT_SIZE,
    var welcomeTextColor: Int = DEFAULT_TEXT_COLOR,
    var chipsCheckedColor: Int = parseColor(DEFAULT_CHIPS_CHECKED_COLOR),
    var chipsCheckedTextColor: Int = parseColor(DEFAULT_BLACK_COLOR),
    var chipsUnCheckedColor: Int = parseColor(DEFAULT_CHIPS_UNCHECKED_COLOR),
    var chipsUnCheckedTextColor: Int = parseColor(DEFAULT_BLACK_COLOR),
    var recyclerRawCount: Int = DEFAULT_RAW_COUNT,
    var product_list_item: ProductListItem = ProductListItem()
) {
    companion object {
        const val DEFAULT_VALUE = "default"
        const val DEFAULT_RAW_COUNT = 0
        const val CUSTOM_ATTRIBUTES = "custom"
        const val ALIGN_LEFT = "left"
        const val ALIGN_RIGHT = "right"
        const val DEFAULT_TEXT_SIZE = "20dp"
        const val DEFAULT_TEXT_COLOR = 0
        const val DEFAULT_BLACK_COLOR = "#FF000000"
        const val DEFAULT_WHITE_COLOR = "#FFFFFFFF"
        const val DEFAULT_CHIPS_CHECKED_COLOR = "#FFA800"
        const val DEFAULT_CHIPS_UNCHECKED_COLOR = "#EFEFEF"


        const val BACKGROUND_IMAGE_CHOICE = 1
        const val BACKGROUND_COLOR_CHOICE = 2
        const val DEFAULT_BACKGROUND_IMAGE_NAME = "main background 1"
        const val TEMP_BACKGROUND_IMAGE_NAME = "main background 2"
    }

    init {
        when (styleCode) {
            DEFAULT -> {}
            BLUR_PRO -> {
                product_list_item.style=ProductListItemStyle.BLUR_ITEM
            }
            BAKERY_BLACK -> {
                product_list_item.style=ProductListItemStyle.BAKERY_ITEM
            }
            STANDARD_MATERIAL_DETAILS_ITEMS -> {
                product_list_item.style=ProductListItemStyle.MATERIAL_ITEM
            }
            COLORIZES_CATEGORIES_DETAILS_ITEMS -> {}
            CATEGORIES_LIST_SWEETS_DETAILS_ITEMS -> {}
        }
    }


}


fun putPrefInt(context: Context, key: String, value: Int) {
    val sharedPreferences = context.getSharedPreferences(STYLES_PREFERENCES_FILE, MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt(key, value)
    editor.apply()
}


fun getPrefInt(context: Context, key: String, ifNullValue: Int): Int {
    val sharedPreferences = context.getSharedPreferences(STYLES_PREFERENCES_FILE, MODE_PRIVATE)
    return sharedPreferences.getInt(key, ifNullValue)
}


fun putPrefStyle(context: Context, style: Style) {
    val gson: Gson = Gson()
    val jsonStyle = gson.toJson(style)
    val sharedPreferences = context.getSharedPreferences(STYLES_PREFERENCES_FILE, MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString(STYLE_KEY, jsonStyle)
    editor.apply()
}

fun getPrefStyle(context: Context): Style {
    val gson: Gson = Gson()
    val defaultStyle = Style()
    val defaultStyleJson: String = gson.toJson(defaultStyle)
    val sharedPreferences = context.getSharedPreferences(STYLES_PREFERENCES_FILE, MODE_PRIVATE)
    val returnedJsonStyle: String? = sharedPreferences.getString(STYLE_KEY, defaultStyleJson)
    return gson.fromJson(returnedJsonStyle, Style::class.java)
}





