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