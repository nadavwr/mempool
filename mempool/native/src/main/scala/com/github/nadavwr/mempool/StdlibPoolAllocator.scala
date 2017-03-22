package com.github.nadavwr.mempool

import scala.scalanative.native._

private[mempool] object StdlibPoolAllocator extends PoolAllocator {
  override def malloc(size: CSize): Ptr[Byte] = stdlib.malloc(size)
  override def free(ptr: Ptr[Byte]): Unit = stdlib.free(ptr)
}

