package com.github.nadavwr.mempool.sample

import com.github.nadavwr.mempool._
import scala.scalanative.native._

object Sample extends App {
  implicit val pool = Pool(1024)
  type N = CStruct6[CInt, CInt, CInt, CInt, CInt, CInt]
  implicit val sizedN: Sized[N] = Sized.instance[N](sizeof[N])

  def add(a: Ptr[N], b: Ptr[N])(implicit pool: Pool): Ptr[N] = {
    val result: Ptr[N] = pool.malloc[N]
    !result._1 = !a._1 + !b._1
    !result._2 = !a._2 + !b._2
    !result._3 = !a._3 + !b._3
    !result._4 = !a._4 + !b._4
    !result._5 = !a._5 + !b._5
    !result._6 = !a._6 + !b._6
    result
  }

  val a = stackalloc[N]
  !a._1 = 1
  !a._2 = 2
  !a._3 = 3
  !a._4 = 4
  !a._5 = 5
  !a._6 = 6

  val b = stackalloc[N]
  !b._1 = 1
  !b._2 = 2
  !b._3 = 3
  !b._4 = 4
  !b._5 = 5
  !b._6 = 6



  println("pool.available: " + pool.available)
  val c = add(a, b)
  println("pool.available: " + pool.available)
  println(sizeof[N])

}
