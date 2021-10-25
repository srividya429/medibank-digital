package com.medibank.shop.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsAppActivityTest{

    @Rule
    val newsAppActivity = ActivityTestRule(NewsAppActivity::class.java)
}