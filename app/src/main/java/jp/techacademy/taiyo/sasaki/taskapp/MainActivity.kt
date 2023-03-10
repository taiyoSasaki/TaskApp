package jp.techacademy.taiyo.sasaki.taskapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*

const val EXTRA_TASK = "jp.techacademy.taiyo.sasaki.taskapp.TASK"

class MainActivity : AppCompatActivity() {
    //Realmクラスの実装
    private lateinit var mRealm: Realm
    private  val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            reloadListView()
        }
    }

    //TaskAdapterクラスの実装
    private lateinit var mTaskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }

        //Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)

        //ListViewの設定
        mTaskAdapter = TaskAdapter(this)

        //ListViewをタップしたときの処理
        listView1.setOnItemClickListener { parent, view, position, id ->
            //入力・編集する画面に遷移させる
            val task = parent.adapter.getItem(position) as Task
            val intent = Intent(this, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }

        //ListViewを長押ししたときの処理
        listView1.setOnItemLongClickListener { parent, _, position, _ ->
            //タスクを削除する
            val task = parent.adapter.getItem(position) as Task

            //ダイアログを表示する
            val builder = AlertDialog.Builder(this)

            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか")

            builder.setPositiveButton("OK") { _, _ ->
                val results = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()

                mRealm.beginTransaction()
                results.deleteAllFromRealm()
                mRealm.commitTransaction()

                val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
                val resultPendingIntent = PendingIntent.getBroadcast(
                    this,
                    task.id,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(resultPendingIntent)

                reloadListView()
            }

            builder.setNegativeButton("cancel", null)

            val dialog = builder.create()
            dialog.show()

            true
        }

        //検索欄の実装
        search_button.setOnClickListener {
            var search = search_edit_text.text.toString()

            //何か入力されている場合
            if (search != "") {
                var searchResults = arrayListOf<Task>()
                for (i in 0..mTaskAdapter.getCount() - 1) {
                    var category = mTaskAdapter.getItemCategry(i)
                    if (category == search) {
                        searchResults.add(mTaskAdapter.getItem(i) as Task)
                    }
                }
                mTaskAdapter.mTaskList = searchResults

                listView1.adapter = mTaskAdapter
                mTaskAdapter.notifyDataSetChanged()

            }
        }

        reloadListView()
    }

    private fun reloadListView() {
        //Realmデータベースから、「すべてのデータを取得して新しい日時順に並べた結果」を取得
        val taskRealmResults = mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)

        //上記の結果を、TaskListとしてセットする
        mTaskAdapter.mTaskList = mRealm.copyFromRealm(taskRealmResults)

        //TaskListView用のアダプターに渡す
        listView1.adapter = mTaskAdapter

        //表示を更新するために、アダプターにデータが更新されたことを知らせる
        mTaskAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
    }

}