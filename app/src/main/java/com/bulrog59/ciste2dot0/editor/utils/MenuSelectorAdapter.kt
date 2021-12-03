package com.bulrog59.ciste2dot0.editor.utils


class MenuSelectorAdapter(
    choices: List<String>,
    previousPosition: Int,
    private val callBack: (Int) -> Unit
) :
    MultipleMenuSelectorAdapter(choices, mutableListOf(previousPosition), true, { callBack(it[0]) })
