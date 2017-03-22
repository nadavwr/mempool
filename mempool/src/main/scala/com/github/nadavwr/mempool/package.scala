package com.github.nadavwr

import scala.scalanative.native._

package object mempool {

  type PoolStruct = CStruct3[CBool, CSize, Ptr[Byte]]
  type Pool = Ptr[PoolStruct]

  implicit class PoolOps(val pool: Pool) extends AnyVal {

    def useGc: CBool = !pool._1
    private[mempool] def useGc_=(useGc: CBool): Unit = !pool._1 = useGc

    private def allocator: PoolAllocator =
      if (useGc) GcPoolAllocator else StdlibPoolAllocator

    def available: CSize = !pool._2
    private[mempool] def available_=(size: CSize): Unit = !pool._2 = size

    private[mempool] def tail: Ptr[Byte] = !pool._3
    private[mempool] def tail_=(tail: Ptr[Byte]): Unit = !pool._3 = tail

    def dispose(): Unit = allocator.free(pool.cast[Ptr[Byte]])

    def malloc[A : Sized]: Ptr[A] = malloc[A](1)

    def malloc[A : Sized](n: CSize): Ptr[A] = {
      val size = n * Sized.sizeof[A]
      require(available >= size)
      val ptr = tail
      available -= size
      tail += size
      ptr.cast[Ptr[A]]
    }
  }


  object Pool {
    def apply(size: CSize, useGc: CBool = false): Pool = {
      val allocator = if (useGc) GcPoolAllocator else StdlibPoolAllocator
      val poolStructSize = sizeof[PoolStruct]
      val poolBytes: Ptr[Byte] = allocator.malloc(poolStructSize + size)
      val pool: Ptr[PoolStruct] = poolBytes.cast[Ptr[PoolStruct]]
      pool.available = size
      pool.tail = (pool + 1).cast[Ptr[Byte]]
      pool
    }
  }
}
