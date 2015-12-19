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
