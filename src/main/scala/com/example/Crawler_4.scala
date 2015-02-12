package com.example

import org.jsoup._
import collection.JavaConverters._
import java.net.{URI, URLDecoder, URLEncoder}
import java.sql.{DriverManager, Connection, Statement, ResultSet,SQLException}
import scala.util.matching.Regex

/**
 * Crawler_4
 * 指定URLをクロール
 * さらに詳細ページのデータもクロールしてDBに保存
 */
object Crawler_4{

    /**
     * 会社情報ページのデータを取得
     */
    def getDetailInfo(url: String) = {

        val doc = Jsoup.connect(url).get

        // 電話番号
        var Tel: String = null
        // FAX
        var Fax: String = null
        // 代表者名
        var Representative: String = null
        // 業界
        var Industry: String = null
        // 資本金
        var Capital: String = null
        // 決算月
        var Settlement: String = null
        // 従業員数
        var Employee: String = null
        // 設立年月日
        var Establish: String = null

        // 詳細データを取得
        val CompanyDetail = doc.select(".grayTable tbody tr")

        val CompanyIterator = CompanyDetail.iterator().asScala

        // 必要なデータだけ置き換え
        CompanyIterator.foreach {
            value => {
                value.select("th").text() match {
                    case tel if tel == "電話番号" => Tel = value.select("td").text()
                    case fax if fax == "FAX" => Fax = value.select("td").text()
                    case representative if representative == "代表者名" => Representative = value.select("td").text()
                    case industry if industry == "業界" => Industry = value.select("td").text()
                    case capital if capital == "資本金" => Capital = value.select("td").text()
                    case settlement if settlement == "決算月" => Settlement = value.select("td").text()
                    case employee if employee == "従業員数 (アルバイトを含む)" => Employee = value.select("td").text()
                    case establish if establish == "設立年月日" => Establish = value.select("td").text()
                    case _ => ""  // 当てはまらなかった場合何もしない
                }
            }
        }

        // 取得した詳細データ
        (Tel, Fax, Representative, Industry, Capital, Settlement, Employee, Establish)

    }

    /**
     * 詳細ページの評価データを取得
     */
    def getStarInfo(url: String) = {

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

                // リスト[企業ID, 企業名, 詳細URL,  エリア, 企業URL, 電話番号, FAX, 代表者名, 業界, 資本金, 決算月, 従業員数, 設立年月日]
                var CompanyInfoList: List[(String, String, String, String, String, String, String, String, String, String, String, String, String)] = Nil
                // リスト[企業ID, 総合評価, 給与水準, 安定性, 成長性, やりがい, 理念, ブランド, 雰囲気, 入社難易度, 福利厚生, 研修制度]
                var CompanyStarList: List[(String, String, String, String, String, String, String, String, String, String, String, String)] = Nil
                // 企業ID取得用正規表現
                val CompanyIdRegex: Regex = "([0-9]+)".r
                // 企業ID
                var CompanyId: String = null
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
                // 企業infoページurl
                var CompanyInfoUrl: String = null

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
                        CompanyInfoUrl = value.select(".nav li:last-child a").attr("abs:href")
                        // １秒間のアクセスが多くならないよう意図的に１秒ずつ待機させる
                        Thread.sleep(1000)
                        var CompanyStarInfo = getStarInfo(CompanyDetailUrl)
                        Thread.sleep(1000)
                        var CompanyDetailInfo = getDetailInfo(CompanyInfoUrl)

                        // 企業IDを収得する
                        CompanyId = CompanyIdRegex.findFirstMatchIn(CompanyDetailUrl).get.toString

                        // タプルで値を持つリストとして後ろに追加
                        CompanyInfoList = CompanyInfoList :+ (CompanyId, CompanyName, CompanyDetailUrl, CompanyArea, CompanyUrl, CompanyDetailInfo._1, CompanyDetailInfo._2
                                                              , CompanyDetailInfo._3, CompanyDetailInfo._4, CompanyDetailInfo._5, CompanyDetailInfo._6, CompanyDetailInfo._7, CompanyDetailInfo._8)

                        // タプルで値を持つリストとして後ろに追加
                        CompanyStarList = CompanyStarList :+ (CompanyId, CompanyStar, CompanyStarInfo._1, CompanyStarInfo._2, CompanyStarInfo._3
                                                              , CompanyStarInfo._4, CompanyStarInfo._5, CompanyStarInfo._6, CompanyStarInfo._7
                                                              , CompanyStarInfo._8, CompanyStarInfo._9, CompanyStarInfo._10)
                    }
                }

                // バルクインサートでDBに保存
                var stmt = con.createStatement()
                var BaseSql = "INSERT INTO Company.CompanyDetailInfo2 (Id, CompanyName, CompanyDetailUrl, CompanyArea, CompanyUrl, Tel, Fax, Representative, Industry, Capital, Settlement, Employee, Establish)"
                var InsertSql: String = null
                var boolSql: Boolean = false

                for( CompanyDetail <- CompanyInfoList) {
                    if( !boolSql) {
                        InsertSql = " Values('" + CompanyDetail._1 + "', '" + CompanyDetail._2 + "', '" + CompanyDetail._3 + "', '" + CompanyDetail._4 + "', '" + CompanyDetail._5 + "', '" + CompanyDetail._6 + "', '" + CompanyDetail._7 + "', '" + CompanyDetail._8 + "', '" + CompanyDetail._9 + "', '" + CompanyDetail._10 + "', '" + CompanyDetail._11 + "', '" + CompanyDetail._12 + "', '" + CompanyDetail._13 + "' )"
                        boolSql = true
                    } else {
                        InsertSql += " ,('" + CompanyDetail._1 + "', '" + CompanyDetail._2 + "', '" + CompanyDetail._3 + "', '" + CompanyDetail._4 + "', '" + CompanyDetail._5 + "', '" + CompanyDetail._6 + "', '" + CompanyDetail._7 + "', '" + CompanyDetail._8 + "', '" + CompanyDetail._9 + "', '" + CompanyDetail._10 + "', '" + CompanyDetail._11 + "', '" + CompanyDetail._12 + "', '" + CompanyDetail._13 + "' )"
                    }
                }

                var rs = stmt.executeUpdate(BaseSql + InsertSql)

                var BaseSql2 = "INSERT INTO Company.CompanyStarInfo (Id, CompanyStar, PayLevelStar, StabilityStar, GrowthStar, WorthwhileStar, IdeaStar, BrandStar, AtmosphereStar, EntranceStar, WelfareStar, TrainingStar)"
                var InsertSql2: String = null
                var boolSql2: Boolean = false

                for( CompanyDetail <- CompanyStarList) {
                    if( !boolSql2) {
                        InsertSql2 = " Values('" + CompanyDetail._1 + "', '" + CompanyDetail._2 + "', '" + CompanyDetail._3 + "', '" + CompanyDetail._4 + "', '" + CompanyDetail._5 + "', '" + CompanyDetail._6 + "', '" + CompanyDetail._7 + "', '" + CompanyDetail._8 + "', '" + CompanyDetail._9 + "', '" + CompanyDetail._10 + "', '" + CompanyDetail._11 + "', '" + CompanyDetail._12 + "' )"
                        boolSql2 = true
                    } else {
                        InsertSql2 += " ,('" + CompanyDetail._1 + "', '" + CompanyDetail._2 + "', '" + CompanyDetail._3 + "', '" + CompanyDetail._4 + "', '" + CompanyDetail._5 + "', '" + CompanyDetail._6 + "', '" + CompanyDetail._7 + "', '" + CompanyDetail._8 + "', '" + CompanyDetail._9 + "', '" + CompanyDetail._10 + "', '" + CompanyDetail._11 + "', '" + CompanyDetail._12 + "' )"
                    }
                }

                var rs2 = stmt.executeUpdate(BaseSql2 + InsertSql2)

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