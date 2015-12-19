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