package com.sartori.rmnews.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/** Centralizes event names and keeps text parameters within Analytics limits. */
class FirebaseNewsAnalytics @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : NewsAnalytics {
    override fun trackSearch(query: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, query.take(MAX_TEXT_LENGTH))
        })
    }

    override fun trackArticleOpened(title: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, articleParams(title))
    }

    override fun trackRefresh() {
        firebaseAnalytics.logEvent("refresh_news", null)
    }

    // Measures button clicks, not successful browser loads or completed reading.
    override fun trackFullArticleClicked(title: String) {
        firebaseAnalytics.logEvent("open_full_article", articleParams(title))
    }

    private fun articleParams(title: String) = Bundle().apply {
        putString(FirebaseAnalytics.Param.CONTENT_TYPE, "article")
        putString(FirebaseAnalytics.Param.ITEM_NAME, title.take(MAX_TEXT_LENGTH))
    }

    private companion object {
        const val MAX_TEXT_LENGTH = 100
    }
}
