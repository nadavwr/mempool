package com.github.nadavwr.mempool

import scala.scalanative.native
import scala.scalanative.native._

trait Sized[A] {
  def size: CSize
}

object Sized {
  def instance[A](size: CSize): Sized[A] = {
    val _size = size
    new Sized[A] {
      override def size: CSize = _size
    }
  }

  def sizeof[A : Sized]: CSize = implicitly[Sized[A]].size

  implicit lazy val byteSized: Sized[Byte] = Sized.instance[Byte](native.sizeof[Byte])
  implicit lazy val PtrSized: Sized[Ptr[_]] = Sized.instance[Ptr[_]](native.sizeof[Ptr[_]])
}
