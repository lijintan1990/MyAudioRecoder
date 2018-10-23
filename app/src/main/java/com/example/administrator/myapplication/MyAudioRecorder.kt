package com.example.administrator.myapplication

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile

class MyAudioRecorder {
    companion object {
        const val TAG: String = "MyAudioRecorder"
        private var sAudioRecorder = com.example.administrator.myapplication.MyAudioRecorder()
        fun createInstance():MyAudioRecorder {
            return sAudioRecorder
        }
    }

    private var mIsRecording:Boolean = false
    private var mAudioRecorder:AudioRecord? = null
    private val mSampleRate:Int = 44100
    private val mChannelConfig = AudioFormat.CHANNEL_IN_STEREO
    private val mAudioFormat:Int = AudioFormat.ENCODING_PCM_16BIT
    private var mAudioData:ByteArray? = null
    private var mFileStream:FileStream? = null
    private var mFileName:String = ""

    private fun createAudioRecorder() {
        if (mAudioRecorder == null) {
            val minBufferSize: Int = AudioRecord.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat)
            if (mAudioData == null) {
                mAudioData = ByteArray(minBufferSize * 2)
            }
            mAudioRecorder = AudioRecord(MediaRecorder.AudioSource.MIC,
                    mSampleRate, mChannelConfig, mAudioFormat, minBufferSize)
        }
    }

    fun startRecord(fileName:String):Int {
        var iRet : Int = -1
        if (mIsRecording || mAudioRecorder != null)
            return iRet

        mFileName = fileName
        mIsRecording = true
        createAudioRecorder()
        mAudioRecorder?.startRecording()
        mFileStream =  FileStream()
        var thread = Thread(Runnable {
            kotlin.run {
                mFileStream!!.openFile(mFileName)
                while (mIsRecording && !Thread.interrupted()) {
                    var len: Int? = mAudioRecorder?.read(mAudioData, 0, mAudioData!!.size)
                    Log.d(TAG, "read date len:" + len.toString())
                    mAudioData?.let { len?.let { it1 -> mFileStream!!.writeData(it, it1) } }
                }

                mAudioRecorder?.stop()
                mAudioRecorder?.release()
                mAudioRecorder = null
                mFileStream?.closeFile()
                writeHeader()
            }
        })

        thread.start()
        return iRet
    }

    fun stopRecord(){
        mIsRecording = false
    }

    private fun writeHeader() {
        try {
            val raf = RandomAccessFile(mFileName, "rw")
            raf.seek(0)
            val wavHeader = WavHeader(mSampleRate, mChannelConfig, raf.length(), mAudioFormat)
            raf.write(wavHeader.toBytes())
            raf.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}