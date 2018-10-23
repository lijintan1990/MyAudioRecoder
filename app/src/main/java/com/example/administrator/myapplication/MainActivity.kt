package com.example.administrator.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG:String = "MyAudioRecord"
    }
    class PermissionListener {
        public var mAudioPermmitOk:Int = 0
        fun  onGranted() {
            Log.d(TAG,"request audio permission ok")
            mAudioPermmitOk = 1
        }
        fun onDenied( deniedPermission:List<String>) {
            Log.d(TAG, "audio permission failed")
            mAudioPermmitOk = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val permits:Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        var listener = PermissionListener()
        requestRunPermission(permits, listener)

        if (listener.mAudioPermmitOk == 0)
            return
    }

    public fun startRecord(v:View) {
        var myAudioRecorder = MyAudioRecorder.createInstance()
        myAudioRecorder.startRecord("/sdcard/audio.wav")
    }

    public fun stopRecord(v:View) {
        var myAudioRecorder = MyAudioRecorder.createInstance()
        myAudioRecorder.stopRecord()
    }

    var mListener:PermissionListener? = null
    /**
     * 授权运行
     * @param permissions 需要权限
     * @param listener 权限监听
     */
    fun requestRunPermission(permissions: Array<String>, listener: PermissionListener) {
        mListener = listener
        val permissionLists = arrayListOf<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionLists.add(permission)
            }
        }

        if (!permissionLists.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionLists.toTypedArray(), 1)
        } else {
            //表示全都授权了
            mListener!!.onGranted()
        }
    }

    /**
     * 申请授权处理结果
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> try {
                if (grantResults.size > 0) {
                    //存放没授权的权限
                    val deniedPermissions = arrayListOf<String>()
                    for (i in grantResults.indices) {
                        val grantResult = grantResults[i]
                        val permission = permissions[i]
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission)
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        //说明都授权了
                        mListener?.onGranted()
                    } else {
                        mListener?.onDenied(deniedPermissions)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            else -> {
            }
        }
    }

}
