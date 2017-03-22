package com.github.nadavwr.mempool

import scala.scalanative.native._

private[mempool] trait PoolAllocator {
  def malloc(size: CSize): Ptr[Byte]
  def free(addr: Ptr[Byte]): Unit
}
