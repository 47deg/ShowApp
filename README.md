# Run app:

```bash 
./activator run
```

# Run tests:
```bash
./activator test
```

# Contents

## Model.scala

Sample model given the project description using ADTs.

```scala
package com.example

import org.joda.time.DateTime

object Model {

  type Id = Long

  type URL = String //java.net.URL ommited for simplicity

  /**
    * You have information on videos for a show, a list of images associated with a show,
    * and a list of video ads associated with a show all considered "assets".
    * Each asset has an ID, a name, a type indicator, a URL, and an expiration date.
    */
  sealed trait Asset {
    def id: Id
    def name: String
    def url: URL
    def expiresOn: DateTime
  }

  /**
    * Videos have a field that indicates if it is a movie, a full episode or a clip.
    */
  sealed trait VideoType
  case object Movie extends VideoType
  case object FullEpisode extends VideoType
  case object Clip extends VideoType

  final case class Video(
      id: Id,
      name: String,
      url: URL,
      videoType: VideoType,
      expiresOn: DateTime) extends Asset

  /**
    * Image assets can be represented by a base asset.
    */
  final case class Image(
      id: Id,
      name: String,
      url: URL,
      expiresOn: DateTime) extends Asset

  /**
    * Ad assets include a field for a product description.
    */
  final case class VideoAd(
      id: Id,
      name: String,
      url: URL,
      productDescription: String,
      expiresOn: DateTime) extends Asset
  /**
    * Containers describe a collection of assets.
    * Containers can be considered a "show" with information that includes an ID, name, description, and assets.
    */
  final case class ShowContainer(
      id: Id,
      name: String,
      videos: List[Video] = Nil,
      images: List[Image] = Nil,
      videoAd: List[VideoAd] = Nil) {

    def assets: List[Asset] = videos ++ images ++ videoAd

  }

}
```

## PureApp.scala

A purely functional approach using cats Show instances

```scala
package com.example

import cats.Show
import com.example.Model._

/**
  * Pure FP SCALA VERSION with cats.Show typeclass instances.
  * https://github.com/non/cats/blob/master/core/src/main/scala/cats/Show.scala
  *
  * Create a program that generates at least one container with many assets (at least one of each type) with all properties set.
  * The program should visit each asset and print information about that asset specific to the type of asset it is.
  *
  * Notes:
  * Type class instances support ad hoc overridable polymorphism without hierarchy restrictions via implicit resolution.
  * Compiler guarantees correctness if missing instances at compile time.
  */
trait ShowInstances {

  implicit val videoShowInstance: Show[Video] = new Show[Video] {
    override def show(f: Video): String = s"Found video ${f.videoType} with ${f.name}"
  }

  implicit val imageShowInstance: Show[Image] = new Show[Image] {
    override def show(f: Image): String = s"Found image with ${f.name}"
  }

  implicit val videoAdShowInstance: Show[VideoAd] = new Show[VideoAd] {
    override def show(f: VideoAd): String = s"Found ad with ${f.name} and product description: ${f.productDescription}"
  }

  implicit def assetShowInstance(
      implicit SV: Show[Video],
      SI: Show[Image],
      SA: Show[VideoAd]
  ): Show[Asset] = new Show[Asset] {
    override def show(f: Asset): String = f match {
      case a: Video => SV.show(a)
      case a: Image => SI.show(a)
      case a: VideoAd => SA.show(a)
    }
  }

  implicit def showContainerShowInstance(implicit SV: Show[Asset]): Show[ShowContainer] =
    new Show[ShowContainer] {
      override def show(f: ShowContainer): String =
        s"""
           | Show metadata for ${f.name} is :
           |   ${f.assets.map(a => SV.show(a)).mkString("\n   ")}
           |
         """.stripMargin
    }

}

object PureApp extends ShowInstances {

  import SampleData._

  def show(implicit SS : Show[ShowContainer]): String = SS.show(showContainer)

  def main(args: Array[String]): Unit = println(show)

}
```

SampleData.scala

```scala
package com.example

import com.example.Model._
import org.joda.time.DateTime

object SampleData {

  /**
    * Sample data
    */
  val showContainer = ShowContainer(
    id = 3452345345L,
    name = "Vikings",
    videos = List(
      Video(0L, "Ragnar Lothbrok meets Princess Aslaug", "http://vk.io/test.mp4", Clip, DateTime.now()),
      Video(1L, "Rollo fights in Cumbria", "http://vk.io/test2.mp4", Movie, DateTime.now()),
      Video(2L, "Floki builds the towers", "http://vk.io/test3.mp4", FullEpisode, DateTime.now())),
    images = List(
      Image(3L, "Ragnar Lothbrok meets Princess Aslaug", "http://vk.io/test.jpg", DateTime.now()),
      Image(4L, "Rollo fights in Cumbria", "http://vk.io/test2.jpg", DateTime.now()),
      Image(5L, "Floki builds the towers", "http://vk.io/test3.jpg", DateTime.now())),
    videoAd = List(
      VideoAd(6L, "BestBuy Ad", "http://vk.io/test.jpg", "Watch for free this season", DateTime.now()),
      VideoAd(7L, "Wallmart Ad", "http://vk.io/test2.jpg", "Watch for free this season", DateTime.now()),
      VideoAd(8L, "Veracci Add", "http://vk.io/test3.jpg", "Watch for free this season", DateTime.now())))

  /**
    * Return a list of descriptions for each asset contained in a ShowContainer
    */
  def descriptionLines(f :ShowContainer): List[String] = f.assets map {
    case Video(_, name, _, videoType, _) => s"Found video $videoType with $name"
    case Image(_, name, _, _) => s"Found image with $name"
    case VideoAd(_, name, _, productDescription, _) => s"Found ad with $name and product description: $productDescription"
  }

}
```

## VanillaScalaApp.scala

A Vanilla Scala approach using side effects and simple println

```scala
package com.example

/**
  * VANILLA SCALA VERSION
  * Create a program that generates at least one container with many assets (at least one of each type) with all properties set.
  * The program should visit each asset and print information about that asset specific to the type of asset it is.
  */
object VanillaApp {

  import SampleData._

  def run(): Unit = descriptionLines(showContainer) foreach println

  def main(args: Array[String]): Unit = run()

}

```

## ArbitraryInstances.scala

Arbitrary scala check instances that generate data for property checking

```scala
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
```

## ShowContainerSpecification.scala

A very simple test that uses arbitrary instances.

```scala
package com.example

import com.example.Model.{VideoAd, Image, Video, ShowContainer}
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class ShowContainerSpecification
    extends Specification
        with ScalaCheck
        with ArbitraryInstances {

  "show container contains videos" >> prop { (a: ShowContainer) =>
    a.assets.exists(_.isInstanceOf[Video]) must_== true
  }.display(minTestsOk = 200, workers = 3)

  "show container contains images" >> prop { (a: ShowContainer) =>
    a.assets.exists(_.isInstanceOf[Image]) must_== true
  }.display(minTestsOk = 200, workers = 3)

  "show container contains ads" >> prop { (a: ShowContainer) =>
    a.assets.exists(_.isInstanceOf[VideoAd]) must_== true
  }.display(minTestsOk = 200, workers = 3)

}
```