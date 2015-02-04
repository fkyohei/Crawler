package com.example

import org.jsoup._
import collection.JavaConverters._
import java.net.{URI, URLDecoder, URLEncoder}

object Crawler_1{

    def getSource(url: String): Unit = {

        val doc = Jsoup.connect(url).get

        // 企業名とURLをセットで入れるリスト
        var CompanyList: List[(String, String)] = Nil
        // 企業名とURL用変数
        var CompanyName: String = null
        var CompanyUrl: String = null

        // 1企業分のhtmlをセットにして取得
        val Companys = doc.select(".listBox")

        val CompanyIterator = Companys.iterator().asScala

        CompanyIterator.foreach {
            value => {
                CompanyName = value.select(".ttl .companyName h3 a").text
                CompanyUrl = value.select(".ttl > .companyName > h3 > a").attr("href")
                // タプルで値を持つリストとして後ろに追加
                CompanyList = CompanyList :+ (CompanyName, CompanyUrl)
            }
        }

        // 出力
        for( CompanyDetail <- CompanyList) {
            println(CompanyDetail._1 +" : " +CompanyDetail._2)
        }
    }

     def main(args: Array[String]) = {
          val url = "http://jobtalk.jp/company/index_1.html"
          getSource(url)
    }
}