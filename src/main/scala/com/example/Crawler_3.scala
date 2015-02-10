package com.example

import org.jsoup._
import collection.JavaConverters._
import java.net.{URI, URLDecoder, URLEncoder}
import java.sql.{DriverManager, Connection, Statement, ResultSet,SQLException}

/**
 * Crawler_3
 * 指定URLをクロール
 * さらに詳細ページのデータもクロールしてDBに保存
 */
object Crawler_3{

    /**
     * 詳細ページのデータを取得
     */
    def getDetailInfo(url: String) = {

        val doc = Jsoup.connect(url).get

        // 給与水準
        var PayLevelStar: String = null
        // 安定性
        var StabilityStar: String = null
        // 成長性
        var GrowthStar: String = null
        // やりがい
        var WorthwhileStar: String = null
        // 理念
        var IdeaStar: String = null
        // ブランド
        var BrandStar: String = null
        // 雰囲気
        var AtmosphereStar: String = null
        // 入社難易度
        var EntranceStar: String = null
        // 福利厚生
        var WelfareStar: String = null
        // 教育
        var TrainingStar: String = null

        // 詳細データを取得
        val CompanyDetail = doc.select("#companyReport dl")

        val CompanyIterator = CompanyDetail.iterator().asScala

        // 必要なデータだけ置き換え
        CompanyIterator.foreach {
            value => {
                value.select("dt span").text() match {
                    case paylevelstar if paylevelstar == "給与水準" => PayLevelStar = value.select("dd span em").text()
                    case stabilitystarstar if stabilitystarstar == "企業の安定性" => StabilityStar = value.select("dd span em").text()
                    case growthstar if growthstar == "企業の成長性、将来性" => GrowthStar = value.select("dd span em").text()
                    case worthwhilestar if worthwhilestar == "仕事のやりがい" => WorthwhileStar = value.select("dd span em").text()
                    case ideastar if ideastar == "企業の理念と浸透性" => IdeaStar = value.select("dd span em").text()
                    case brandstar if brandstar == "ブランドイメージ" => BrandStar = value.select("dd span em").text()
                    case atmospherestar if atmospherestar == "社員の雰囲気" => AtmosphereStar = value.select("dd span em").text()
                    case entrancestar if entrancestar == "入社難易度" => EntranceStar = value.select("dd span em").text()
                    case welfarestar if welfarestar == "福利厚生" => WelfareStar = value.select("dd span em").text()
                    case trainingstar if trainingstar == "教育、研修制度" => TrainingStar = value.select("dd span em").text()
                    case _ => ""  // 当てはまらなかった場合何もしない
                }
            }
        }

        // 取得した詳細データ
        (PayLevelStar, StabilityStar, GrowthStar, WorthwhileStar, IdeaStar, BrandStar, AtmosphereStar, EntranceStar, WelfareStar, TrainingStar)

    }

    /**
     * リストページの情報を取得
     */
    def getSource(url: String): Unit = {
        try{

            Class.forName("com.mysql.jdbc.Driver").newInstance()
            var con = DriverManager.getConnection("jdbc:mysql://localhost/Company?" + "user=root&password=")

            try {
                val doc = Jsoup.connect(url).get

                // リスト[企業名, 詳細URL, スター, エリア, 企業URL, 給与水準, 安定性, 成長性, やりがい, 理念, ブランド, 雰囲気, 入社難易度, 福利厚生, 研修制度]
                var CompanyList: List[(String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)] = Nil
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
                        // １秒間のアクセスが多くならないよう意図的に２秒待機させる
                        Thread.sleep(2000);
                        var CompanyDetailInfo = getDetailInfo(CompanyDetailUrl)
                        // タプルで値を持つリストとして後ろに追加
                        CompanyList = CompanyList :+ (CompanyName, CompanyDetailUrl, CompanyStar
                                                      , CompanyArea, CompanyUrl, CompanyDetailInfo._1
                                                      , CompanyDetailInfo._2, CompanyDetailInfo._3, CompanyDetailInfo._4
                                                      , CompanyDetailInfo._5, CompanyDetailInfo._6, CompanyDetailInfo._7
                                                      , CompanyDetailInfo._8, CompanyDetailInfo._9, CompanyDetailInfo._10)
                    }
                }

                // バルクインサートでDBに保存
                var stmt = con.createStatement()
                val BaseSql = "INSERT INTO Company.CompanyDetailInfo (CompanyName, CompanyDetailUrl, CompanyStar, CompanyArea, CompanyUrl, PayLevelStar, StabilityStar, GrowthStar, WorthwhileStar, IdeaStar, BrandStar, AtmosphereStar, EntranceStar, WelfareStar, TrainingStar)"
                var InsertSql: String = null
                var boolSql: Boolean = false

                for( CompanyDetail <- CompanyList) {
                    if( !boolSql) {
                        InsertSql = " Values('" + CompanyDetail._1 + "', '" + CompanyDetail._2 + "', '" + CompanyDetail._3 + "', '" + CompanyDetail._4 + "', '" + CompanyDetail._5 + "', '" + CompanyDetail._6 + "', '" + CompanyDetail._7 + "', '" + CompanyDetail._8 + "', '" + CompanyDetail._9 + "', '" + CompanyDetail._10 + "', '" + CompanyDetail._11 + "', '" + CompanyDetail._12 + "', '" + CompanyDetail._13 + "', '" + CompanyDetail._14 + "', '" + CompanyDetail._15 + "')"
                        boolSql = true
                    } else {
                        InsertSql += " ,('" + CompanyDetail._1 + "', '" + CompanyDetail._2 + "', '" + CompanyDetail._3 + "', '" + CompanyDetail._4 + "', '" + CompanyDetail._5 + "', '" + CompanyDetail._6 + "', '" + CompanyDetail._7 + "', '" + CompanyDetail._8 + "', '" + CompanyDetail._9 + "', '" + CompanyDetail._10 + "', '" + CompanyDetail._11 + "', '" + CompanyDetail._12 + "', '" + CompanyDetail._13 + "', '" + CompanyDetail._14 + "', '" + CompanyDetail._15 + "')"
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