package com.example

import com.example.Model._
import org.joda.time.DateTime
import org.scalacheck.{Gen, Arbitrary}

/**
  * More fun with Property testing arbitrary instances and generators
  * There is no point to generate your own test data if you are using ScalaCheck
  */
trait ArbitraryInstances {

  import Arbitrary._

  implicit def arbNonEmptyList[A : Arbitrary]: Arbitrary[List[A]] =
    Arbitrary(Gen.nonEmptyListOf(arbitrary[A]))

  implicit def arbDateTime: Arbitrary[DateTime] =
    Arbitrary(Gen.oneOf(
      DateTime.now(),
      DateTime.now().plusDays(1),
      DateTime.now().minusDays(1)))

  implicit def arbVideoType: Arbitrary[VideoType] =
    Arbitrary(Gen.oneOf(Movie, FullEpisode, Clip))

  implicit def arbVideo: Arbitrary[Video] =
    Arbitrary(for {
      id <- arbitrary[Id]
      name <- arbitrary[String]
      url <- arbitrary[URL]
      videoType <- arbitrary[VideoType]
      expiresOn <- arbitrary[DateTime]
    } yield Video(id, name, url, videoType, expiresOn))

  implicit def arbImage: Arbitrary[Image] =
    Arbitrary(for {
      id <- arbitrary[Id]
      name <- arbitrary[String]
      url <- arbitrary[URL]
      expiresOn <- arbitrary[DateTime]
    } yield Image(id, name, url, expiresOn))

  implicit def arbVideoAd: Arbitrary[VideoAd] =
    Arbitrary(for {
      id <- arbitrary[Id]
      name <- arbitrary[String]
      url <- arbitrary[URL]
      productDescription <- arbitrary[String]
      expiresOn <- arbitrary[DateTime]
    } yield VideoAd(id, name, url, productDescription, expiresOn))

  implicit def arbShowContainer: Arbitrary[ShowContainer] =
    Arbitrary(for {
      id <- arbitrary[Id]
      name <- arbitrary[String]
      videos <- arbitrary[List[Video]]
      images <- arbitrary[List[Image]]
      videoAds <- arbitrary[List[VideoAd]]
    } yield ShowContainer(id, name, videos, images, videoAds))

}

object ArbitraryInstances extends ArbitraryInstances