package com.bulrog59.ciste2dot0.scenes.detector

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DetectorOption(val pic2Scene:Map<String, Int>)
