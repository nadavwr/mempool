package com.github.nadavwr.mempool

import scalanative.native._

@link("gc")
@extern
private[mempool] object GcPoolAllocator extends PoolAllocator {
  @name("GC_malloc_atomic")
  override def malloc(size: CSize): Ptr[Byte] = extern
  @name("GC_free")
  override def free(ptr: Ptr[Byte]): Unit = extern
}
