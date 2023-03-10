package jp.techacademy.taiyo.sasaki.taskapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.Date

open class Task : RealmObject(), Serializable {
    var title: String = ""      //タイトル
    var category: String = ""   //カテゴリ
    var contents: String = ""   //内容
    var date: Date = Date()     //日時

    // idをプライマリーキーとして設定
    @PrimaryKey
    var id : Int = 0
}