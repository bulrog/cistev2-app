package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getItemList
import com.bulrog59.ciste2dot0.editor.utils.ItemPicker
import com.bulrog59.ciste2dot0.editor.utils.ListEditor
import com.bulrog59.ciste2dot0.editor.utils.MultipleItemPicker
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.rules.Rule
import com.bulrog59.ciste2dot0.scenes.rules.RuleKey
import com.bulrog59.ciste2dot0.scenes.rules.RulesOptions
import com.fasterxml.jackson.databind.JsonNode

class RuleEngineEditor(
    private val activity: Activity,
    private val gameData: GameData,
    private val scenePosition: Int,
    private val done: (JsonNode) -> Unit
) {

    private var rules = GameOptionHelper.gamePreviousElement<List<Rule>, RulesOptions>(
        gameData,
        scenePosition
    ) { it?.rules } ?: emptyList()

    private val allItems=getItemList(gameData)

    private fun getItemText(itemID:Int):String{
        return allItems.filter { it.id==itemID }.map { it.name }.first()
    }

    private fun getRuleText(rule:Rule):String{
        val ruleText=activity.getText(rule.ruleKey.description)
        val itemListText=rule.itemIds.joinToString(",") { getItemText(it) }
        return "$ruleText: $itemListText"
    }

    private fun getRulesText(rules:List<Rule>):List<String>{
        return rules.map {getRuleText(it)}

    }

    private fun editRule(rule:Rule?, done:(Rule)->Unit){
       //TODO: rework itempicker and menuselectoradaptor to be particular case of the multiple case:
       ItemPicker(activity).init(
           R.string.rule_picker_title,
           RuleKey.values().map { activity.getString(it.description) }){
           val ruleKey=RuleKey.values()[it]
//           MultipleItemPicker(activity).init(
//
//           )

       }
    }

    fun init() {
        ListEditor(activity, rules,this::getRulesText,this::editRule){
            done(convertToJsonNode(it))
        }

    }
}