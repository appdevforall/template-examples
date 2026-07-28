package com.example.bubblewand

import com.jme3.app.AndroidHarness

class MainActivity : AndroidHarness() {
    init {
        appClass = BubbleWandGame::class.java.name
    }
}
