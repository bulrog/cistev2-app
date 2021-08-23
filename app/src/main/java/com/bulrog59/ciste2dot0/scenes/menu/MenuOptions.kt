package com.bulrog59.ciste2dot0.scenes.menu

data class MenuOptions(val menuItems:List<MenuItem>)

data class MenuItem(val buttonText:String,val nextScene:Int)
