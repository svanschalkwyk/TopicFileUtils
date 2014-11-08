package net.remcam.util

/**
 * Created by svanschalkwyk on 11/6/14.
 */
object Control {

  def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B =
    try {
      f(resource)
    } finally {
      resource.close()
    }
}
