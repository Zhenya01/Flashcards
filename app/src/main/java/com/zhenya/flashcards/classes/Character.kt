package com.zhenya.flashcards.classes

class Character {

    var id : Int = 0
    var hanzi : String = ""
    var pinyin : String = ""
    var eng : String = ""
    var module : String = ""
    var category: String = ""

    constructor(id: Int, hanzi:String, pinyin:String, eng:String, module:String, category: String) {
        this.id = id
        this.hanzi = hanzi
        this.pinyin = pinyin
        this.eng = eng
        this.module = module
        this.category = category
    }
    constructor(hanzi:String, pinyin:String, eng:String, module:String, category: String) {
        this.hanzi = hanzi
        this.pinyin = pinyin
        this.eng = eng
        this.module = module
        this.category = category
    }

}
