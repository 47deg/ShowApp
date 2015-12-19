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
