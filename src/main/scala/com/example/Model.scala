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