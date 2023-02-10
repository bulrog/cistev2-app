package com.bulrog59.ciste2dot0.scenes.rules

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.scenes.Scene

class RulesScene(private val rulesOptions: RulesOptions, private val cisteActivity: CisteActivity) :
    Scene {

    override fun shutdown() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun verifyRules() {
        var nextScene = rulesOptions.defaultScene
        for (rule in rulesOptions.rules) {
            when (rule.ruleKey) {
                RuleKey.contains -> {
                    if (cisteActivity.inventory.contains(rule.itemIds)) {
                        nextScene = rule.nextScene
                        break
                    }
                }
                RuleKey.all -> {
                    if (cisteActivity.inventory.all(rule.itemIds)) {
                        nextScene = rule.nextScene
                        break
                    }
                }
            }

        }
        cisteActivity.setScene(nextScene)
    }


}