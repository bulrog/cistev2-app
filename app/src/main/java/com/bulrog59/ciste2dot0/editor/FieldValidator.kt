package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.widget.EditText
import com.bulrog59.ciste2dot0.GameMgtActivity
import com.bulrog59.ciste2dot0.R

class FieldValidator(val activity: Activity) {

    companion object {
        val MAX_CHAR=50
    }

    private fun validateField(
        fieldId: Int,
        validator: (String) -> Boolean,
        errorMessage: String
    ): Boolean {
        val field = activity.findViewById<EditText>(fieldId)
        val error = validator(field.text.toString())
        if (error) {
            field.error = errorMessage
        }
        return error
    }

    fun notEmptyField(fieldId: Int): Boolean {
        return validateField(
            fieldId,
            String::isEmpty,
            activity.getString(R.string.empty_field_error)
        )
    }
    fun maxSizeField(fieldId: Int):Boolean {
        return validateField(fieldId,{v-> v.length > MAX_CHAR},activity.getString(R.string.tool_long_field)+ MAX_CHAR)
    }

    fun inList(fieldId: Int,elements:List<String>):Boolean{
        return validateField(fieldId,{ v -> !elements.contains(v) },activity.getString(R.string.element_not_in_list))
    }

}