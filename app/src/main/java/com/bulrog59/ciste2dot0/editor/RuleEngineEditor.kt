package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getItemList
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getSceneDescription
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


    private var rulesOptions = GameOptionHelper.gamePreviousElement<RulesOptions, RulesOptions>(
        gameData,
        scenePosition
    ) { it }

    private val allItems = getItemList(gameData)

    private fun getItemText(itemID: Int): String {
        return allItems.filter { it.id == itemID }.map { it.name }.first()
    }

    private fun getRuleText(rule: Rule): String {
        val ruleText = activity.getText(rule.ruleKey.description)
        val itemListText = rule.itemIds.joinToString(",") { getItemText(it) }
        return "$ruleText: $itemListText-> ${getSceneDescription(gameData, rule.nextScene)}"
    }

    private fun getRulesText(rules: List<Rule>): List<String> {
        return rules.map { getRuleText(it) }

    }

    private fun getItemPositions(itemsID: List<Int>): List<Int> {
        val itemsPosition = mutableListOf<Int>()
        for (itemID in itemsID) {

            for (itemPosition in allItems.indices) {
                if (allItems[itemPosition].id == itemID) {
                    itemsPosition.add(itemPosition)
                    break
                }
            }

        }
        return itemsPosition
    }

    private fun selectItems(rule: Rule?, ruleKey: RuleKey, doneRule: (Rule) -> Unit) {
        MultipleItemPicker(activity).init(
            R.string.item_selection_title,
            allItems.map { it.name },
            getItemPositions(rule?.itemIds ?: emptyList())

        ) { positionList ->
            GameOptionHelper.getItemPickerNextScene<RulesOptions>(
                activity,
                gameData,
                scenePosition,
                { rule?.nextScene }) { nextScene ->
                doneRule(Rule(ruleKey, positionList.map { allItems[it].id }, nextScene))
            }

        }
    }

    private fun editRule(rule: Rule?, doneRule: (Rule) -> Unit) {
        ItemPicker(activity).init(
            R.string.rule_picker_title,
            RuleKey.values().map { activity.getString(it.description) }) { it ->
            selectItems(rule, RuleKey.values()[it], doneRule)
        }
    }

    fun init() {
        ListEditor(
            activity,
            rulesOptions?.rules ?: emptyList(),
            this::getRulesText,
            this::editRule
        ) {
            //TODO: add edition to select the default scene:
            //TODO: add editor to change scene order:
            done(convertToJsonNode(RulesOptions(it, rulesOptions?.defaultScene ?: -1)))
        }.init()

    }
}