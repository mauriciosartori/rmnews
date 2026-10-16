package com.sartori.rmnews.analytics

/** User interactions, independent of the analytics provider. */
interface NewsAnalytics {
    fun trackSearch(query: String)
    fun trackArticleOpened(title: String)
    fun trackRefresh()
    fun trackFullArticleClicked(title: String)
}
