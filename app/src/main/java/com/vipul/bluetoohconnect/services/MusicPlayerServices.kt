package com.vipul.bluetoohconnect.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.vipul.bluetoohconnect.R


class MusicPlayerServices : Service() {


    private lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer = MediaPlayer.create(this, R.raw.sample_music)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}