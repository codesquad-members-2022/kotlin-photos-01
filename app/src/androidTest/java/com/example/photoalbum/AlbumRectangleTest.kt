package com.example.photoalbum

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlbumRectangleTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.example.photoalbum", appContext.packageName)
    }

    @Test
    fun addAlbumRectangleTest() {
        val view = RecyclerFragment()
        val list = view.createAlbumRectangle()

        Assert.assertEquals(40, list.size)
    }

}