package com.albuquerque.customcomponents.extensions

import android.view.View

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
    this.animation?.cancel()
}

fun View.setInvisible() {
    this.visibility = View.INVISIBLE
    this.animation?.cancel()
}