
package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class FrenchMangaProvider : MainAPI() {
    override var mainUrl = "https://w16.french-manga.net"
    override var name = "FrenchManga"
    override val supportedTypes = setOf(TvType.Anime, TvType.AnimeMovie)
    override var lang = "fr"

    override suspend fun search(query: String): ArrayList<SearchResponse> {
        val link = "$mainUrl/?s=$query"
        val html = app.get(link).document
        val results = ArrayList<SearchResponse>()

        html.select("div.item").forEach { element ->
            val title = element.selectFirst("a")?.attr("title") ?: return@forEach
            val url = element.selectFirst("a")?.attr("href") ?: return@forEach
            val poster = element.selectFirst("img")?.attr("src")

            results.add(
                NewAnimeSearchResponse(
                    title,
                    url,
                    TvType.Anime
                ) {
                    this.posterUrl = poster
                }
            )
        }
        return results
    }

    override suspend fun load(url: String): LoadResponse? {
        val html = app.get(url).document

        val title = html.selectFirst("h1")?.text() ?: "Anime"
        val poster = html.selectFirst(".poster img")?.attr("src")
        val description = html.selectFirst(".description")?.text()

        val episodes = html.select("ul.episodios li a").mapNotNull { element ->
            val epUrl = element.attr("href")
            val name = element.text()
            Episode(epUrl, name = name)
        }

        return NewAnimeLoadResponse(title, url, TvType.Anime, episodes) {
            this.posterUrl = poster
            this.plot = description
        }
    }
}
