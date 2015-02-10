package com.example

import org.jsoup._
import collection.JavaConverters._
import java.net.{URI, URLDecoder, URLEncoder}
import java.sql.{DriverManager, Connection, Statement, ResultSet,SQLException}

/**
 * Crawler_2
 * 指定URLをクロールしてDBに保存
 */
object Crawler_2{

    def getSource(url: String): Unit = {
        try{

            Class.forName("com.mysql.jdbc.Driver").newInstance()
            var con = DriverManager.getConnection("jdbc:mysql://localhost/Company?" + "user=root&password=")

            try {
                val doc = Jsoup.connect(url).get

                // リスト[企業名, 詳細URL, スター, エリア, 企業URL]
                var CompanyList: List[(String, String, String, String, String)] = Nil
                // 企業名
                var CompanyName: String = null
                // 詳細URL
                var CompanyDetailUrl: String = null
                // スター
                var CompanyStar: String = null
                // エリア
                var CompanyArea: String = null
                // 企業URL
                var CompanyUrl: String = null

                // 1企業分のhtmlをセットにして取得
                val Companys = doc.select(".listBox")

                val CompanyIterator = Companys.iterator().asScala

                CompanyIterator.foreach {
                    value => {
                        CompanyName = value.select(".ttl .companyName h3 a").text()
                        CompanyDetailUrl = value.select(".ttl .companyName h3 a").attr("abs:href")
                        CompanyStar = value.select(".txt em").text()
                        CompanyArea = value.select(".txt .data .area").text()
                        CompanyUrl = value.select(".txt .data .url").text()
                        // タプルで値を持つリストとして後ろに追加
                        CompanyList = CompanyList :+ (CompanyName, CompanyDetailUrl, CompanyStar, CompanyArea, CompanyUrl)
                    }
                }

                // バルクインサートでDBに保存
                var stmt = con.createStatement()
                val BaseSql = "INSERT INTO Company.CompanyInfo (CompanyName, CompanyDetailUrl, CompanyStar, CompanyArea, CompanyUrl)"
                var InsertSql: String = null
                var boolSql: Boolean = false
                for( CompanyDetail <- CompanyList) {
                    if( !boolSql) {
                        InsertSql = " Values('" + CompanyDetail._1 + "', '" + CompanyDetail._2 + "', '" + CompanyDetail._3 + "', '" + CompanyDetail._4 + "', '" + CompanyDetail._5 + "')"
                        boolSql = true
                    } else {
                        InsertSql = InsertSql + " ,('" + CompanyDetail._1 + "', '" +CompanyDetail._2 + "', '" + CompanyDetail._3 + "', '" + CompanyDetail._4 + "', '" + CompanyDetail._5 + "')"
                    }
                }
                var rs = stmt.executeUpdate(BaseSql + InsertSql)
                stmt.close()


            } catch {
                case e:SQLException => println("Database error "+e)
                case e:Throwable => {
                   println("Some other exception type:")
                   e.printStackTrace()
                }

            } finally {
                con.close()
            }

        } catch {
            case e:SQLException => println("Database error "+e)
            case e:Throwable => {
                println("Some other exception type:")
                e.printStackTrace()
            }
        }
    }

     def main(args: Array[String]) = {
          val url = "http://jobtalk.jp/company/index_1.html"
          getSource(url)
    }
}