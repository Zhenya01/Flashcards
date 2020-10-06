package com.zhenya.flashcards.classes

class Modules {

    var id: Int = -1
    var name: String = ""
    var description: String = ""
    var category: String = ""

    constructor(id:Int, name: String, description: String, category: String) {
        this.id=id
        this.name = name
        this.description = description
        this.category = category
    }

    constructor(name: String,description: String, category: String){
        this.name = name
        this.description = description
        this.category = category

    }

    constructor(id:Int, name: String, description: String) {
        this.id = id
        this.name = name
        this.description = description
    }

    override fun toString(): String {
        return "Modules(id=$id, name='$name', description='$description')"
    }

}