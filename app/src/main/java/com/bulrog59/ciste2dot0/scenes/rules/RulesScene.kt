package com.bulrog59.ciste2dot0.scenes.rules

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.scenes.Scene

class RulesScene(private val rulesOptions: RulesOptions, private val cisteActivity: CisteActivity) :
    Scene {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun verifyRules() {
        for (rule in rulesOptions.rules){
            var nextScene=rulesOptions.defaultScene
            when(rule.ruleKey){
                RuleKey.contains-> {
                   if (cisteActivity.inventory.contains(rule.itemIds)){
                    nextScene=rule.nextScene
                   }
                }
                RuleKey.all -> {
                    if (cisteActivity.inventory.all(rule.itemIds)){
                        nextScene=rule.nextScene
                    }
                }
            }
            cisteActivity.setScene(nextScene)
        }
    }


}