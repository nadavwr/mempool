package com.github.nadavwr.mempool

import scala.scalanative.runtime.GC
import scalanative.native._

private[mempool] object GcPoolAllocator extends PoolAllocator {
  @link("gc")
  @extern
  private object impl {
    @name("GC_free")
    def free(ptr: Ptr[Byte]): Unit = extern
  }

  override def malloc(size: CSize): Ptr[CSignedChar] = GC.malloc(size)

  override def free(ptr: Ptr[CSignedChar]): Unit = impl.free(ptr)
}
