package com.github.nadavwr

import scala.scalanative.native._
import scala.scalanative.runtime.GC

package object mempool {

  type PoolStruct = CStruct4[CBool, CBool, CSize, Ptr[Byte]]
  type Pool = Ptr[PoolStruct]

  implicit class PoolOps(val pool: Pool) extends AnyVal {

    def useGc: CBool = !pool._1
    private[mempool] def useGc_=(useGc: CBool): Unit = !pool._1 = useGc

    def useHeap: CBool = !pool._2
    private[mempool] def useHeap_=(useHeap: CBool): Unit = !pool._2 = useHeap

    private def allocator: PoolAllocator =
      if (useGc) GcPoolAllocator else StdlibPoolAllocator

    def available: CSize = !pool._3
    private[mempool] def available_=(size: CSize): Unit = !pool._3 = size

    private[mempool] def tail: Ptr[Byte] = !pool._4
    private[mempool] def tail_=(tail: Ptr[Byte]): Unit = !pool._4 = tail

    def dispose(): Unit = {
      require(!useHeap)
      allocator.free(pool.cast[Ptr[Byte]])
    }

    def malloc[A : Sized]: Ptr[A] = malloc[A](1)

    def malloc[A : Sized](n: CSize): Ptr[A] = {
      val size = n * Sized.sizeof[A]
      require(available >= size)
      val newInstance =
        if (useHeap) GC.malloc(size)
        else {
          val ptr = tail
          tail += size
          ptr
        }
      available -= size
      newInstance.cast[Ptr[A]]
    }
  }

  object Pool {
    lazy val defaultHeap: Pool = heap(Long.MaxValue)

    def heap(size: CSize): Pool = {
      val allocator = GcPoolAllocator
      val poolStructSize = sizeof[PoolStruct]
      val poolBytes: Ptr[Byte] = allocator.malloc(poolStructSize)
      val pool: Ptr[PoolStruct] = poolBytes.cast[Ptr[PoolStruct]]
      pool.useHeap = true
      pool.available = size
      pool.tail = 0.cast[Ptr[Byte]]
      pool
    }

    def managed(size: CSize): Pool = {
      val allocator = GcPoolAllocator
      val poolStructSize = sizeof[PoolStruct]
      val poolBytes: Ptr[Byte] = allocator.malloc(poolStructSize + size)
      val pool: Ptr[PoolStruct] = poolBytes.cast[Ptr[PoolStruct]]
      pool.useHeap = false
      pool.useGc = true
      pool.available = size
      pool.tail = (pool + 1).cast[Ptr[Byte]]
      pool
    }

    def apply(size: CSize): Pool = {
      val allocator = StdlibPoolAllocator
      val poolStructSize = sizeof[PoolStruct]
      val poolBytes: Ptr[Byte] = allocator.malloc(poolStructSize + size)
      val pool: Ptr[PoolStruct] = poolBytes.cast[Ptr[PoolStruct]]
      pool.useHeap = false
      pool.useGc = false
      pool.available = size
      pool.tail = (pool + 1).cast[Ptr[Byte]]
      pool
    }
  }
}
